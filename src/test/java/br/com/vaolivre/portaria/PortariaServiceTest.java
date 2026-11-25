package br.com.vaolivre.portaria;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.clientes.Cliente.DadosDoCliente;
import br.com.vaolivre.clientes.TipoCliente;
import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.contratos.SituacaoContrato;
import br.com.vaolivre.ocorrencias.Motivo;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PortariaServiceTest {

  private static final LocalDate HOJE = LocalDate.of(2026, 11, 25);

  private static Contrato contrato(LocalDate inicio, LocalDate fim, SituacaoContrato situacao) {
    Cliente cliente =
        new Cliente(
            new DadosDoCliente(TipoCliente.PJ, "Alfa", "11222333000181", null, null, null),
            OffsetDateTime.now());
    Contrato contrato = new Contrato(cliente, 5, inicio, fim, OffsetDateTime.now());
    contrato.alterar(5, inicio, fim, situacao);
    return contrato;
  }

  @Test
  void contratoAtivoEDentroDaVigenciaNaoImpede() {
    Contrato vigente = contrato(HOJE.minusMonths(1), HOJE.plusMonths(1), SituacaoContrato.ATIVO);
    assertThat(PortariaService.impedimentoDoContrato(Optional.of(vigente), HOJE)).isEmpty();
  }

  @Test
  void cadaImpedimentoTemOSeuMotivo() {
    assertThat(PortariaService.impedimentoDoContrato(Optional.empty(), HOJE))
        .contains(Motivo.SEM_CONTRATO);
    assertThat(
            PortariaService.impedimentoDoContrato(
                Optional.of(contrato(HOJE.minusYears(1), null, SituacaoContrato.SUSPENSO)), HOJE))
        .contains(Motivo.CONTRATO_SUSPENSO);
    assertThat(
            PortariaService.impedimentoDoContrato(
                Optional.of(
                    contrato(HOJE.minusYears(1), HOJE.minusDays(1), SituacaoContrato.ATIVO)),
                HOJE))
        .contains(Motivo.CONTRATO_VENCIDO);
    assertThat(
            PortariaService.impedimentoDoContrato(
                Optional.of(contrato(HOJE.plusDays(3), null, SituacaoContrato.ATIVO)), HOJE))
        .contains(Motivo.CONTRATO_FUTURO);
  }

  @Test
  void contratoQueVenceHojeAindaVale() {
    Contrato venceHoje = contrato(HOJE.minusYears(1), HOJE, SituacaoContrato.ATIVO);
    assertThat(PortariaService.impedimentoDoContrato(Optional.of(venceHoje), HOJE)).isEmpty();
  }
}
