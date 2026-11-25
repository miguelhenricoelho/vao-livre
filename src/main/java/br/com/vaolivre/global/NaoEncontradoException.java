package br.com.vaolivre.global;

/** Registro pedido pelo endereço não existe. Vira a página 404. */
public class NaoEncontradoException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public NaoEncontradoException(String mensagem) {
    super(mensagem);
  }
}
