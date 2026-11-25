package br.com.vaolivre.ocorrencias;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OcorrenciasRepository
    extends JpaRepository<Ocorrencia, Long>, JpaSpecificationExecutor<Ocorrencia> {

  @Query(
      "select o from Ocorrencia o left join fetch o.cliente left join fetch o.estadia"
          + " join fetch o.operador where o.id = :id")
  Optional<Ocorrencia> completa(Long id);

  long countByResultadoAndOcorridoEmGreaterThanEqual(Resultado resultado, OffsetDateTime desde);

  long countByOcorridoEmGreaterThanEqual(OffsetDateTime desde);

  @Query(
      "select o from Ocorrencia o left join fetch o.cliente join fetch o.operador"
          + " where o.cliente.id = :clienteId order by o.ocorridoEm desc")
  List<Ocorrencia> recentesDoCliente(Long clienteId, Pageable pagina);

  @Query(
      "select o from Ocorrencia o left join fetch o.cliente join fetch o.operador"
          + " order by o.ocorridoEm desc")
  List<Ocorrencia> recentes(Pageable pagina);
}
