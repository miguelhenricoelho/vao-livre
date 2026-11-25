package br.com.vaolivre.global;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

/**
 * Configuração comum: recursos estáticos, relógio e idioma.
 *
 * <p>Os modelos do Thymeleaf (.html) ficam na mesma pasta que o CSS e o JS de cada parte, então só
 * as extensões listadas aqui são servidas. Um .html nunca é entregue cru ao navegador.
 */
@Configuration
public class GlobalConfig implements WebMvcConfigurer {

  public static final String[] ARQUIVOS_PUBLICOS = {"/*/*.css", "/*/*.js", "/*/*.svg"};

  private final String partes;

  public GlobalConfig(@Value("${app.partes}") String partes) {
    this.partes = partes;
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registro) {
    registro.addResourceHandler(ARQUIVOS_PUBLICOS).addResourceLocations(partes);
  }

  @Bean
  Clock relogio(@Value("${app.fuso}") String fuso) {
    return Clock.system(ZoneId.of(fuso));
  }

  @Bean
  LocaleResolver localeResolver() {
    return new FixedLocaleResolver(Locale.of("pt", "BR"));
  }
}
