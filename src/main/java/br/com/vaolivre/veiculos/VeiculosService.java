package br.com.vaolivre.veiculos;

import br.com.vaolivre.auditoria.AuditoriaService;
import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.clientes.ClientesRepository;
import br.com.vaolivre.estadias.EstadiasRepository;
import br.com.vaolivre.global.CampoInvalidoException;
import br.com.vaolivre.global.Filtros;
import br.com.vaolivre.global.NaoEncontradoException;
import jakarta.persistence.criteria.JoinType;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeiculosService {

  private static final int POR_PAGINA = 25;

  private final VeiculosRepository veiculos;
  private final ClientesRepository clientes;
  private final EstadiasRepository estadias;
  private final AuditoriaService auditoria;
  private final Clock relogio;

  public VeiculosService(
      VeiculosRepository veiculos,
      ClientesRepository clientes,
      EstadiasRepository estadias,
      AuditoriaService auditoria,
      Clock relogio) {
    this.veiculos = veiculos;
    this.clientes = clientes;
    this.estadias = estadias;
    this.auditoria = auditoria;
    this.relogio = relogio;
  }

  @Transactional(readOnly = true)
  public Page<LinhaVeiculo> listar(String busca, boolean incluirInativos, int pagina) {
    Specification<Veiculo> filtro =
        Filtros.todos(porBusca(busca), incluirInativos ? null : apenasAtivos());
    return veiculos
        .findAll(filtro, PageRequest.of(Math.max(pagina, 0), POR_PAGINA, Sort.by("placa")))
        .map(v ->
            new LinhaVeiculo(
                v.getId(),
                v.getPlaca(),
                ((v.getModelo() == null ? "" : v.getModelo())
                        + " "
                        + (v.getCor() == null ? "" : v.getCor()))
                    .trim(),
                v.getCliente().getId(),
                v.getCliente().getNome(),
                v.isAtivo(),
                estadias.existsByVeiculoIdAndSaidaEmIsNull(v.getId())));
  }

  @Transactional
  public long cadastrar(VeiculoForm form, long administradorId) {
    Cliente cliente =
        clientes
            .findById(form.clienteId())
            .orElseThrow(
                () -> new CampoInvalidoException("clienteId", "Escolha um cliente da lista."));
    Placa placa = Placa.de(form.placa());
    if (veiculos.existsByPlacaAndAtivoTrue(placa.valor())) {
      throw new CampoInvalidoException("placa", "A placa " + placa + " já está cadastrada.");
    }
    Veiculo veiculo =
        veiculos.save(
            new Veiculo(
                cliente,
                placa,
                texto(form.modelo()),
                texto(form.cor()),
                OffsetDateTime.now(relogio)));
    auditoria.registrar(
        administradorId,
        "Cadastrou veículo",
        "veiculo",
        veiculo.getId(),
        "Para o cliente " + cliente.getId() + ".");
    return veiculo.getId();
  }

  /** O veículo para de entrar. Se estiver no pátio, ainda pode sair. */
  @Transactional
  public long desativar(long id, long administradorId) {
    Veiculo veiculo =
        veiculos
            .findById(id)
            .orElseThrow(() -> new NaoEncontradoException("Veículo não encontrado"));
    veiculo.desativar();
    auditoria.registrar(
        administradorId, "Desativou veículo", "veiculo", id, "Entradas bloqueadas.");
    return veiculo.getCliente().getId();
  }

  private static String texto(String valor) {
    return valor == null || valor.isBlank() ? null : valor.trim();
  }

  private static Specification<Veiculo> porBusca(String busca) {
    if (busca == null || busca.isBlank()) {
      return null;
    }
    String placa = "%" + busca.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "") + "%";
    String nome = "%" + busca.trim().toLowerCase(Locale.ROOT) + "%";
    return (raiz, consulta, cb) ->
        cb.or(
            cb.like(raiz.get("placa"), placa),
            cb.like(cb.lower(raiz.join("cliente", JoinType.INNER).get("nome")), nome));
  }

  private static Specification<Veiculo> apenasAtivos() {
    return (raiz, consulta, cb) -> cb.isTrue(raiz.get("ativo"));
  }

  public record LinhaVeiculo(
      long id,
      String placa,
      String descricao,
      long clienteId,
      String cliente,
      boolean ativo,
      boolean noPatio) {}
}
