package br.com.vaolivre.veiculos;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface VeiculosRepository
    extends JpaRepository<Veiculo, Long>, JpaSpecificationExecutor<Veiculo> {

  @Query("select v from Veiculo v join fetch v.cliente where v.placa = :placa and v.ativo = true")
  Optional<Veiculo> ativoPelaPlaca(String placa);

  @Query(
      "select v from Veiculo v join fetch v.cliente where v.placa = :placa"
          + " order by v.ativo desc, v.id desc limit 1")
  Optional<Veiculo> maisRecentePelaPlaca(String placa);

  boolean existsByPlacaAndAtivoTrue(String placa);

  List<Veiculo> findAllByClienteIdOrderByAtivoDescPlacaAsc(Long clienteId);

  long countByClienteIdAndAtivoTrue(Long clienteId);
}
