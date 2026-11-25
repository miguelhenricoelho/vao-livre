package br.com.vaolivre.acesso;

import br.com.vaolivre.equipe.UsuariosRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Carrega a pessoa da equipe pelo e-mail no login. */
@Service
public class DetalhesDosUsuarios implements UserDetailsService {

  private final UsuariosRepository usuarios;

  public DetalhesDosUsuarios(UsuariosRepository usuarios) {
    this.usuarios = usuarios;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) {
    return usuarios
        .buscarPorEmail(email == null ? "" : email.trim())
        .map(UsuarioLogado::new)
        .orElseThrow(() -> new UsernameNotFoundException("E-mail ou senha incorretos"));
  }
}
