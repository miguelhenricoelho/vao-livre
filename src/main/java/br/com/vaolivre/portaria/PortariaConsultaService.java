package br.com.vaolivre.portaria;

import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.contratos.ContratosRepository;
import br.com.vaolivre.estadias.Estadia;
import br.com.vaolivre.estadias.EstadiasRepository;
import br.com.vaolivre.global.Formatos;
import br.com.vaolivre.global.NaoEncontradoException;
import br.com.vaolivre.ocorrencias.Motivo;
import br.com.vaolivre.ocorrencias.Ocorrencia;
import br.com.vaolivre.ocorrencias.OcorrenciasRepository;
import br.com.vaolivre.ocorrencias.Resultado;
import br.com.vaolivre.ocorrencias.TipoOcorrencia;
import br.com.vaolivre.portaria.QuadroDeVagas.EstadiaNoQuadro;
import br.com.vaolivre.portaria.QuadroDeVagas.Manobra;
import br.com.vaolivre.portaria.ResultadoPortaria.PlacaNoPatio;
import br.com.vaolivre.whatsapp.MensagensWhatsApp;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Monta o que as telas mostram: o resultado de um registro, o pátio e a garagem de um cliente. */
@Service
@Transactional(readOnly = true)
public class PortariaConsultaService {

  /** Depois disso, recarregar a página mostra o desenho parado, sem repetir a manobra. */
  private static final Duration MANOBRA_RECENTE = Duration.ofMinutes(2);

  private final OcorrenciasRepository ocorrencias;
  private final EstadiasRepository estadias;
  private final ContratosRepository contratos;
  private final MensagensWhatsApp whatsapp;
  private final Clock relogio;

  public PortariaConsultaService(
      OcorrenciasRepository ocorrencias,
      EstadiasRepository estadias,
      ContratosRepository contratos,
      MensagensWhatsApp whatsapp,
      Clock relogio) {
    this.ocorrencias = ocorrencias;
    this.estadias = estadias;
    this.contratos = contratos;
    this.whatsapp = whatsapp;
    this.relogio = relogio;
  }

  public ResultadoPortaria resultado(long ocorrenciaId) {
    Ocorrencia ocorrencia =
        ocorrencias
            .completa(ocorrenciaId)
            .orElseThrow(() -> new NaoEncontradoException("Registro da portaria não encontrado"));
    Cliente cliente = ocorrencia.getCliente();
    Optional<Contrato> contrato =
        cliente == null ? Optional.empty() : contratos.vigenteDoCliente(cliente.getId());
    Estadia estadia = ocorrencia.getEstadia();
    boolean recente =
        Duration.between(ocorrencia.getOcorridoEm(), OffsetDateTime.now(relogio))
                .compareTo(MANOBRA_RECENTE)
            < 0;

    List<Estadia> dentro =
        contrato.map(c -> estadias.abertasDoContrato(c.getId())).orElse(List.of());
    int vagas = contrato.map(Contrato::getVagasContratadas).orElse(0);
    Optional<Manobra> manobra = recente ? manobraDe(ocorrencia, estadia) : Optional.empty();
    QuadroDeVagas quadro =
        cliente == null && ocorrencia.getMotivo() == Motivo.PLACA_NAO_CADASTRADA
            ? QuadroDeVagas.montar("", 0, List.of(), manobra)
            : contrato
                .map(c -> QuadroDeVagas.montar(cliente.getNome(), vagas, noQuadro(dentro), manobra))
                .orElse(null);

    String hora = Formatos.hora(ocorrencia.getOcorridoEm(), relogio);
    String data = Formatos.dia(ocorrencia.getOcorridoEm(), relogio);
    List<PlacaNoPatio> ocupando =
        dentro.stream()
            .map(e ->
                new PlacaNoPatio(
                    e.getVeiculo().getPlaca(), Formatos.hora(e.getEntradaEm(), relogio)))
            .toList();

    return new ResultadoPortaria(
        ocorrencia.getTipo(),
        ocorrencia.getResultado(),
        ocorrencia.getMotivo(),
        ocorrencia.getPlacaInformada(),
        hora,
        data,
        cliente == null ? null : cliente.getNome(),
        estadia == null ? null : estadia.getVagaNoQuadro(),
        dentro.size(),
        vagas,
        estadia == null || estadia.isAberta() ? null : Formatos.duracao(estadia.permanencia()),
        contrato.map(c -> dataQueImpede(ocorrencia.getMotivo(), c)).orElse(null),
        ocupando,
        aviso(ocorrencia, cliente, contrato, hora, data, ocupando),
        quadro);
  }

  /** Quem está no pátio agora, do mais antigo para o mais novo. */
  public List<LinhaDoPatio> patio() {
    return estadias.todasAbertas().stream()
        .map(e ->
            new LinhaDoPatio(
                e.getVeiculo().getPlaca(),
                e.getVeiculo().getCliente().getNome(),
                e.getVagaNoQuadro(),
                Formatos.hora(e.getEntradaEm(), relogio)))
        .toList();
  }

  /** A garagem de um cliente, parada (sem manobra), para a ficha do cliente. */
  public Optional<QuadroDeVagas> garagemDoCliente(long clienteId, String titulo) {
    return contratos
        .vigenteDoCliente(clienteId)
        .map(c ->
            QuadroDeVagas.montar(
                titulo,
                c.getVagasContratadas(),
                noQuadro(estadias.abertasDoContrato(c.getId())),
                Optional.empty()));
  }

  public record LinhaDoPatio(String placa, String cliente, int vaga, String entrada) {}

  private List<EstadiaNoQuadro> noQuadro(List<Estadia> dentro) {
    return dentro.stream()
        .map(e ->
            new EstadiaNoQuadro(
                e.getVagaNoQuadro(),
                e.getVeiculo().getPlaca(),
                Formatos.hora(e.getEntradaEm(), relogio)))
        .toList();
  }

  private static Optional<Manobra> manobraDe(Ocorrencia ocorrencia, Estadia estadia) {
    if (ocorrencia.getResultado() != Resultado.LIBERADO) {
      return ocorrencia.getTipo() == TipoOcorrencia.ENTRADA
          ? Optional.of(Manobra.bloqueio())
          : Optional.empty();
    }
    if (ocorrencia.getTipo() == TipoOcorrencia.ENTRADA) {
      return estadia.isAberta()
          ? Optional.of(Manobra.entrada(estadia.getVagaNoQuadro()))
          : Optional.empty();
    }
    return Optional.of(Manobra.saida(estadia.getVagaNoQuadro()));
  }

  private static String dataQueImpede(Motivo motivo, Contrato contrato) {
    if (motivo == Motivo.CONTRATO_VENCIDO) {
      return Formatos.data(contrato.getFim());
    }
    if (motivo == Motivo.CONTRATO_FUTURO) {
      return Formatos.data(contrato.getInicio());
    }
    return null;
  }

  private String aviso(
      Ocorrencia ocorrencia,
      Cliente cliente,
      Optional<Contrato> contrato,
      String hora,
      String data,
      List<PlacaNoPatio> ocupando) {
    Motivo motivo = ocorrencia.getMotivo();
    if (motivo == null || !motivo.avisaCliente() || cliente == null) {
      return null;
    }
    if (cliente.getTelefone() == null || contrato.isEmpty()) {
      return null;
    }
    if (motivo == Motivo.VAGAS_ESGOTADAS) {
      return whatsapp.vagasEsgotadas(
          cliente.getTelefone(),
          cliente.getNome(),
          ocorrencia.getPlacaInformada(),
          hora,
          data,
          contrato.get().getVagasContratadas(),
          ocupando.stream().map(PlacaNoPatio::placa).toList());
    }
    return whatsapp.contratoImpedido(
        cliente.getTelefone(),
        cliente.getNome(),
        ocorrencia.getPlacaInformada(),
        hora,
        data,
        motivo,
        dataQueImpede(motivo, contrato.get()));
  }
}
