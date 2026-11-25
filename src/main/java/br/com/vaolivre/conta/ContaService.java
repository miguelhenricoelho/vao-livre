package br.com.vaolivre.conta;

import br.com.vaolivre.equipe.Usuario;
import br.com.vaolivre.equipe.UsuariosRepository;
import br.com.vaolivre.global.CampoInvalidoException;
import br.com.vaolivre.global.NaoEncontradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContaService {

  private final UsuariosRepository usuarios;
  private final PasswordEncoder codificador;

  public ContaService(UsuariosRepository usuarios, PasswordEncoder codificador) {
    this.usuarios = usuarios;
    this.codificador = codificador;
  }

  @Transactional
  public void trocarSenha(long usuarioId, TrocaSenhaForm form) {
    Usuario usuario =
        usuarios
            .findById(usuarioId)
            .orElseThrow(() -> new NaoEncontradoException("Conta não encontrada"));
    if (!codificador.matches(form.atual(), usuario.getSenhaHash())) {
      throw new CampoInvalidoException("atual", "Senha atual incorreta.");
    }
    if (!form.nova().equals(form.confirmacao())) {
      throw new CampoInvalidoException("confirmacao", "As duas senhas novas não são iguais.");
    }
    if (form.nova().equals(form.atual())) {
      throw new CampoInvalidoException("nova", "Escolha uma senha diferente da atual.");
    }
    usuario.trocarSenha(codificador.encode(form.nova()), false);
  }
}
