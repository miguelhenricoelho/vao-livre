package br.com.vaolivre.global;

import br.com.vaolivre.acesso.UsuarioLogado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** O endereço raiz leva cada papel para a sua tela principal. */
@Controller
public class InicioController {

  @GetMapping("/")
  public String inicio(@AuthenticationPrincipal UsuarioLogado eu) {
    return eu != null && eu.isAdmin() ? "redirect:/painel" : "redirect:/portaria";
  }
}
