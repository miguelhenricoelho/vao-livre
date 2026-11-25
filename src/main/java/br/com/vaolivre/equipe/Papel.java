package br.com.vaolivre.equipe;

/** Papéis da equipe. Ficam na tabela usuario, e só um administrador muda. */
public enum Papel {
  ADMIN("Administrador"),
  OPERADOR("Operador da portaria");

  private final String descricao;

  Papel(String descricao) {
    this.descricao = descricao;
  }

  public String getDescricao() {
    return descricao;
  }

  public String autoridade() {
    return "ROLE_" + name();
  }
}
