package br.com.vaolivre.historico;

import br.com.vaolivre.historico.HistoricoService.FiltroHistorico;
import br.com.vaolivre.ocorrencias.Resultado;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HistoricoController {

  private final HistoricoService historico;

  public HistoricoController(HistoricoService historico) {
    this.historico = historico;
  }

  @GetMapping("/historico")
  public String historico(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate de,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ate,
      @RequestParam(required = false) String placa,
      @RequestParam(required = false) Resultado resultado,
      @RequestParam(defaultValue = "0") int pagina,
      Model modelo) {
    FiltroHistorico filtro = new FiltroHistorico(de, ate, placa, resultado);
    modelo.addAttribute("filtro", filtro);
    modelo.addAttribute("pagina", historico.listar(filtro, pagina));
    return "historico/historico";
  }
}
