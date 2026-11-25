package br.com.vaolivre.ajuda;

import br.com.vaolivre.acesso.AcessoService;
import br.com.vaolivre.acesso.UsuarioLogado;
import br.com.vaolivre.whatsapp.MensagensWhatsApp;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AjudaController {

  private final AcessoService acesso;
  private final MensagensWhatsApp whatsapp;

  public AjudaController(AcessoService acesso, MensagensWhatsApp whatsapp) {
    this.acesso = acesso;
    this.whatsapp = whatsapp;
  }

  @GetMapping("/ajuda")
  public String ajuda(@AuthenticationPrincipal UsuarioLogado eu, Model modelo) {
    String numero = acesso.whatsappDoSuporte();
    modelo.addAttribute(
        "linkSuporte",
        numero == null ? null : whatsapp.pedidoDeAjuda(numero, eu.getNome(), "Portaria"));
    return "ajuda/ajuda";
  }
}
