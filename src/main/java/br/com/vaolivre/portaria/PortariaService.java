package br.com.vaolivre.portaria;

import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.contratos.ContratosRepository;
import br.com.vaolivre.contratos.SituacaoContrato;
import br.com.vaolivre.equipe.Usuario;
import br.com.vaolivre.equipe.UsuariosRepository;
import br.com.vaolivre.estadias.Estadia;
import br.com.vaolivre.estadias.EstadiasRepository;
import br.com.vaolivre.ocorrencias.Motivo;
import br.com.vaolivre.ocorrencias.Ocorrencia;
import br.com.vaolivre.ocorrencias.OcorrenciasRepository;
import br.com.vaolivre.ocorrencias.TipoOcorrencia;
import br.com.vaolivre.veiculos.Placa;
import br.com.vaolivre.veiculos.Veiculo;
import br.com.vaolivre.veiculos.VeiculosRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A regra principal do sistema: liberar ou bloquear a entrada e registrar a saída.
 *
 * <p>Toda entrada trava a linha do contrato vigente ({@code SELECT ... FOR UPDATE}) antes de contar
 * as vagas em uso. Duas entradas do mesmo cliente ao mesmo tempo esperam uma pela outra, e o banco
 * ainda recusa duas estadias abertas na mesma vaga do quadro. O limite nunca estoura.
 */
@Service
public class PortariaService {

  private final VeiculosRepository veiculos;
  private final ContratosRepository contratos;
  private final EstadiasRepository estadias;
  private final OcorrenciasRepository ocorrencias;
  private final UsuariosRepository usuarios;
  private final Clock relogio;

  public PortariaService(
      VeiculosRepository veiculos,
      ContratosRepository contratos,
      EstadiasRepository estadias,
      OcorrenciasRepository ocorrencias,
      UsuariosRepository usuarios,
      Clock relogio) {
    this.veiculos = veiculos;
    this.contratos = contratos;
    this.estadias = estadias;
    this.ocorrencias = ocorrencias;
    this.usuarios = usuarios;
    this.relogio = relogio;
  }

  /** Decide, grava a ocorrência e devolve o id dela. Bloqueio também é gravado. */
  @Transactional
  public long registrarEntrada(String placaDigitada, long operadorId) {
    Placa placa = Placa.de(placaDigitada);
    Usuario operador = usuarios.getReferenceById(operadorId);
    OffsetDateTime agora = OffsetDateTime.now(relogio);

    Optional<Veiculo> ativo = veiculos.ativoPelaPlaca(placa.valor());
    if (ativo.isEmpty()) {
      Optional<Veiculo> antigo = veiculos.maisRecentePelaPlaca(placa.valor());
      Motivo motivo = antigo.isPresent() ? Motivo.VEICULO_INATIVO : Motivo.PLACA_NAO_CADASTRADA;
      return bloquear(TipoOcorrencia.ENTRADA, motivo, placa, antigo.orElse(null), operador, agora);
    }
    Veiculo veiculo = ativo.get();
    Cliente cliente = veiculo.getCliente();
    if (!cliente.isAtivo()) {
      return bloquear(
          TipoOcorrencia.ENTRADA, Motivo.CLIENTE_INATIVO, placa, veiculo, operador, agora);
    }

    Optional<Contrato> vigente = contratos.travarVigenteDoCliente(cliente.getId());
    Optional<Motivo> impedimento = impedimentoDoContrato(vigente, LocalDate.now(relogio));
    if (impedimento.isPresent()) {
      return bloquear(TipoOcorrencia.ENTRADA, impedimento.get(), placa, veiculo, operador, agora);
    }
    Contrato contrato = vigente.orElseThrow();

    if (estadias.existsByVeiculoIdAndSaidaEmIsNull(veiculo.getId())) {
      return bloquear(TipoOcorrencia.ENTRADA, Motivo.JA_NO_PATIO, placa, veiculo, operador, agora);
    }

    List<Integer> ocupadas = estadias.vagasOcupadas(contrato.getId());
    Optional<Integer> livre =
        QuadroDeVagas.primeiraVagaLivre(contrato.getVagasContratadas(), ocupadas);
    if (livre.isEmpty()) {
      return bloquear(
          TipoOcorrencia.ENTRADA, Motivo.VAGAS_ESGOTADAS, placa, veiculo, operador, agora);
    }

    Estadia estadia =
        estadias.saveAndFlush(new Estadia(veiculo, contrato, livre.get(), agora, operador));
    return ocorrencias
        .save(Ocorrencia.liberada(TipoOcorrencia.ENTRADA, estadia, operador, agora))
        .getId();
  }

  /** A saída nunca é bloqueada: se o carro está no pátio, sai. */
  @Transactional
  public long registrarSaida(String placaDigitada, long operadorId) {
    Placa placa = Placa.de(placaDigitada);
    Usuario operador = usuarios.getReferenceById(operadorId);
    OffsetDateTime agora = OffsetDateTime.now(relogio);

    Optional<Estadia> aberta = estadias.abertaPelaPlaca(placa.valor());
    if (aberta.isEmpty()) {
      Optional<Veiculo> veiculo = veiculos.maisRecentePelaPlaca(placa.valor());
      Motivo motivo = veiculo.isPresent() ? Motivo.FORA_DO_PATIO : Motivo.PLACA_NAO_CADASTRADA;
      return bloquear(TipoOcorrencia.SAIDA, motivo, placa, veiculo.orElse(null), operador, agora);
    }
    Estadia estadia = aberta.get();
    estadia.encerrar(agora, operador);
    return ocorrencias
        .save(Ocorrencia.liberada(TipoOcorrencia.SAIDA, estadia, operador, agora))
        .getId();
  }

  static Optional<Motivo> impedimentoDoContrato(Optional<Contrato> vigente, LocalDate hoje) {
    if (vigente.isEmpty()) {
      return Optional.of(Motivo.SEM_CONTRATO);
    }
    Contrato contrato = vigente.get();
    if (contrato.getSituacao() == SituacaoContrato.SUSPENSO) {
      return Optional.of(Motivo.CONTRATO_SUSPENSO);
    }
    if (contrato.vencidoEm(hoje)) {
      return Optional.of(Motivo.CONTRATO_VENCIDO);
    }
    if (contrato.aindaNaoComecouEm(hoje)) {
      return Optional.of(Motivo.CONTRATO_FUTURO);
    }
    return Optional.empty();
  }

  private long bloquear(
      TipoOcorrencia tipo,
      Motivo motivo,
      Placa placa,
      Veiculo veiculo,
      Usuario operador,
      OffsetDateTime agora) {
    return ocorrencias
        .save(Ocorrencia.bloqueada(tipo, motivo, placa, veiculo, operador, agora))
        .getId();
  }
}
