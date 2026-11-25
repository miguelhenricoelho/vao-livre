package br.com.vaolivre.historico;

import br.com.vaolivre.global.Filtros;
import br.com.vaolivre.global.Formatos;
import br.com.vaolivre.ocorrencias.Ocorrencia;
import br.com.vaolivre.ocorrencias.OcorrenciasRepository;
import br.com.vaolivre.ocorrencias.Resultado;
import java.time.Clock;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Histórico de acessos: toda tentativa, liberada ou bloqueada, com filtros feitos no servidor. */
@Service
@Transactional(readOnly = true)
public class HistoricoService {

  private static final int POR_PAGINA = 30;

  private final OcorrenciasRepository ocorrencias;
  private final Clock relogio;

  public HistoricoService(OcorrenciasRepository ocorrencias, Clock relogio) {
    this.ocorrencias = ocorrencias;
    this.relogio = relogio;
  }

  public Page<LinhaHistorico> listar(FiltroHistorico filtro, int pagina) {
    Specification<Ocorrencia> busca =
        Filtros.todos(
            desde(filtro.de()),
            ate(filtro.ate()),
            porPlaca(filtro.placa()),
            porResultado(filtro.resultado()));
    return ocorrencias
        .findAll(
            busca,
            PageRequest.of(
                Math.max(pagina, 0), POR_PAGINA, Sort.by(Sort.Direction.DESC, "ocorridoEm")))
        .map(o ->
            new LinhaHistorico(
                Formatos.dataHora(o.getOcorridoEm(), relogio),
                o.getPlacaInformada(),
                o.getCliente() == null ? "—" : o.getCliente().getNome(),
                o.getTipo().getDescricao(),
                o.getResultado() == Resultado.LIBERADO,
                o.getMotivo() == null ? "" : o.getMotivo().getCurto(),
                o.getOperador().getNome()));
  }

  private Specification<Ocorrencia> desde(LocalDate dia) {
    return dia == null
        ? null
        : (r, q, cb) ->
            cb.greaterThanOrEqualTo(
                r.get("ocorridoEm"), dia.atStartOfDay(relogio.getZone()).toOffsetDateTime());
  }

  private Specification<Ocorrencia> ate(LocalDate dia) {
    return dia == null
        ? null
        : (r, q, cb) ->
            cb.lessThan(
                r.get("ocorridoEm"),
                dia.plusDays(1).atStartOfDay(relogio.getZone()).toOffsetDateTime());
  }

  private static Specification<Ocorrencia> porPlaca(String placa) {
    if (placa == null || placa.isBlank()) {
      return null;
    }
    String limpa = "%" + placa.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "") + "%";
    return (r, q, cb) -> cb.like(r.get("placaInformada"), limpa);
  }

  private static Specification<Ocorrencia> porResultado(Resultado resultado) {
    return resultado == null ? null : (r, q, cb) -> cb.equal(r.get("resultado"), resultado);
  }

  public record FiltroHistorico(LocalDate de, LocalDate ate, String placa, Resultado resultado) {}

  public record LinhaHistorico(
      String quando,
      String placa,
      String cliente,
      String tipo,
      boolean liberado,
      String motivo,
      String operador) {}
}
