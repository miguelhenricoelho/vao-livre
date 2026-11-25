package br.com.vaolivre.contratos;

import br.com.vaolivre.acesso.ConfirmacaoDeSenha;
import br.com.vaolivre.auditoria.AuditoriaService;
import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.clientes.ClientesRepository;
import br.com.vaolivre.estadias.Estadia;
import br.com.vaolivre.estadias.EstadiasRepository;
import br.com.vaolivre.global.CampoInvalidoException;
import br.com.vaolivre.global.NaoEncontradoException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Contrato de vagas: criar um novo e alterar o vigente, sem nunca deixar carro sem vaga. */
@Service
public class ContratosService {

  private final ContratosRepository contratos;
  private final ClientesRepository clientes;
  private final EstadiasRepository estadias;
  private final ConfirmacaoDeSenha confirmacao;
  private final AuditoriaService auditoria;
  private final Clock relogio;

  public ContratosService(
      ContratosRepository contratos,
      ClientesRepository clientes,
      EstadiasRepository estadias,
      ConfirmacaoDeSenha confirmacao,
      AuditoriaService auditoria,
      Clock relogio) {
    this.contratos = contratos;
    this.clientes = clientes;
    this.estadias = estadias;
    this.confirmacao = confirmacao;
    this.auditoria = auditoria;
    this.relogio = relogio;
  }

  @Transactional(readOnly = true)
  public Optional<ContratoParaEditar> vigente(long clienteId) {
    Cliente cliente = cliente(clienteId);
    return contratos
        .vigenteDoCliente(clienteId)
        .map(c ->
            new ContratoParaEditar(
                cliente.getId(),
                cliente.getNome(),
                c.getId(),
                (int) estadias.countByContratoIdAndSaidaEmIsNull(c.getId()),
                new ContratoForm(
                    c.getVagasContratadas(), c.getInicio(), c.getFim(), c.getSituacao(), "")));
  }

  @Transactional(readOnly = true)
  public String nomeDoCliente(long clienteId) {
    return cliente(clienteId).getNome();
  }

  /** Mudar vagas é ação grave: pede a senha, trava o contrato e confere quem está dentro. */
  @Transactional
  public void salvar(long clienteId, ContratoForm form, long administradorId) {
    confirmacao.exigir(administradorId, form.senha());
    if (form.fim() != null && form.fim().isBefore(form.inicio())) {
      throw new CampoInvalidoException("fim", "O fim do contrato precisa ser depois do início.");
    }
    Optional<Contrato> vigente = contratos.travarVigenteDoCliente(clienteId);
    if (vigente.isEmpty()) {
      Contrato novo =
          contratos.save(
              new Contrato(
                  cliente(clienteId),
                  form.vagas(),
                  form.inicio(),
                  form.fim(),
                  OffsetDateTime.now(relogio)));
      auditoria.registrar(
          administradorId, "Criou contrato", "contrato", novo.getId(), form.vagas() + " vagas.");
      return;
    }
    Contrato contrato = vigente.get();
    List<Estadia> dentro = estadias.abertasDoContrato(contrato.getId());
    if (form.situacao() == SituacaoContrato.ENCERRADO && !dentro.isEmpty()) {
      throw new CampoInvalidoException(
          "situacao",
          "Há " + dentro.size() + " veículo(s) deste cliente no pátio. Encerre depois que saírem.");
    }
    dentro.stream()
        .filter(e -> e.getVagaNoQuadro() > form.vagas())
        .findFirst()
        .ifPresent(e -> {
          throw new CampoInvalidoException(
              "vagas",
              "A vaga "
                  + e.getVagaNoQuadro()
                  + " está ocupada por "
                  + e.getVeiculo().getPlaca()
                  + ". Reduza as vagas depois que esse veículo sair.");
        });
    int antes = contrato.getVagasContratadas();
    contrato.alterar(form.vagas(), form.inicio(), form.fim(), form.situacao());
    auditoria.registrar(
        administradorId,
        "Alterou contrato",
        "contrato",
        contrato.getId(),
        "Vagas de "
            + antes
            + " para "
            + form.vagas()
            + "; situação "
            + form.situacao().getDescricao()
            + ".");
  }

  private Cliente cliente(long id) {
    return clientes
        .findById(id)
        .orElseThrow(() -> new NaoEncontradoException("Cliente não encontrado"));
  }

  public record ContratoParaEditar(
      long clienteId, String cliente, long contratoId, int emUso, ContratoForm form) {}
}
