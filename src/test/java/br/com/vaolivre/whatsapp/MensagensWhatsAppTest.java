package br.com.vaolivre.whatsapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

class MensagensWhatsAppTest {

  @Test
  void linkUsaPorCentoVinteNoLugarDoMaisEDecodificaIgual() {
    String link = MensagensWhatsApp.link("5511900000001", "Olá, Alfa. 5 de 5 (pelo sistema)");

    assertThat(link).startsWith("https://wa.me/5511900000001?text=").doesNotContain("+");
    String texto = URI.create(link).getRawQuery().substring("text=".length());
    assertThat(URLDecoder.decode(texto, StandardCharsets.UTF_8))
        .isEqualTo("Olá, Alfa. 5 de 5 (pelo sistema)");
  }

  @Test
  void mensagemDeVagasEsgotadasLevaTodasAsPlacas() {
    StaticMessageSource textos = new StaticMessageSource();
    textos.addMessage(
        "whatsapp.vagas-esgotadas",
        Locale.of("pt", "BR"),
        "Olá, {0}. O veículo {1} chegou às {2} de {3}, mas as {4} vagas estão em uso: {5}. (pelo sistema)");
    MensagensWhatsApp whatsapp = new MensagensWhatsApp(textos);

    String link =
        whatsapp.vagasEsgotadas(
            "5511900000001",
            "Alfa",
            "SPA1B02",
            "08:20",
            "25/11/2026",
            2,
            List.of("BRA2E19", "QTP4A21"));
    String texto =
        URLDecoder.decode(link.substring(link.indexOf("text=") + 5), StandardCharsets.UTF_8);

    assertThat(texto)
        .isEqualTo(
            "Olá, Alfa. O veículo SPA1B02 chegou às 08:20 de 25/11/2026, mas as 2 vagas estão em uso:"
                + " BRA2E19 e QTP4A21. (pelo sistema)");
  }
}
