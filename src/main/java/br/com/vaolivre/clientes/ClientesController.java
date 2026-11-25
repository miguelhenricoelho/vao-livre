package br.com.vaolivre.clientes;

import br.com.vaolivre.acesso.UsuarioLogado;
import br.com.vaolivre.global.CampoInvalidoException;
import br.com.vaolivre.portaria.PortariaConsultaService;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDate;
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
@RequestMapping("/clientes")
@PreAuthorize("hasRole('ADMIN')")
public class ClientesController {

  private final ClientesService clientes;
  private final PortariaConsultaService portaria;
  private final Clock relogio;

  public ClientesController(
      ClientesService clientes, PortariaConsultaService portaria, Clock relogio) {
    this.clientes = clientes;
    this.portaria = portaria;
    this.relogio = relogio;
  }

  @GetMapping
  public String lista(
      @RequestParam(required = false) String busca,
      @RequestParam(required = false) TipoCliente tipo,
      @RequestParam(defaultValue = "false") boolean inativos,
      @RequestParam(defaultValue = "0") int pagina,
      Model modelo) {
    modelo.addAttribute("pagina", clientes.listar(busca, tipo, inativos, pagina));
    modelo.addAttribute("busca", busca == null ? "" : busca);
    modelo.addAttribute("tipo", tipo);
    modelo.addAttribute("inativos", inativos);
    modelo.addAttribute("tipos", TipoCliente.values());
    return "clientes/clientes";
  }

  @GetMapping("/novo")
  public String novo(Model modelo) {
    modelo.addAttribute("form", CadastroRapidoForm.vazio(LocalDate.now(relogio)));
    modelo.addAttribute("tipos", TipoCliente.values());
    return "clientes/cadastro";
  }

  @PostMapping("/novo")
  public String cadastrar(
      @Valid @ModelAttribute("form") CadastroRapidoForm form,
      BindingResult erros,
      @RequestParam(defaultValue = "salvar") String acao,
      @AuthenticationPrincipal UsuarioLogado eu,
      Model modelo) {
    modelo.addAttribute("tipos", TipoCliente.values());
    if ("mais-linhas".equals(acao)) {
      modelo.addAttribute("form", form.comMaisLinhas());
      return "clientes/cadastro";
    }
    if (erros.hasErrors()) {
      return "clientes/cadastro";
    }
    try {
      return "redirect:/clientes/" + clientes.cadastrarRapido(form, eu.getId()) + "?cadastrado";
    } catch (CampoInvalidoException invalido) {
      erros.rejectValue(invalido.getCampo(), "invalido", invalido.getMessage());
      return "clientes/cadastro";
    }
  }

  @GetMapping("/{id}")
  public String ficha(@PathVariable long id, Model modelo) {
    FichaDoCliente ficha = clientes.ficha(id);
    modelo.addAttribute("ficha", ficha);
    modelo.addAttribute("garagem", portaria.garagemDoCliente(id, ficha.nome()).orElse(null));
    return "clientes/cliente";
  }

  @GetMapping("/{id}/editar")
  public String editar(@PathVariable long id, Model modelo) {
    modelo.addAttribute("form", clientes.formDeEdicao(id));
    modelo.addAttribute("clienteId", id);
    modelo.addAttribute("tipos", TipoCliente.values());
    return "clientes/editar";
  }

  @PostMapping("/{id}/editar")
  public String salvar(
      @PathVariable long id,
      @Valid @ModelAttribute("form") ClienteForm form,
      BindingResult erros,
      @AuthenticationPrincipal UsuarioLogado eu,
      Model modelo) {
    modelo.addAttribute("clienteId", id);
    modelo.addAttribute("tipos", TipoCliente.values());
    if (erros.hasErrors()) {
      return "clientes/editar";
    }
    try {
      clientes.atualizar(id, form, eu.getId());
      return "redirect:/clientes/" + id + "?salvo";
    } catch (CampoInvalidoException invalido) {
      erros.rejectValue(invalido.getCampo(), "invalido", invalido.getMessage());
      return "clientes/editar";
    }
  }

  @PostMapping("/{id}/desativar")
  public String desativar(
      @PathVariable long id,
      @RequestParam(required = false) String senha,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    clientes.desativar(id, senha, eu.getId());
    atributos.addFlashAttribute("aviso", "Cliente desativado. Os veículos dele não entram mais.");
    return "redirect:/clientes/" + id;
  }

  @PostMapping("/{id}/reativar")
  public String reativar(
      @PathVariable long id,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    clientes.reativar(id, eu.getId());
    atributos.addFlashAttribute("aviso", "Cliente reativado.");
    return "redirect:/clientes/" + id;
  }
}
