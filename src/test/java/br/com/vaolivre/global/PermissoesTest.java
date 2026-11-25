package br.com.vaolivre.global;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.vaolivre.acesso.AcessoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/** Permissões de verdade: rotas de administrador, as duas senhas, CSRF e os modelos .html. */
class PermissoesTest extends BancoDeTeste {

  @Autowired WebApplicationContext contexto;
  @Autowired AcessoService acesso;

  private MockMvc mvc;
  private int versao;

  @BeforeEach
  void preparar() {
    mvc = MockMvcBuilders.webAppContextSetup(contexto).apply(springSecurity()).build();
    versao = acesso.versaoAtual();
  }

  @Test
  void semASenhaDaAreaTudoVoltaParaEntrar() throws Exception {
    mvc.perform(get("/portaria")).andExpect(redirectedUrl("/entrar"));
    mvc.perform(get("/login")).andExpect(redirectedUrl("/entrar"));
  }

  @Test
  void comASenhaDaAreaMasSemLoginVaiParaOLogin() throws Exception {
    mvc.perform(get("/portaria").sessionAttr(AcessoService.ATRIBUTO_DA_SESSAO, versao))
        .andExpect(status().is3xxRedirection())
        .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/login")));
  }

  @Test
  @WithMockUser(roles = "OPERADOR")
  void operadorNaoAbreRotasDeAdministrador() throws Exception {
    for (String rota :
        new String[] {
          "/painel",
          "/clientes",
          "/clientes/1",
          "/veiculos",
          "/equipe",
          "/auditoria",
          "/contratos/1"
        }) {
      mvc.perform(get(rota).sessionAttr(AcessoService.ATRIBUTO_DA_SESSAO, versao))
          .andExpect(status().isForbidden());
    }
  }

  @Test
  @WithMockUser(roles = "OPERADOR")
  void operadorNaoAlteraDadosPelaUrl() throws Exception {
    mvc.perform(
            post("/clientes/1/desativar")
                .param("senha", "qualquer")
                .with(csrf())
                .sessionAttr(AcessoService.ATRIBUTO_DA_SESSAO, versao))
        .andExpect(status().isForbidden());
    mvc.perform(
            post("/equipe")
                .param("nome", "Intruso")
                .with(csrf())
                .sessionAttr(AcessoService.ATRIBUTO_DA_SESSAO, versao))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void formularioSemCsrfERecusado() throws Exception {
    mvc.perform(
            post("/portaria/entrada")
                .param("placa", "ABC1234")
                .sessionAttr(AcessoService.ATRIBUTO_DA_SESSAO, versao))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser(roles = "ADMIN")
  void modeloHtmlNuncaEEntregueCru() throws Exception {
    for (String caminho :
        new String[] {
          "/clientes/clientes.html", "/portaria/portaria.html", "/global/global.html"
        }) {
      String corpo =
          mvc.perform(get(caminho).sessionAttr(AcessoService.ATRIBUTO_DA_SESSAO, versao))
              .andExpect(status().is4xxClientError())
              .andReturn()
              .getResponse()
              .getContentAsString();
      assertThat(corpo).doesNotContain("th:text").doesNotContain("th:fragment");
    }
  }

  @Test
  void cssEJsSaoPublicos() throws Exception {
    mvc.perform(get("/global/global.css")).andExpect(status().isOk());
    mvc.perform(get("/portaria/portaria.js")).andExpect(status().isOk());
  }

  @Test
  void cabecalhosDeSeguranca() throws Exception {
    mvc.perform(get("/entrar"))
        .andExpect(status().isOk())
        .andExpect(header().exists("Content-Security-Policy"))
        .andExpect(header().string("X-Content-Type-Options", "nosniff"))
        .andExpect(header().exists("Referrer-Policy"));
  }
}
