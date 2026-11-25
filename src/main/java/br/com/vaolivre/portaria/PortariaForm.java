package br.com.vaolivre.portaria;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/** Placa digitada na portaria. Aceita minúsculas, hífen e espaço; o service normaliza. */
public record PortariaForm(
    @NotBlank(message = "Digite a placa do veículo.")
        @Pattern(
            regexp = "(?i)^\\s*[A-Z]{3}[\\s-]?[0-9][A-Z0-9][0-9]{2}\\s*$",
            message = "Placa fora do formato. Use o antigo (ABC1234) ou o Mercosul (ABC1D23).")
        String placa) {

  public static PortariaForm vazio() {
    return new PortariaForm("");
  }
}
