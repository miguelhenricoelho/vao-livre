package br.com.vaolivre.global;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/** Padrão brasileiro: datas dd/mm/aaaa, horário de Brasília, CPF, CNPJ e telefone. */
public final class Formatos {

  public static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  public static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");
  public static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

  private static final Pattern NAO_DIGITO = Pattern.compile("[^0-9]");

  private Formatos() {}

  public static String somenteDigitos(String texto) {
    return texto == null ? "" : NAO_DIGITO.matcher(texto).replaceAll("");
  }

  public static String data(LocalDate data) {
    return data == null ? "" : DATA.format(data);
  }

  public static String dia(OffsetDateTime momento, Clock relogio) {
    return momento == null ? "" : DATA.format(momento.atZoneSameInstant(relogio.getZone()));
  }

  public static String hora(OffsetDateTime momento, Clock relogio) {
    return momento == null ? "" : HORA.format(momento.atZoneSameInstant(relogio.getZone()));
  }

  public static String dataHora(OffsetDateTime momento, Clock relogio) {
    return momento == null ? "" : DATA_HORA.format(momento.atZoneSameInstant(relogio.getZone()));
  }

  /** "1 h 48 min", "25 min" ou "menos de 1 min". */
  public static String duracao(Duration duracao) {
    long minutos = duracao.toMinutes();
    if (minutos < 1) {
      return "menos de 1 min";
    }
    long horas = minutos / 60;
    long resto = minutos % 60;
    if (horas == 0) {
      return resto + " min";
    }
    return resto == 0 ? horas + " h" : horas + " h " + resto + " min";
  }

  public static String documento(String digitos) {
    if (digitos == null) {
      return "";
    }
    if (digitos.length() == 11) {
      return digitos.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
    if (digitos.length() == 14) {
      return digitos.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }
    return digitos;
  }

  /** Guarda com o 55 do Brasil (12 ou 13 dígitos) e mostra como (11) 90000-0001. */
  public static String telefone(String digitos) {
    if (digitos == null || digitos.length() < 12) {
      return digitos == null ? "" : digitos;
    }
    String nacional = digitos.substring(2);
    String ddd = nacional.substring(0, 2);
    String numero = nacional.substring(2);
    int corte = numero.length() - 4;
    return "(" + ddd + ") " + numero.substring(0, corte) + "-" + numero.substring(corte);
  }

  /** Aceita "(11) 90000-0001" ou "11900000001" e devolve "5511900000001". */
  public static String telefoneParaGuardar(String digitado) {
    String digitos = somenteDigitos(digitado);
    if (digitos.isEmpty()) {
      return null;
    }
    return digitos.startsWith("55") && digitos.length() >= 12 ? digitos : "55" + digitos;
  }
}
