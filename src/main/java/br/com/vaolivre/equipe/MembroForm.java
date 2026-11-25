package br.com.vaolivre.equipe;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Nova pessoa da equipe. A senha é temporária: ela troca no primeiro acesso. */
public record MembroForm(
    @NotBlank(message = "Digite o nome.") @Size(max = 120, message = "Use até 120 caracteres.")
        String nome,
    @NotBlank(message = "Digite o e-mail.") @Email(message = "Confira o e-mail.") String email,
    @NotNull(message = "Escolha o papel.") Papel papel,
    @NotBlank(message = "Digite uma senha temporária.")
        @Size(min = 10, message = "A senha temporária precisa ter pelo menos 10 caracteres.")
        String senhaTemporaria) {

  public static MembroForm vazio() {
    return new MembroForm("", "", Papel.OPERADOR, "");
  }
}
