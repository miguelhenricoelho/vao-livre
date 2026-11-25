package br.com.vaolivre.painel;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class PainelController {

  private final PainelService painel;

  public PainelController(PainelService painel) {
    this.painel = painel;
  }

  @GetMapping("/painel")
  public String painel(Model modelo) {
    modelo.addAttribute("painel", painel.painel());
    return "painel/painel";
  }
}
