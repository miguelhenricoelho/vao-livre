package br.com.vaolivre.acesso;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConfiguracaoRepository extends JpaRepository<Configuracao, Short> {

  default Optional<Configuracao> unica() {
    return findById(Configuracao.ID_UNICO);
  }

  @Query("select c.versaoSenhaArea from Configuracao c where c.id = 1")
  Optional<Integer> versaoDaSenhaDaArea();
}
