package br.com.vaolivre.estadias;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EstadiasRepository extends JpaRepository<Estadia, Long> {

  boolean existsByVeiculoIdAndSaidaEmIsNull(Long veiculoId);

  @Query(
      "select e.vagaNoQuadro from Estadia e where e.contrato.id = :contratoId and e.saidaEm is null")
  List<Integer> vagasOcupadas(Long contratoId);

  long countByContratoIdAndSaidaEmIsNull(Long contratoId);

  long countBySaidaEmIsNull();

  @Query(
      "select e from Estadia e join fetch e.veiculo v where e.contrato.id = :contratoId"
          + " and e.saidaEm is null order by e.vagaNoQuadro")
  List<Estadia> abertasDoContrato(Long contratoId);

  @Query(
      "select e from Estadia e join fetch e.veiculo v join fetch v.cliente join fetch e.contrato"
          + " where v.placa = :placa and e.saidaEm is null")
  Optional<Estadia> abertaPelaPlaca(String placa);

  @Query(
      "select e from Estadia e join fetch e.veiculo v join fetch v.cliente"
          + " where e.saidaEm is null order by e.entradaEm")
  List<Estadia> todasAbertas();

  /** Contratos com mais carros dentro do que vagas. Precisa ser sempre zero. */
  @Query(
      "select count(c) from Contrato c where c.vagasContratadas < (select count(e) from Estadia e"
          + " where e.contrato = c and e.saidaEm is null)")
  long contratosAcimaDoLimite();
}
