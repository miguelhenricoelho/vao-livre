package br.com.vaolivre.auditoria;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AuditoriaController {

  private final AuditoriaService auditoria;

  public AuditoriaController(AuditoriaService auditoria) {
    this.auditoria = auditoria;
  }

  @GetMapping("/auditoria")
  public String auditoria(@RequestParam(defaultValue = "0") int pagina, Model modelo) {
    modelo.addAttribute("pagina", auditoria.listar(pagina));
    return "auditoria/auditoria";
  }
}
