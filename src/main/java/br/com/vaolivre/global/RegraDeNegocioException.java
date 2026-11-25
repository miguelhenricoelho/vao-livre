package br.com.vaolivre.global;

/**
 * Uma regra do sistema impediu a ação. A mensagem é mostrada para a pessoa, na voz da marca, e por
 * isso nunca leva detalhe técnico.
 */
public class RegraDeNegocioException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public RegraDeNegocioException(String mensagem) {
    super(mensagem);
  }
}
