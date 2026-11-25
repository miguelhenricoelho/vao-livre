package br.com.vaolivre.whatsapp;

import br.com.vaolivre.ocorrencias.Motivo;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * Monta os links do WhatsApp com a mensagem pronta. Os textos ficam em
 * partes/portaria/conteudo.properties e partes/ajuda/conteudo.properties.
 */
@Component
public class MensagensWhatsApp {

  private static final Locale PT_BR = Locale.of("pt", "BR");

  private final MessageSource textos;

  public MensagensWhatsApp(MessageSource textos) {
    this.textos = textos;
  }

  /** {@code URLEncoder} troca espaço por "+"; o WhatsApp espera "%20". */
  public static String link(String numero, String mensagem) {
    return "https://wa.me/"
        + numero
        + "?text="
        + URLEncoder.encode(mensagem, StandardCharsets.UTF_8).replace("+", "%20");
  }

  public String vagasEsgotadas(
      String numero,
      String cliente,
      String placa,
      String hora,
      String data,
      int vagas,
      List<String> placasDentro) {
    String mensagem =
        textos.getMessage(
            "whatsapp.vagas-esgotadas",
            new Object[] {cliente, placa, hora, data, String.valueOf(vagas), lista(placasDentro)},
            PT_BR);
    return link(numero, mensagem);
  }

  /** Contrato vencido, suspenso ou que ainda não começou. */
  public String contratoImpedido(
      String numero,
      String cliente,
      String placa,
      String hora,
      String data,
      Motivo motivo,
      String dataDoContrato) {
    String situacao =
        textos.getMessage(
            "whatsapp.situacao." + motivo.name(),
            new Object[] {dataDoContrato == null ? "" : dataDoContrato},
            PT_BR);
    String mensagem =
        textos.getMessage(
            "whatsapp.contrato-impedido",
            new Object[] {cliente, placa, hora, data, situacao},
            PT_BR);
    return link(numero, mensagem);
  }

  public String pedidoDeAjuda(String numero, String nome, String tela) {
    String mensagem =
        textos.getMessage("whatsapp.pedido-de-ajuda", new Object[] {nome, tela}, PT_BR);
    return link(numero, mensagem);
  }

  /** "A, B e C". */
  static String lista(List<String> itens) {
    if (itens.isEmpty()) {
      return "";
    }
    if (itens.size() == 1) {
      return itens.getFirst();
    }
    return String.join(", ", itens.subList(0, itens.size() - 1)) + " e " + itens.getLast();
  }
}
