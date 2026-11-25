package br.com.vaolivre.global;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

/** Monta o link de outra página da lista mantendo os filtros já escolhidos. */
@Component("paginacao")
public class Paginacao {

  public String link(int pagina) {
    HttpServletRequest pedido =
        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    String consulta = pedido.getQueryString();
    return UriComponentsBuilder.fromPath(pedido.getRequestURI())
        .query(consulta)
        .replaceQueryParam("pagina", Math.max(pagina, 0))
        .build()
        .toUriString();
  }
}
