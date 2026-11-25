package br.com.vaolivre.clientes;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ClientesRepository
    extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {

  boolean existsByDocumento(String documento);

  boolean existsByDocumentoAndIdNot(String documento, Long id);

  List<Cliente> findAllByAtivoTrueOrderByNomeAsc();
}
