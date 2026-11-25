package br.com.vaolivre.conta;

import br.com.vaolivre.acesso.UsuarioLogado;
import br.com.vaolivre.global.CampoInvalidoException;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContaController {

  private static final String VIEW = "conta/conta";

  private final ContaService conta;

  public ContaController(ContaService conta) {
    this.conta = conta;
  }

  @GetMapping({"/conta", "/conta/senha"})
  public String conta(Model modelo) {
    modelo.addAttribute("form", TrocaSenhaForm.vazio());
    return VIEW;
  }

  @PostMapping("/conta/senha")
  public String trocar(
      @Valid @ModelAttribute("form") TrocaSenhaForm form,
      BindingResult erros,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    if (erros.hasErrors()) {
      return VIEW;
    }
    try {
      conta.trocarSenha(eu.getId(), form);
    } catch (CampoInvalidoException invalido) {
      erros.rejectValue(invalido.getCampo(), "invalido", invalido.getMessage());
      return VIEW;
    }
    atributos.addFlashAttribute("aviso", "Senha trocada.");
    return eu.isAdmin() ? "redirect:/painel" : "redirect:/portaria";
  }
}
