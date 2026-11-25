package br.com.vaolivre.global;

import br.com.vaolivre.acesso.AcessoService;
import br.com.vaolivre.acesso.LimiteDeLoginFiltro;
import br.com.vaolivre.acesso.SessaoDaEquipeFiltro;
import br.com.vaolivre.acesso.UsuarioLogado;
import br.com.vaolivre.equipe.UsuariosRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter.ReferrerPolicy;

/**
 * Tudo fechado por padrão e liberado rota por rota. Os papéis valem por rota (aqui) e por método
 * ({@code @PreAuthorize} nos controllers de administrador).
 */
@Configuration
@EnableMethodSecurity
public class GlobalSeguranca {

  private static final String CSP =
      "default-src 'self'; script-src 'self'; style-src 'self' https://fonts.googleapis.com;"
          + " font-src 'self' https://fonts.gstatic.com; img-src 'self' data:;"
          + " connect-src 'self'; frame-ancestors 'none'; form-action 'self';"
          + " base-uri 'self'; object-src 'none'";

  @Bean
  PasswordEncoder codificadorDeSenha() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  SecurityFilterChain cadeiaDeSeguranca(
      HttpSecurity http, AcessoService acesso, UsuariosRepository usuarios) throws Exception {
    http.authorizeHttpRequests(regras ->
            regras
                .requestMatchers(GlobalConfig.ARQUIVOS_PUBLICOS)
                .permitAll()
                .requestMatchers("/entrar", "/login", "/error")
                .permitAll()
                .requestMatchers(
                    "/painel/**",
                    "/clientes/**",
                    "/contratos/**",
                    "/veiculos/**",
                    "/equipe/**",
                    "/auditoria/**")
                .hasRole("ADMIN")
                .requestMatchers(
                    "/", "/portaria/**", "/historico/**", "/ajuda", "/privacidade", "/conta/**")
                .hasAnyRole("ADMIN", "OPERADOR")
                .anyRequest()
                .authenticated())
        .formLogin(login ->
            login
                .loginPage("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .successHandler(depoisDoLogin())
                .failureUrl("/login?erro")
                .permitAll())
        .logout(sair -> sair.logoutUrl("/sair").logoutSuccessUrl("/entrar?saiu"))
        .sessionManagement(sessao -> sessao.sessionFixation(fixacao -> fixacao.changeSessionId()))
        .headers(cabecalhos ->
            cabecalhos
                .contentSecurityPolicy(csp -> csp.policyDirectives(CSP))
                .referrerPolicy(
                    politica -> politica.policy(ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                .httpStrictTransportSecurity(
                    hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31_536_000)))
        .addFilterAfter(
            new SessaoDaEquipeFiltro(acesso, usuarios), SecurityContextHolderFilter.class)
        .addFilterBefore(new LimiteDeLoginFiltro(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  /** Senha temporária vai direto para a troca; depois, cada papel cai na sua tela principal. */
  private static AuthenticationSuccessHandler depoisDoLogin() {
    return (pedido, resposta, autenticacao) -> {
      String destino = "/portaria";
      if (autenticacao.getPrincipal() instanceof UsuarioLogado eu && eu.isAdmin()) {
        destino = "/painel";
      }
      resposta.sendRedirect(pedido.getContextPath() + destino);
    };
  }
}
