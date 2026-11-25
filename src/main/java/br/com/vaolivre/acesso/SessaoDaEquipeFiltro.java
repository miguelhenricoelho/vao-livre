package br.com.vaolivre.acesso;

import br.com.vaolivre.equipe.Usuario;
import br.com.vaolivre.equipe.UsuariosRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Garante as duas senhas em todo pedido:
 *
 * <ol>
 *   <li>sem a senha da área na sessão (ou com uma versão antiga dela), volta para /entrar;
 *   <li>pessoa desativada depois do login perde a sessão na hora;
 *   <li>quem está com senha temporária só usa a tela de troca de senha.
 * </ol>
 */
public class SessaoDaEquipeFiltro extends OncePerRequestFilter {

  private static final Set<String> LIVRES = Set.of("/entrar", "/error");
  private static final Pattern ESTATICO = Pattern.compile("^/[a-z-]+/[\\w.-]+\\.(css|js|svg)$");
  private static final Set<String> SENHA_TEMPORARIA = Set.of("/conta/senha", "/sair");

  private final AcessoService acesso;
  private final UsuariosRepository usuarios;

  public SessaoDaEquipeFiltro(AcessoService acesso, UsuariosRepository usuarios) {
    this.acesso = acesso;
    this.usuarios = usuarios;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest pedido) {
    String caminho = caminho(pedido);
    return LIVRES.contains(caminho) || ESTATICO.matcher(caminho).matches();
  }

  /** Caminho sem o contexto da aplicação, igual no servidor de verdade e nos testes. */
  static String caminho(HttpServletRequest pedido) {
    return pedido.getRequestURI().substring(pedido.getContextPath().length());
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest pedido, HttpServletResponse resposta, FilterChain cadeia)
      throws ServletException, IOException {
    HttpSession sessao = pedido.getSession(false);
    Object versao = sessao == null ? null : sessao.getAttribute(AcessoService.ATRIBUTO_DA_SESSAO);
    if (!(versao instanceof Integer v) || v != acesso.versaoAtual()) {
      encerrar(pedido);
      resposta.sendRedirect(pedido.getContextPath() + "/entrar");
      return;
    }

    Optional<Long> logado = idLogado();
    if (logado.isPresent()) {
      Optional<Usuario> usuario = usuarios.findById(logado.get());
      if (usuario.isEmpty() || !usuario.get().isAtivo()) {
        encerrar(pedido);
        resposta.sendRedirect(pedido.getContextPath() + "/entrar?saiu");
        return;
      }
      if (usuario.get().isDeveTrocarSenha() && !SENHA_TEMPORARIA.contains(caminho(pedido))) {
        resposta.sendRedirect(pedido.getContextPath() + "/conta/senha");
        return;
      }
    }
    cadeia.doFilter(pedido, resposta);
  }

  private static Optional<Long> idLogado() {
    Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
    if (autenticacao != null && autenticacao.getPrincipal() instanceof UsuarioLogado eu) {
      return Optional.of(eu.getId());
    }
    return Optional.empty();
  }

  private static void encerrar(HttpServletRequest pedido) {
    SecurityContextHolder.clearContext();
    HttpSession sessao = pedido.getSession(false);
    if (sessao != null) {
      sessao.invalidate();
    }
  }
}
