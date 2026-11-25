package br.com.vaolivre.acesso;

import br.com.vaolivre.equipe.UsuariosRepository;
import br.com.vaolivre.global.RegraDeNegocioException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Ações graves (mudar vagas, desativar cliente, mudar papel, desativar alguém, trocar a senha da
 * área) pedem a senha da pessoa de novo, na hora.
 */
@Component
public class ConfirmacaoDeSenha {

  private final UsuariosRepository usuarios;
  private final PasswordEncoder codificador;

  public ConfirmacaoDeSenha(UsuariosRepository usuarios, PasswordEncoder codificador) {
    this.usuarios = usuarios;
    this.codificador = codificador;
  }

  public void exigir(long usuarioId, String senhaDigitada) {
    String hash =
        usuarios
            .findById(usuarioId)
            .orElseThrow(() -> new RegraDeNegocioException("Sessão expirada. Entre de novo."))
            .getSenhaHash();
    if (senhaDigitada == null || !codificador.matches(senhaDigitada, hash)) {
      throw new RegraDeNegocioException(
          "Senha incorreta. Para esta ação, confirme com a sua senha de acesso.");
    }
  }
}
