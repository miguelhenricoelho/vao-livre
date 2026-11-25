package br.com.vaolivre.contratos;

public enum SituacaoContrato {
  ATIVO("Ativo"),
  SUSPENSO("Suspenso"),
  ENCERRADO("Encerrado");

  private final String descricao;

  SituacaoContrato(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  public boolean isVigente() {
    return this != ENCERRADO;
  }
}
