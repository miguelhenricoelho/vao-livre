package br.com.vaolivre.clientes;

public enum TipoCliente {
  PF("Pessoa física", 11),
  PJ("Pessoa jurídica", 14);

  private final String descricao;
  private final int digitosDoDocumento;

  TipoCliente(String descricao, int digitosDoDocumento) {
    this.descricao = descricao;
    this.digitosDoDocumento = digitosDoDocumento;
  }

  public String getDescricao() {
    return descricao;
  }

  public int getDigitosDoDocumento() {
    return digitosDoDocumento;
  }
}
