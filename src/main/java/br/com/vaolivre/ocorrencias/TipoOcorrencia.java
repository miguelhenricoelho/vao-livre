package br.com.vaolivre.ocorrencias;

public enum TipoOcorrencia {
  ENTRADA("Entrada"),
  SAIDA("Saída");

  private final String descricao;

  TipoOcorrencia(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }
}
