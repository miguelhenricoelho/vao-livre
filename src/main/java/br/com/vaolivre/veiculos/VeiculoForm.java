package br.com.vaolivre.veiculos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VeiculoForm(
    @NotNull(message = "Escolha o cliente.") Long clienteId,
    @NotBlank(message = "Digite a placa.")
        @Pattern(
            regexp = "(?i)^\\s*[A-Z]{3}[\\s-]?[0-9][A-Z0-9][0-9]{2}\\s*$",
            message = "Placa fora do formato (ABC1234 ou ABC1D23).")
        String placa,
    @Size(max = 80, message = "Use até 80 caracteres.") String modelo,
    @Size(max = 30, message = "Use até 30 caracteres.") String cor) {

  public static VeiculoForm vazio(Long clienteId) {
    return new VeiculoForm(clienteId, "", "", "");
  }
}
