package br.com.vaolivre.clientes;

import br.com.vaolivre.acesso.ConfirmacaoDeSenha;
import br.com.vaolivre.auditoria.AuditoriaService;
import br.com.vaolivre.clientes.Cliente.DadosDoCliente;
import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.contratos.ContratosRepository;
import br.com.vaolivre.estadias.EstadiasRepository;
import br.com.vaolivre.global.CampoInvalidoException;
import br.com.vaolivre.global.Filtros;
import br.com.vaolivre.global.Formatos;
import br.com.vaolivre.global.NaoEncontradoException;
import br.com.vaolivre.veiculos.Placa;
import br.com.vaolivre.veiculos.Veiculo;
import br.com.vaolivre.veiculos.VeiculosRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro de clientes: cadastro rápido, edição, busca e desativação. */
@Service
public class ClientesService {

  private static final int POR_PAGINA = 20;

  private final ClientesRepository clientes;
  private final ContratosRepository contratos;
  private final VeiculosRepository veiculos;
  private final EstadiasRepository estadias;
  private final AuditoriaService auditoria;
  private final ConfirmacaoDeSenha confirmacao;
  private final Clock relogio;

  public ClientesService(
      ClientesRepository clientes,
      ContratosRepository contratos,
      VeiculosRepository veiculos,
      EstadiasRepository estadias,
      AuditoriaService auditoria,
      ConfirmacaoDeSenha confirmacao,
      Clock relogio) {
    this.clientes = clientes;
    this.contratos = contratos;
    this.veiculos = veiculos;
    this.estadias = estadias;
    this.auditoria = auditoria;
    this.confirmacao = confirmacao;
    this.relogio = relogio;
  }

  /** Cliente, contrato e veículos juntos, numa transação: ou entra tudo, ou nada. */
  @Transactional
  public long cadastrarRapido(CadastroRapidoForm form, long administradorId) {
    OffsetDateTime agora = OffsetDateTime.now(relogio);
    DadosDoCliente dados = normalizar(form.cliente(), "cliente.", null);
    if (form.fim() != null && form.fim().isBefore(form.inicio())) {
      throw new CampoInvalidoException("fim", "O fim do contrato precisa ser depois do início.");
    }
    List<Placa> placas = placasNovas(form.veiculosPreenchidos());

    Cliente cliente = clientes.save(new Cliente(dados, agora));
    contratos.save(new Contrato(cliente, form.vagas(), form.inicio(), form.fim(), agora));
    for (int i = 0; i < placas.size(); i++) {
      VeiculoNovo linha = form.veiculosPreenchidos().get(i);
      veiculos.save(
          new Veiculo(
              cliente,
              placas.get(i),
              vazioComoNulo(linha.modelo()),
              vazioComoNulo(linha.cor()),
              agora));
    }
    auditoria.registrar(
        administradorId,
        "Cadastrou cliente",
        "cliente",
        cliente.getId(),
        form.vagas() + " vagas e " + placas.size() + " veículos.");
    return cliente.getId();
  }

  @Transactional
  public void atualizar(long id, ClienteForm form, long administradorId) {
    Cliente cliente = buscar(id);
    cliente.atualizar(normalizar(form, "", id), OffsetDateTime.now(relogio));
    auditoria.registrar(administradorId, "Editou cliente", "cliente", id, "Dados cadastrais.");
  }

  /** Ação grave: pede a senha de novo. Os veículos param de entrar; quem está dentro pode sair. */
  @Transactional
  public void desativar(long id, String senha, long administradorId) {
    confirmacao.exigir(administradorId, senha);
    buscar(id).desativar(OffsetDateTime.now(relogio));
    auditoria.registrar(
        administradorId, "Desativou cliente", "cliente", id, "Entradas bloqueadas.");
  }

  @Transactional
  public void reativar(long id, long administradorId) {
    buscar(id).reativar(OffsetDateTime.now(relogio));
    auditoria.registrar(administradorId, "Reativou cliente", "cliente", id, "Entradas liberadas.");
  }

  @Transactional(readOnly = true)
  public Page<LinhaCliente> listar(
      String busca, TipoCliente tipo, boolean incluirInativos, int pagina) {
    Specification<Cliente> filtro =
        Filtros.todos(porBusca(busca), porTipo(tipo), incluirInativos ? null : apenasAtivos());
    return clientes
        .findAll(filtro, PageRequest.of(Math.max(pagina, 0), POR_PAGINA, Sort.by("nome")))
        .map(this::linha);
  }

  @Transactional(readOnly = true)
  public FichaDoCliente ficha(long id) {
    Cliente cliente = buscar(id);
    Optional<Contrato> contrato = contratos.vigenteDoCliente(id);
    List<FichaDoCliente.LinhaVeiculo> carros =
        veiculos.findAllByClienteIdOrderByAtivoDescPlacaAsc(id).stream()
            .map(v ->
                new FichaDoCliente.LinhaVeiculo(
                    v.getId(),
                    v.getPlaca(),
                    textoDoVeiculo(v),
                    v.isAtivo(),
                    estadias.existsByVeiculoIdAndSaidaEmIsNull(v.getId())))
            .toList();
    return new FichaDoCliente(
        cliente.getId(),
        cliente.getNome(),
        cliente.getTipo().getDescricao(),
        Formatos.documento(cliente.getDocumento()),
        cliente.getResponsavel(),
        Formatos.telefone(cliente.getTelefone()),
        cliente.getEmail(),
        cliente.isAtivo(),
        Formatos.dia(cliente.getCriadoEm(), relogio),
        contrato
            .map(c ->
                new FichaDoCliente.DadosDoContrato(
                    c.getId(),
                    c.getVagasContratadas(),
                    c.getSituacao().getDescricao(),
                    Formatos.data(c.getInicio()),
                    c.getFim() == null ? null : Formatos.data(c.getFim())))
            .orElse(null),
        carros);
  }

  @Transactional(readOnly = true)
  public ClienteForm formDeEdicao(long id) {
    Cliente c = buscar(id);
    return new ClienteForm(
        c.getTipo(),
        c.getNome(),
        Formatos.documento(c.getDocumento()),
        c.getResponsavel(),
        Formatos.telefone(c.getTelefone()),
        c.getEmail());
  }

  @Transactional(readOnly = true)
  public List<OpcaoCliente> ativosParaEscolha() {
    return clientes.findAllByAtivoTrueOrderByNomeAsc().stream()
        .map(c -> new OpcaoCliente(c.getId(), c.getNome()))
        .toList();
  }

  public record OpcaoCliente(long id, String nome) {}

  private Cliente buscar(long id) {
    return clientes
        .findById(id)
        .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));
  }

  private DadosDoCliente normalizar(ClienteForm form, String prefixo, Long idAtual) {
    String documento = Formatos.somenteDigitos(form.documento());
    if (!Documento.valido(form.tipo(), documento)) {
      throw new CampoInvalidoException(
          prefixo + "documento",
          form.tipo() == TipoCliente.PF
              ? "CPF inválido. Confira os 11 números."
              : "CNPJ inválido. Confira os 14 números.");
    }
    boolean repetido =
        idAtual == null
            ? clientes.existsByDocumento(documento)
            : clientes.existsByDocumentoAndIdNot(documento, idAtual);
    if (repetido) {
      throw new CampoInvalidoException(
          prefixo + "documento", "Já existe um cliente com este documento.");
    }
    return new DadosDoCliente(
        form.tipo(),
        form.nome().trim(),
        documento,
        vazioComoNulo(form.responsavel()),
        Formatos.telefoneParaGuardar(form.telefone()),
        vazioComoNulo(form.email() == null ? null : form.email().trim().toLowerCase()));
  }

  private List<Placa> placasNovas(List<VeiculoNovo> linhas) {
    Set<String> vistas = new HashSet<>();
    return linhas.stream()
        .map(linha -> {
          Placa placa = Placa.de(linha.placa());
          if (!vistas.add(placa.valor())) {
            throw new CampoInvalidoException(
                "veiculos", "A placa " + placa + " aparece duas vezes.");
          }
          if (veiculos.existsByPlacaAndAtivoTrue(placa.valor())) {
            throw new CampoInvalidoException(
                "veiculos", "A placa " + placa + " já está cadastrada para outro cliente.");
          }
          return placa;
        })
        .toList();
  }

  private LinhaCliente linha(Cliente cliente) {
    Optional<Contrato> contrato = contratos.vigenteDoCliente(cliente.getId());
    int emUso =
        contrato.map(c -> (int) estadias.countByContratoIdAndSaidaEmIsNull(c.getId())).orElse(0);
    return new LinhaCliente(
        cliente.getId(),
        cliente.getNome(),
        cliente.getTipo().name(),
        Formatos.documento(cliente.getDocumento()),
        contrato.map(Contrato::getVagasContratadas).orElse(0),
        emUso,
        contrato.map(c -> c.getSituacao().getDescricao()).orElse("Sem contrato"),
        cliente.isAtivo());
  }

  private static String textoDoVeiculo(Veiculo v) {
    String modelo = v.getModelo() == null ? "" : v.getModelo();
    String cor = v.getCor() == null ? "" : v.getCor();
    return (modelo + " " + cor).trim();
  }

  private static String vazioComoNulo(String texto) {
    return texto == null || texto.isBlank() ? null : texto.trim();
  }

  private static Specification<Cliente> porBusca(String busca) {
    if (busca == null || busca.isBlank()) {
      return null;
    }
    String texto = "%" + busca.trim().toLowerCase() + "%";
    String digitos = Formatos.somenteDigitos(busca);
    return (raiz, consulta, cb) ->
        digitos.length() >= 3
            ? cb.or(
                cb.like(cb.lower(raiz.get("nome")), texto),
                cb.like(raiz.get("documento"), "%" + digitos + "%"))
            : cb.like(cb.lower(raiz.get("nome")), texto);
  }

  private static Specification<Cliente> porTipo(TipoCliente tipo) {
    return tipo == null ? null : (raiz, consulta, cb) -> cb.equal(raiz.get("tipo"), tipo);
  }

  private static Specification<Cliente> apenasAtivos() {
    return (raiz, consulta, cb) -> cb.isTrue(raiz.get("ativo"));
  }

  public record LinhaCliente(
      long id,
      String nome,
      String tipo,
      String documento,
      int vagas,
      int emUso,
      String situacao,
      boolean ativo) {}
}
