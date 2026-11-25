package br.com.vaolivre.contratos;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ContratosRepository extends JpaRepository<Contrato, Long> {

  /**
   * Trava a linha do contrato vigente até o fim da transação ({@code SELECT ... FOR UPDATE}). Duas
   * entradas do mesmo cliente ao mesmo tempo esperam uma pela outra, e a contagem de vagas nunca
   * estoura.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      "select c from Contrato c where c.cliente.id = :clienteId"
          + " and c.situacao in (br.com.vaolivre.contratos.SituacaoContrato.ATIVO,"
          + " br.com.vaolivre.contratos.SituacaoContrato.SUSPENSO)")
  Optional<Contrato> travarVigenteDoCliente(Long clienteId);

  @Query(
      "select c from Contrato c where c.cliente.id = :clienteId"
          + " and c.situacao in (br.com.vaolivre.contratos.SituacaoContrato.ATIVO,"
          + " br.com.vaolivre.contratos.SituacaoContrato.SUSPENSO)")
  Optional<Contrato> vigenteDoCliente(Long clienteId);

  List<Contrato> findAllByClienteIdOrderByInicioDesc(Long clienteId);

  @Query(
      "select c from Contrato c join fetch c.cliente where c.situacao in"
          + " (br.com.vaolivre.contratos.SituacaoContrato.ATIVO,"
          + " br.com.vaolivre.contratos.SituacaoContrato.SUSPENSO)")
  List<Contrato> vigentes();
}
