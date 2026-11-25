package br.com.vaolivre.clientes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Dados do cliente como vêm do formulário. Ativo e datas nunca vêm daqui. */
public record ClienteForm(
    @NotNull(message = "Escolha pessoa física ou jurídica.") TipoCliente tipo,
    @NotBlank(message = "Digite o nome ou a razão social.")
        @Size(max = 160, message = "Use até 160 caracteres.")
        String nome,
    @NotBlank(message = "Digite o CPF ou o CNPJ.") String documento,
    @Size(max = 120, message = "Use até 120 caracteres.") String responsavel,
    @Pattern(
            regexp = "^$|^[\\d\\s()+-]{10,20}$",
            message = "Digite o telefone com DDD, como (11) 90000-0000.")
        String telefone,
    @Email(message = "Confira o e-mail.") @Size(max = 160, message = "Use até 160 caracteres.")
        String email) {

  public static ClienteForm vazio() {
    return new ClienteForm(TipoCliente.PJ, "", "", "", "", "");
  }
}
