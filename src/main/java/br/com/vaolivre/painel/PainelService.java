package br.com.vaolivre.painel;

import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.contratos.ContratosRepository;
import br.com.vaolivre.contratos.SituacaoContrato;
import br.com.vaolivre.estadias.EstadiasRepository;
import br.com.vaolivre.global.Formatos;
import br.com.vaolivre.ocorrencias.OcorrenciasRepository;
import br.com.vaolivre.ocorrencias.Resultado;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Indicadores reais do banco. Nenhum número inventado. */
@Service
@Transactional(readOnly = true)
public class PainelService {

  private static final int DIAS_PARA_AVISAR_VENCIMENTO = 30;

  private final ContratosRepository contratos;
  private final EstadiasRepository estadias;
  private final OcorrenciasRepository ocorrencias;
  private final Clock relogio;

  public PainelService(
      ContratosRepository contratos,
      EstadiasRepository estadias,
      OcorrenciasRepository ocorrencias,
      Clock relogio) {
    this.contratos = contratos;
    this.estadias = estadias;
    this.ocorrencias = ocorrencias;
    this.relogio = relogio;
  }

  public Painel painel() {
    LocalDate hoje = LocalDate.now(relogio);
    OffsetDateTime inicioDoDia = hoje.atStartOfDay(relogio.getZone()).toOffsetDateTime();

    List<LinhaDoPainel> linhas =
        contratos.vigentes().stream()
            .map(c -> linha(c, hoje))
            .sorted(
                Comparator.comparingDouble(LinhaDoPainel::ocupacao)
                    .reversed()
                    .thenComparing(LinhaDoPainel::cliente))
            .toList();

    return new Painel(
        estadias.countBySaidaEmIsNull(),
        linhas.stream().filter(LinhaDoPainel::cheio).count(),
        ocorrencias.countByResultadoAndOcorridoEmGreaterThanEqual(Resultado.BLOQUEADO, inicioDoDia),
        ocorrencias.countByOcorridoEmGreaterThanEqual(inicioDoDia),
        linhas.stream().filter(l -> l.venceEm() != null).count(),
        estadias.contratosAcimaDoLimite(),
        linhas);
  }

  private LinhaDoPainel linha(Contrato contrato, LocalDate hoje) {
    int emUso = (int) estadias.countByContratoIdAndSaidaEmIsNull(contrato.getId());
    int vagas = contrato.getVagasContratadas();
    boolean vencendo =
        contrato.getFim() != null
            && !contrato.getFim().isBefore(hoje)
            && contrato.getFim().isBefore(hoje.plusDays(DIAS_PARA_AVISAR_VENCIMENTO + 1L));
    return new LinhaDoPainel(
        contrato.getCliente().getId(),
        contrato.getCliente().getNome(),
        vagas,
        emUso,
        contrato.getSituacao() == SituacaoContrato.SUSPENSO,
        vencendo ? Formatos.data(contrato.getFim()) : null,
        IntStream.rangeClosed(1, vagas).mapToObj(i -> i <= emUso).toList());
  }

  public record Painel(
      long noPatio,
      long clientesNoLimite,
      long bloqueiosHoje,
      long registrosHoje,
      long contratosVencendo,
      long contratosAcimaDoLimite,
      List<LinhaDoPainel> linhas) {}

  /** Uma linha por cliente com contrato vigente. "pontos" desenha as vagas: true = ocupada. */
  public record LinhaDoPainel(
      long clienteId,
      String cliente,
      int vagas,
      int emUso,
      boolean suspenso,
      String venceEm,
      List<Boolean> pontos) {

    public double ocupacao() {
      return vagas == 0 ? 0 : (double) emUso / vagas;
    }

    public boolean cheio() {
      return emUso >= vagas;
    }
  }
}
