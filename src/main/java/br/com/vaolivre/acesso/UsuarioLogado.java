package br.com.vaolivre.acesso;

import br.com.vaolivre.equipe.Papel;
import br.com.vaolivre.equipe.Usuario;
import java.io.Serial;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/** Quem está logado. Fica na sessão; nunca leva dado além do necessário. */
public final class UsuarioLogado implements UserDetails {

  @Serial private static final long serialVersionUID = 1L;

  private final long id;
  private final String nome;
  private final String email;
  private final String senhaHash;
  private final Papel papel;
  private final boolean ativo;

  public UsuarioLogado(Usuario usuario) {
    this.id = usuario.getId();
    this.nome = usuario.getNome();
    this.email = usuario.getEmail();
    this.senhaHash = usuario.getSenhaHash();
    this.papel = usuario.getPapel();
    this.ativo = usuario.isAtivo();
  }

  public long getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public Papel getPapel() {
    return papel;
  }

  public boolean isAdmin() {
    return papel == Papel.ADMIN;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(papel.autoridade()));
  }

  @Override
  public String getPassword() {
    return senhaHash;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return ativo;
  }
}
