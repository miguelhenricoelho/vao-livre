package br.com.vaolivre.auditoria;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {

  @EntityGraph(attributePaths = "usuario")
  Page<RegistroAuditoria> findAllByOrderByOcorridoEmDesc(Pageable pagina);
}
