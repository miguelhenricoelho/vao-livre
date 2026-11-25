package br.com.vaolivre.conta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TrocaSenhaForm(
    @NotBlank(message = "Digite a senha atual.") String atual,
    @NotBlank(message = "Digite a nova senha.")
        @Size(min = 10, message = "A nova senha precisa ter pelo menos 10 caracteres.")
        String nova,
    @NotBlank(message = "Repita a nova senha.") String confirmacao) {

  public static TrocaSenhaForm vazio() {
    return new TrocaSenhaForm("", "", "");
  }
}
