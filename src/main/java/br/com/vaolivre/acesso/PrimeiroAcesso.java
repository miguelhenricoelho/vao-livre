package br.com.vaolivre.acesso;

import br.com.vaolivre.equipe.Papel;
import br.com.vaolivre.equipe.Usuario;
import br.com.vaolivre.equipe.UsuariosRepository;
import java.time.Clock;
import java.time.OffsetDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Com o banco vazio, cria o primeiro administrador e a senha da área a partir das variáveis de
 * ambiente (ou do .env). Nenhuma senha fica no código nem no repositório.
 */
@Component
@Order(1)
public class PrimeiroAcesso implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(PrimeiroAcesso.class);

  private final UsuariosRepository usuarios;
  private final ConfiguracaoRepository configuracoes;
  private final PasswordEncoder codificador;
  private final Clock relogio;
  private final String nome;
  private final String email;
  private final String senha;
  private final String senhaDaArea;

  public PrimeiroAcesso(
      UsuariosRepository usuarios,
      ConfiguracaoRepository configuracoes,
      PasswordEncoder codificador,
      Clock relogio,
      @Value("${app.admin.nome}") String nome,
      @Value("${app.admin.email}") String email,
      @Value("${app.admin.senha}") String senha,
      @Value("${app.senha-area}") String senhaDaArea) {
    this.usuarios = usuarios;
    this.configuracoes = configuracoes;
    this.codificador = codificador;
    this.relogio = relogio;
    this.nome = nome;
    this.email = email;
    this.senha = senha;
    this.senhaDaArea = senhaDaArea;
  }

  @Override
  @Transactional
  public void run(ApplicationArguments argumentos) {
    if (configuracoes.unica().isEmpty()) {
      exigir(senhaDaArea, "APP_SENHA_AREA");
      configuracoes.save(
          new Configuracao(codificador.encode(senhaDaArea), OffsetDateTime.now(relogio)));
      LOG.info("Senha da área da equipe configurada.");
    }
    if (usuarios.count() == 0) {
      exigir(email, "APP_ADMIN_EMAIL");
      exigir(senha, "APP_ADMIN_SENHA");
      usuarios.save(new Usuario(nome, email, codificador.encode(senha), Papel.ADMIN, false));
      LOG.info("Primeiro administrador criado.");
    }
  }

  private static void exigir(String valor, String variavel) {
    if (valor == null || valor.isBlank()) {
      throw new IllegalStateException(
          "Primeiro acesso: defina " + variavel + " no arquivo .env ou no ambiente.");
    }
    if (variavel.contains("SENHA") && valor.length() < AcessoService.TAMANHO_MINIMO_DA_SENHA) {
      throw new IllegalStateException(
          variavel
              + " precisa ter pelo menos "
              + AcessoService.TAMANHO_MINIMO_DA_SENHA
              + " caracteres.");
    }
  }
}
