package br.com.vaolivre.equipe;

import br.com.vaolivre.acesso.AcessoService;
import br.com.vaolivre.acesso.ConfirmacaoDeSenha;
import br.com.vaolivre.acesso.UsuarioLogado;
import br.com.vaolivre.global.CampoInvalidoException;
import br.com.vaolivre.global.Formatos;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/equipe")
@PreAuthorize("hasRole('ADMIN')")
public class EquipeController {

  private static final String VIEW = "equipe/equipe";

  private final EquipeService equipe;
  private final AcessoService acesso;
  private final ConfirmacaoDeSenha confirmacao;

  public EquipeController(
      EquipeService equipe, AcessoService acesso, ConfirmacaoDeSenha confirmacao) {
    this.equipe = equipe;
    this.acesso = acesso;
    this.confirmacao = confirmacao;
  }

  @GetMapping
  public String equipe(Model modelo) {
    modelo.addAttribute("form", MembroForm.vazio());
    preparar(modelo);
    return VIEW;
  }

  @PostMapping
  public String criar(
      @Valid @ModelAttribute("form") MembroForm form,
      BindingResult erros,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos,
      Model modelo) {
    if (!erros.hasErrors()) {
      try {
        equipe.criar(form, eu.getId());
        atributos.addFlashAttribute(
            "aviso", "Conta criada. Passe a senha temporária pessoalmente.");
        return "redirect:/equipe";
      } catch (CampoInvalidoException invalido) {
        erros.rejectValue(invalido.getCampo(), "invalido", invalido.getMessage());
      }
    }
    preparar(modelo);
    return VIEW;
  }

  @PostMapping("/{id}/papel")
  public String papel(
      @PathVariable long id,
      @RequestParam Papel papel,
      @RequestParam(required = false) String senha,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    equipe.mudarPapel(id, papel, senha, eu.getId());
    atributos.addFlashAttribute("aviso", "Papel alterado. Vale no próximo login da pessoa.");
    return "redirect:/equipe";
  }

  @PostMapping("/{id}/desativar")
  public String desativar(
      @PathVariable long id,
      @RequestParam(required = false) String senha,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    equipe.desativar(id, senha, eu.getId());
    atributos.addFlashAttribute("aviso", "Conta desativada. A sessão dela foi encerrada.");
    return "redirect:/equipe";
  }

  @PostMapping("/{id}/reativar")
  public String reativar(
      @PathVariable long id,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    equipe.reativar(id, eu.getId());
    atributos.addFlashAttribute("aviso", "Conta reativada.");
    return "redirect:/equipe";
  }

  @PostMapping("/{id}/senha")
  public String redefinir(
      @PathVariable long id,
      @RequestParam(required = false) String temporaria,
      @RequestParam(required = false) String senha,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    equipe.redefinirSenha(id, temporaria, senha, eu.getId());
    atributos.addFlashAttribute("aviso", "Senha temporária definida. A pessoa troca ao entrar.");
    return "redirect:/equipe";
  }

  @PostMapping("/senha-da-area")
  public String senhaDaArea(
      @RequestParam(required = false) String nova,
      @RequestParam(required = false) String senha,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    confirmacao.exigir(eu.getId(), senha);
    acesso.trocarSenhaDaArea(nova, eu.getId());
    atributos.addFlashAttribute("aviso", "Senha da área trocada. Todo mundo vai digitar a nova.");
    return "redirect:/entrar";
  }

  @PostMapping("/whatsapp")
  public String whatsapp(
      @RequestParam(required = false) String numero,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    acesso.definirWhatsappDoSuporte(numero, eu.getId());
    atributos.addFlashAttribute("aviso", "WhatsApp do suporte atualizado.");
    return "redirect:/equipe";
  }

  private void preparar(Model modelo) {
    modelo.addAttribute("membros", equipe.listar());
    modelo.addAttribute("papeis", Papel.values());
    modelo.addAttribute("whatsapp", Formatos.telefone(acesso.whatsappDoSuporte()));
  }
}
