package br.com.vaolivre.global;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base dos testes de integração: um PostgreSQL de verdade (Testcontainers), com as migrations do
 * Flyway e o {@code ddl-auto=validate}. O mesmo banco serve todas as classes de teste (o contexto
 * do Spring é reaproveitado entre elas). Sem Docker, esses testes são pulados, nunca aprovados.
 */
@SpringBootTest(
    properties = {
      "app.admin.nome=Administrador de teste",
      "app.admin.email=admin@teste.invalid",
      "app.admin.senha=senha-de-teste-admin",
      "app.senha-area=senha-de-teste-area",
      "spring.config.import="
    })
@Testcontainers(disabledWithoutDocker = true)
public abstract class BancoDeTeste {

  @ServiceConnection
  static final PostgreSQLContainer BANCO = new PostgreSQLContainer("postgres:18-alpine");

  /** Liga uma vez só; o Testcontainers desliga o contêiner quando os testes terminam. */
  @BeforeAll
  static void ligarBanco() {
    BANCO.start();
  }
}
