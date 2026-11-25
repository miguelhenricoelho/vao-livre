package br.com.vaolivre.equipe;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuariosRepository extends JpaRepository<Usuario, Long> {

  @Query("select u from Usuario u where lower(u.email) = lower(:email)")
  Optional<Usuario> buscarPorEmail(String email);

  boolean existsByEmailIgnoreCase(String email);

  long countByPapelAndAtivoTrue(Papel papel);

  List<Usuario> findAllByOrderByAtivoDescNomeAsc();
}
