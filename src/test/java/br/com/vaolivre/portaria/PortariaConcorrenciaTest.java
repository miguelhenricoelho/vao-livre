package br.com.vaolivre.portaria;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.clientes.Cliente.DadosDoCliente;
import br.com.vaolivre.clientes.ClientesRepository;
import br.com.vaolivre.clientes.TipoCliente;
import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.contratos.ContratosRepository;
import br.com.vaolivre.equipe.UsuariosRepository;
import br.com.vaolivre.estadias.EstadiasRepository;
import br.com.vaolivre.global.BancoDeTeste;
import br.com.vaolivre.ocorrencias.Motivo;
import br.com.vaolivre.ocorrencias.OcorrenciasRepository;
import br.com.vaolivre.ocorrencias.Resultado;
import br.com.vaolivre.veiculos.Placa;
import br.com.vaolivre.veiculos.Veiculo;
import br.com.vaolivre.veiculos.VeiculosRepository;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Prova, num Postgres de verdade, que entradas ao mesmo tempo nunca estouram o limite de vagas. */
class PortariaConcorrenciaTest extends BancoDeTeste {

  private static final AtomicInteger SEQUENCIA = new AtomicInteger();

  @Autowired PortariaService portaria;
  @Autowired ClientesRepository clientes;
  @Autowired ContratosRepository contratos;
  @Autowired VeiculosRepository veiculos;
  @Autowired EstadiasRepository estadias;
  @Autowired OcorrenciasRepository ocorrencias;
  @Autowired UsuariosRepository usuarios;

  @Test
  void duasEntradasAoMesmoTempoComUmaVagaLivreSoUmaPassa() throws Exception {
    Cliente cliente = clienteComVagas(2, "AAA");
    List<String> placas = placas(cliente, "AAA", 3);
    long operador = operador();
    portaria.registrarEntrada(placas.get(0), operador);

    List<Long> registros = aoMesmoTempo(List.of(placas.get(1), placas.get(2)), operador);

    long liberadas = registros.stream().filter(this::liberado).count();
    assertThat(liberadas).isEqualTo(1);
    assertThat(registros.stream().filter(id -> motivo(id) == Motivo.VAGAS_ESGOTADAS)).hasSize(1);
    assertThat(estadias.countByContratoIdAndSaidaEmIsNull(contrato(cliente))).isEqualTo(2);
  }

  @Test
  void dezCarrosParaTresVagasEntramExatamenteTres() throws Exception {
    Cliente cliente = clienteComVagas(3, "BBB");
    List<String> placas = placas(cliente, "BBB", 10);

    List<Long> registros = aoMesmoTempo(placas, operador());

    assertThat(registros.stream().filter(this::liberado).count()).isEqualTo(3);
    assertThat(estadias.countByContratoIdAndSaidaEmIsNull(contrato(cliente))).isEqualTo(3);
    assertThat(estadias.vagasOcupadas(contrato(cliente))).containsExactlyInAnyOrder(1, 2, 3);
    assertThat(estadias.contratosAcimaDoLimite()).isZero();
  }

  @Test
  void mesmoCarroDuasVezesAoMesmoTempoSoEntraUmaVez() throws Exception {
    Cliente cliente = clienteComVagas(5, "CCC");
    String placa = placas(cliente, "CCC", 1).getFirst();

    List<Long> registros = aoMesmoTempo(List.of(placa, placa), operador());

    assertThat(registros.stream().filter(this::liberado).count()).isEqualTo(1);
    assertThat(registros.stream().filter(id -> motivo(id) == Motivo.JA_NO_PATIO)).hasSize(1);
  }

  @Test
  void saidaLiberaAVagaParaOProximo() {
    Cliente cliente = clienteComVagas(1, "DDD");
    List<String> placas = placas(cliente, "DDD", 2);
    long operador = operador();

    assertThat(liberado(portaria.registrarEntrada(placas.get(0), operador))).isTrue();
    assertThat(motivo(portaria.registrarEntrada(placas.get(1), operador)))
        .isEqualTo(Motivo.VAGAS_ESGOTADAS);
    assertThat(liberado(portaria.registrarSaida(placas.get(0), operador))).isTrue();
    assertThat(liberado(portaria.registrarEntrada(placas.get(1), operador))).isTrue();
  }

  private List<Long> aoMesmoTempo(List<String> placas, long operador) throws Exception {
    ExecutorService fila = Executors.newFixedThreadPool(placas.size());
    try {
      CountDownLatch largada = new CountDownLatch(1);
      List<Future<Long>> resultados = new ArrayList<>();
      for (String placa : placas) {
        Callable<Long> tarefa =
            () -> {
              largada.await();
              return portaria.registrarEntrada(placa, operador);
            };
        resultados.add(fila.submit(tarefa));
      }
      largada.countDown();
      List<Long> ids = new ArrayList<>();
      for (Future<Long> resultado : resultados) {
        ids.add(resultado.get());
      }
      return ids;
    } finally {
      fila.shutdownNow();
    }
  }

  private boolean liberado(Long ocorrenciaId) {
    return ocorrencias.findById(ocorrenciaId).orElseThrow().getResultado() == Resultado.LIBERADO;
  }

  private Motivo motivo(Long ocorrenciaId) {
    return ocorrencias.findById(ocorrenciaId).orElseThrow().getMotivo();
  }

  private long operador() {
    return usuarios.buscarPorEmail("admin@teste.invalid").orElseThrow().getId();
  }

  private long contrato(Cliente cliente) {
    return contratos.vigenteDoCliente(cliente.getId()).orElseThrow().getId();
  }

  private Cliente clienteComVagas(int vagas, String prefixo) {
    OffsetDateTime agora = OffsetDateTime.now();
    String documento = String.format("%011d", 10_000_000_000L + SEQUENCIA.incrementAndGet());
    Cliente cliente =
        clientes.save(
            new Cliente(
                new DadosDoCliente(TipoCliente.PF, "Teste " + prefixo, documento, null, null, null),
                agora));
    contratos.save(new Contrato(cliente, vagas, LocalDate.now().minusDays(1), null, agora));
    return cliente;
  }

  private List<String> placas(Cliente cliente, String prefixo, int quantidade) {
    List<String> placas = new ArrayList<>();
    for (int i = 0; i < quantidade; i++) {
      String placa = prefixo + "1A" + String.format("%02d", i);
      veiculos.save(new Veiculo(cliente, Placa.de(placa), null, null, OffsetDateTime.now()));
      placas.add(placa);
    }
    return placas;
  }
}
