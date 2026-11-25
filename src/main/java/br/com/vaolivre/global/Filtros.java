package br.com.vaolivre.global;

import org.springframework.data.jpa.domain.Specification;

/** Junta os filtros de uma busca, ignorando os que vieram vazios. */
public final class Filtros {

  private Filtros() {}

  @SafeVarargs
  public static <T> Specification<T> todos(Specification<T>... partes) {
    Specification<T> resultado = (raiz, consulta, cb) -> cb.conjunction();
    for (Specification<T> parte : partes) {
      if (parte != null) {
        resultado = resultado.and(parte);
      }
    }
    return resultado;
  }
}
