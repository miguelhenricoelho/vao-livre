package br.com.vaolivre.veiculos;

import br.com.vaolivre.acesso.UsuarioLogado;
import br.com.vaolivre.clientes.ClientesService;
import br.com.vaolivre.global.CampoInvalidoException;
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
@RequestMapping("/veiculos")
@PreAuthorize("hasRole('ADMIN')")
public class VeiculosController {

  private static final String VIEW = "veiculos/veiculos";

  private final VeiculosService veiculos;
  private final ClientesService clientes;

  public VeiculosController(VeiculosService veiculos, ClientesService clientes) {
    this.veiculos = veiculos;
    this.clientes = clientes;
  }

  @GetMapping
  public String lista(
      @RequestParam(required = false) String busca,
      @RequestParam(defaultValue = "false") boolean inativos,
      @RequestParam(defaultValue = "0") int pagina,
      @RequestParam(required = false) Long cliente,
      Model modelo) {
    preparar(modelo, busca, inativos, pagina);
    modelo.addAttribute("form", VeiculoForm.vazio(cliente));
    return VIEW;
  }

  @PostMapping
  public String cadastrar(
      @Valid @ModelAttribute("form") VeiculoForm form,
      BindingResult erros,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos,
      Model modelo) {
    if (!erros.hasErrors()) {
      try {
        veiculos.cadastrar(form, eu.getId());
        atributos.addFlashAttribute("aviso", "Veículo cadastrado.");
        return "redirect:/veiculos";
      } catch (CampoInvalidoException invalido) {
        erros.rejectValue(invalido.getCampo(), "invalido", invalido.getMessage());
      }
    }
    preparar(modelo, null, false, 0);
    return VIEW;
  }

  @PostMapping("/{id}/desativar")
  public String desativar(
      @PathVariable long id,
      @AuthenticationPrincipal UsuarioLogado eu,
      RedirectAttributes atributos) {
    long clienteId = veiculos.desativar(id, eu.getId());
    atributos.addFlashAttribute("aviso", "Veículo desativado. Ele não entra mais.");
    return "redirect:/clientes/" + clienteId;
  }

  private void preparar(Model modelo, String busca, boolean inativos, int pagina) {
    modelo.addAttribute("pagina", veiculos.listar(busca, inativos, pagina));
    modelo.addAttribute("busca", busca == null ? "" : busca);
    modelo.addAttribute("inativos", inativos);
    modelo.addAttribute("clientes", clientes.ativosParaEscolha());
  }
}
