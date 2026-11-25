package br.com.vaolivre.portaria;

import br.com.vaolivre.acesso.UsuarioLogado;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** A tela da portaria: registra e mostra o resultado que o service decidiu. */
@Controller
@RequestMapping("/portaria")
public class PortariaController {

  private static final String VIEW = "portaria/portaria";

  private final PortariaService portaria;
  private final PortariaConsultaService consulta;

  public PortariaController(PortariaService portaria, PortariaConsultaService consulta) {
    this.portaria = portaria;
    this.consulta = consulta;
  }

  @GetMapping
  public String portaria(@RequestParam(required = false) Long registro, Model modelo) {
    modelo.addAttribute("form", PortariaForm.vazio());
    if (registro != null) {
      modelo.addAttribute("resultado", consulta.resultado(registro));
    }
    modelo.addAttribute("patio", consulta.patio());
    return VIEW;
  }

  @PostMapping("/entrada")
  public String entrada(
      @Valid @ModelAttribute("form") PortariaForm form,
      BindingResult erros,
      @AuthenticationPrincipal UsuarioLogado eu,
      Model modelo) {
    if (erros.hasErrors()) {
      modelo.addAttribute("patio", consulta.patio());
      return VIEW;
    }
    return "redirect:/portaria?registro=" + portaria.registrarEntrada(form.placa(), eu.getId());
  }

  @PostMapping("/saida")
  public String saida(
      @Valid @ModelAttribute("form") PortariaForm form,
      BindingResult erros,
      @AuthenticationPrincipal UsuarioLogado eu,
      Model modelo) {
    if (erros.hasErrors()) {
      modelo.addAttribute("patio", consulta.patio());
      return VIEW;
    }
    return "redirect:/portaria?registro=" + portaria.registrarSaida(form.placa(), eu.getId());
  }
}
