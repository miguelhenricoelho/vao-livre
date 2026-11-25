package br.com.vaolivre.veiculos;

import br.com.vaolivre.global.RegraDeNegocioException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Placa brasileira, no formato antigo (ABC1234) ou Mercosul (ABC1D23). Guarda só letras maiúsculas
 * e números, sem hífen nem espaço.
 */
public record Placa(String valor) {

  private static final Pattern FORMATO = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");
  private static final Pattern SEPARADORES = Pattern.compile("[^A-Z0-9]");

  public Placa {
    if (valor == null || !FORMATO.matcher(valor).matches()) {
      throw new RegraDeNegocioException(
          "Placa fora do formato. Use o antigo (ABC1234) ou o Mercosul (ABC1D23).");
    }
  }

  /** Aceita minúsculas, hífen e espaços, e devolve a placa normalizada. */
  public static Placa de(String digitada) {
    String limpa =
        digitada == null
            ? ""
            : SEPARADORES.matcher(digitada.toUpperCase(Locale.ROOT)).replaceAll("");
    return new Placa(limpa);
  }

  public static boolean valida(String digitada) {
    if (digitada == null) {
      return false;
    }
    String limpa = SEPARADORES.matcher(digitada.toUpperCase(Locale.ROOT)).replaceAll("");
    return FORMATO.matcher(limpa).matches();
  }

  @Override
  public String toString() {
    return valor;
  }
}
