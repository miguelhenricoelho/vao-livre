package br.com.vaolivre.clientes;

/** Confere os dígitos verificadores de CPF e CNPJ. */
public final class Documento {

  private Documento() {}

  public static boolean valido(TipoCliente tipo, String digitos) {
    if (digitos == null || digitos.length() != tipo.getDigitosDoDocumento()) {
      return false;
    }
    if (digitos.chars().distinct().count() == 1) {
      return false;
    }
    return tipo == TipoCliente.PF ? cpfValido(digitos) : cnpjValido(digitos);
  }

  static boolean cpfValido(String cpf) {
    return digito(cpf, 9, 10) == cpf.charAt(9) - '0' && digito(cpf, 10, 11) == cpf.charAt(10) - '0';
  }

  private static int digito(String cpf, int quantidade, int pesoInicial) {
    int soma = 0;
    for (int i = 0; i < quantidade; i++) {
      soma += (cpf.charAt(i) - '0') * (pesoInicial - i);
    }
    int resto = (soma * 10) % 11;
    return resto == 10 ? 0 : resto;
  }

  static boolean cnpjValido(String cnpj) {
    int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
    return digitoCnpj(cnpj, pesos1) == cnpj.charAt(12) - '0'
        && digitoCnpj(cnpj, pesos2) == cnpj.charAt(13) - '0';
  }

  private static int digitoCnpj(String cnpj, int[] pesos) {
    int soma = 0;
    for (int i = 0; i < pesos.length; i++) {
      soma += (cnpj.charAt(i) - '0') * pesos[i];
    }
    int resto = soma % 11;
    return resto < 2 ? 0 : 11 - resto;
  }
}
