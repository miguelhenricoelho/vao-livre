package br.com.vaolivre.acesso;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.web.filter.OncePerRequestFilter;

/** Limite de tentativas de login por IP (segunda camada). */
public class LimiteDeLoginFiltro extends OncePerRequestFilter {

  private final Limitador limite = new Limitador(10, Duration.ofMinutes(15));

  @Override
  protected boolean shouldNotFilter(HttpServletRequest pedido) {
    return !("POST".equals(pedido.getMethod())
        && "/login".equals(SessaoDaEquipeFiltro.caminho(pedido)));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest pedido, HttpServletResponse resposta, FilterChain cadeia)
      throws ServletException, IOException {
    if (!limite.tentar(pedido.getRemoteAddr())) {
      resposta.sendRedirect(pedido.getContextPath() + "/login?bloqueado");
      return;
    }
    cadeia.doFilter(pedido, resposta);
  }
}
