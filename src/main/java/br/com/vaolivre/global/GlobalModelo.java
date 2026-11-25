package br.com.vaolivre.global;

import br.com.vaolivre.acesso.UsuarioLogado;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** O que toda tela precisa: quem está logado, onde está, a data de hoje e o modo revisão. */
@ControllerAdvice
public class GlobalModelo {

  private final Clock relogio;
  private final boolean modoRevisao;

  public GlobalModelo(Clock relogio, @Value("${app.modo-revisao}") boolean modoRevisao) {
    this.relogio = relogio;
    this.modoRevisao = modoRevisao;
  }

  @ModelAttribute("eu")
  public UsuarioLogado eu(@AuthenticationPrincipal UsuarioLogado usuario) {
    return usuario;
  }

  /** Primeiro trecho do caminho ("portaria", "clientes"...), para marcar o menu. */
  @ModelAttribute("secao")
  public String secao(HttpServletRequest pedido) {
    String[] partes = pedido.getRequestURI().substring(pedido.getContextPath().length()).split("/");
    return partes.length > 1 ? partes[1] : "";
  }

  @ModelAttribute("hoje")
  public String hoje() {
    return Formatos.data(LocalDate.now(relogio));
  }

  @ModelAttribute("modoRevisao")
  public boolean modoRevisao() {
    return modoRevisao;
  }
}
