package br.com.vaolivre.contratos;

import br.com.vaolivre.acesso.UsuarioLogado;
import br.com.vaolivre.contratos.ContratosService.ContratoParaEditar;
import br.com.vaolivre.global.CampoInvalidoException;
import jakarta.validation.Valid;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Optional;
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

/** Contrato do cliente: /contratos/{idDoCliente}. */
@Controller
@RequestMapping("/contratos/{clienteId}")
@PreAuthorize("hasRole('ADMIN')")
public class ContratosController {

  private static final String VIEW = "contratos/contratos";

  private final ContratosService contratos;
  private final Clock relogio;

  public ContratosController(ContratosService contratos, Clock relogio) {
    this.contratos = contratos;
    this.relogio = relogio;
  }

  @GetMapping
  public String editar(@PathVariable long clienteId, Model modelo) {
    Optional<ContratoParaEditar> vigente = contratos.vigente(clienteId);
    modelo.addAttribute("clienteId", clienteId);
    modelo.addAttribute("cliente", contratos.nomeDoCliente(clienteId));
    modelo.addAttribute("emUso", vigente.map(ContratoParaEditar::emUso).orElse(0));
    modelo.addAttribute("novo", vigente.isEmpty());
    modelo.addAttribute(
        "form",
        vigente
            .map(ContratoParaEditar::form)
            .orElse(new ContratoForm(1, LocalDate.now(relogio), null, SituacaoContrato.ATIVO, "")));
    modelo.addAttribute("situacoes", SituacaoContrato.values());
    return VIEW;
  }

  @PostMapping
  public String salvar(
      @PathVariable long clienteId,
      @Valid @ModelAttribute("form") ContratoForm form,
      BindingResult erros,
      @AuthenticationPrincipal UsuarioLogado eu,
      Model modelo) {
    if (!erros.hasErrors()) {
      try {
        contratos.salvar(clienteId, form, eu.getId());
        return "redirect:/clientes/" + clienteId + "?contrato";
      } catch (CampoInvalidoException invalido) {
        erros.rejectValue(invalido.getCampo(), "invalido", invalido.getMessage());
      }
    }
    modelo.addAttribute("clienteId", clienteId);
    modelo.addAttribute("cliente", contratos.nomeDoCliente(clienteId));
    modelo.addAttribute(
        "emUso", contratos.vigente(clienteId).map(ContratoParaEditar::emUso).orElse(0));
    modelo.addAttribute("novo", contratos.vigente(clienteId).isEmpty());
    modelo.addAttribute("situacoes", SituacaoContrato.values());
    return VIEW;
  }
}
