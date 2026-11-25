package br.com.vaolivre.equipe;

import br.com.vaolivre.acesso.ConfirmacaoDeSenha;
import br.com.vaolivre.auditoria.AuditoriaService;
import br.com.vaolivre.global.CampoInvalidoException;
import br.com.vaolivre.global.NaoEncontradoException;
import br.com.vaolivre.global.RegraDeNegocioException;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Contas da equipe, criadas pelo administrador. Papel fica na tabela e só o administrador muda. */
@Service
public class EquipeService {

  private final UsuariosRepository usuarios;
  private final PasswordEncoder codificador;
  private final ConfirmacaoDeSenha confirmacao;
  private final AuditoriaService auditoria;

  public EquipeService(
      UsuariosRepository usuarios,
      PasswordEncoder codificador,
      ConfirmacaoDeSenha confirmacao,
      AuditoriaService auditoria) {
    this.usuarios = usuarios;
    this.codificador = codificador;
    this.confirmacao = confirmacao;
    this.auditoria = auditoria;
  }

  @Transactional(readOnly = true)
  public List<LinhaEquipe> listar() {
    return usuarios.findAllByOrderByAtivoDescNomeAsc().stream()
        .map(u ->
            new LinhaEquipe(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getPapel(),
                u.isAtivo(),
                u.isDeveTrocarSenha()))
        .toList();
  }

  @Transactional
  public void criar(MembroForm form, long administradorId) {
    if (usuarios.existsByEmailIgnoreCase(form.email().trim())) {
      throw new CampoInvalidoException("email", "Já existe alguém da equipe com este e-mail.");
    }
    Usuario novo =
        usuarios.save(
            new Usuario(
                form.nome().trim(),
                form.email(),
                codificador.encode(form.senhaTemporaria()),
                form.papel(),
                true));
    auditoria.registrar(
        administradorId,
        "Criou conta da equipe",
        "usuario",
        novo.getId(),
        "Papel " + form.papel().getDescricao() + ".");
  }

  @Transactional
  public void mudarPapel(long id, Papel papel, String senha, long administradorId) {
    confirmacao.exigir(administradorId, senha);
    Usuario usuario = buscar(id);
    protegerUltimoAdministrador(usuario, id, administradorId, papel != Papel.ADMIN);
    usuario.mudarPapel(papel);
    auditoria.registrar(
        administradorId, "Mudou papel", "usuario", id, "Novo papel: " + papel.getDescricao() + ".");
  }

  @Transactional
  public void desativar(long id, String senha, long administradorId) {
    confirmacao.exigir(administradorId, senha);
    Usuario usuario = buscar(id);
    protegerUltimoAdministrador(usuario, id, administradorId, true);
    usuario.desativar();
    auditoria.registrar(administradorId, "Desativou conta", "usuario", id, "Acesso encerrado.");
  }

  @Transactional
  public void reativar(long id, long administradorId) {
    buscar(id).reativar();
    auditoria.registrar(administradorId, "Reativou conta", "usuario", id, "Acesso liberado.");
  }

  /** Senha esquecida: o administrador define uma temporária, que a pessoa troca ao entrar. */
  @Transactional
  public void redefinirSenha(long id, String temporaria, String senha, long administradorId) {
    confirmacao.exigir(administradorId, senha);
    if (temporaria == null || temporaria.length() < 10) {
      throw new RegraDeNegocioException("A senha temporária precisa ter pelo menos 10 caracteres.");
    }
    buscar(id).trocarSenha(codificador.encode(temporaria), true);
    auditoria.registrar(
        administradorId, "Redefiniu senha", "usuario", id, "Senha temporária definida.");
  }

  private void protegerUltimoAdministrador(
      Usuario alvo, long id, long administradorId, boolean perdeAdmin) {
    if (id == administradorId) {
      throw new RegraDeNegocioException("Você não pode tirar o próprio acesso de administrador.");
    }
    if (perdeAdmin
        && alvo.getPapel() == Papel.ADMIN
        && usuarios.countByPapelAndAtivoTrue(Papel.ADMIN) <= 1) {
      throw new RegraDeNegocioException("O sistema precisa de pelo menos um administrador ativo.");
    }
  }

  private Usuario buscar(long id) {
    return usuarios
        .findById(id)
        .orElseThrow(() -> new NaoEncontradoException("Pessoa não encontrada"));
  }

  public record LinhaEquipe(
      long id, String nome, String email, Papel papel, boolean ativo, boolean senhaTemporaria) {}
}
