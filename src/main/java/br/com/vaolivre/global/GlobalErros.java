package br.com.vaolivre.global;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Erros centralizados, com páginas na voz da marca e sem detalhe interno.
 *
 * <p>Os erros que o Spring trata sozinho (endereço inexistente, rota sem permissão) usam os modelos
 * {@code partes/error/4xx.html} e {@code 5xx.html}, na pasta que o Spring exige.
 */
@ControllerAdvice
public class GlobalErros {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalErros.class);
  private static final String VIEW = "error/error";

  /** Registro inexistente, endereço que não existe ou id que não é número: tudo vira 404. */
  @ExceptionHandler({
    NaoEncontradoException.class,
    NoHandlerFoundException.class,
    NoResourceFoundException.class,
    MethodArgumentTypeMismatchException.class
  })
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ModelAndView naoEncontrado() {
    return new ModelAndView(VIEW, Map.of("status", 404), HttpStatus.NOT_FOUND);
  }

  /** Volta para a tela de onde a pessoa veio, com a mensagem da regra que impediu a ação. */
  @ExceptionHandler(RegraDeNegocioException.class)
  public String regraDeNegocio(
      RegraDeNegocioException erro, HttpServletRequest pedido, RedirectAttributes atributos) {
    atributos.addFlashAttribute("erro", erro.getMessage());
    return "redirect:" + caminhoDeVolta(pedido);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ModelAndView inesperado(Exception erro) {
    LOG.error("Erro inesperado ao atender o pedido", erro);
    return new ModelAndView(VIEW, Map.of("status", 500), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /** Só aceita voltar para um caminho deste próprio sistema. */
  static String caminhoDeVolta(HttpServletRequest pedido) {
    String origem = pedido.getHeader("Referer");
    if (origem == null || origem.isBlank()) {
      return "/";
    }
    URI uri;
    try {
      uri = URI.create(origem);
    } catch (IllegalArgumentException invalido) {
      LOG.debug("Referer inválido ignorado");
      return "/";
    }
    if (uri.getHost() != null && !uri.getHost().equalsIgnoreCase(pedido.getServerName())) {
      return "/";
    }
    String caminho = uri.getRawPath();
    if (caminho == null || caminho.isBlank() || !caminho.startsWith("/")) {
      return "/";
    }
    return uri.getRawQuery() == null ? caminho : caminho + "?" + uri.getRawQuery();
  }
}
