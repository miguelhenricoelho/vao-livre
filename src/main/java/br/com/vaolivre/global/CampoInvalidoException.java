package br.com.vaolivre.global;

/**
 * Uma regra do service recusou um campo do formulário (documento repetido, placa já cadastrada). O
 * controller devolve o erro ao lado do campo, sem perder o que a pessoa digitou.
 */
public class CampoInvalidoException extends RegraDeNegocioException {

  private static final long serialVersionUID = 1L;

  private final String campo;

  public CampoInvalidoException(String campo, String mensagem) {
    super(mensagem);
    this.campo = campo;
  }

  public String getCampo() {
    return campo;
  }
}
