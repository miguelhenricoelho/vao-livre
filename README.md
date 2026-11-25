# Vão Livre — controle de portaria de estacionamento

Sistema para estacionamentos com vagas contratadas por empresas e pessoas físicas. O operador digita a placa e o sistema libera ou bloqueia a entrada na hora. Se o cliente tem 5 vagas e as 5 estão em uso, o 6º carro espera até um sair. O limite é garantido pelo banco de dados, não só pela tela.

**Stack:** Java 25 · Spring Boot 4.2 · Thymeleaf · Spring Security · Spring Data JPA · PostgreSQL 18 · Flyway · Maven.

## O que precisa estar instalado

1. **Java 25 (Temurin):** https://adoptium.net/temurin/releases/?version=25. No Windows, instale o `.msi` com "Set JAVA_HOME" marcado.
2. **Docker Desktop:** https://www.docker.com/products/docker-desktop/. No Windows, ele precisa do WSL. Se o Docker não ligar, rode `wsl --install --no-distribution` num terminal de administrador e reinicie o computador.

Não precisa instalar o Maven: o projeto traz o `mvnw`, que baixa a versão certa sozinho.

## Como rodar no seu computador

1. Abra o **Docker Desktop** e espere aparecer "Engine running".
2. Na pasta do projeto, crie o arquivo `.env` a partir do `.env.example` e preencha as senhas (todas com pelo menos 10 caracteres).
3. Rode:

   ```powershell
   ./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"
   ```

   Na primeira vez demora alguns minutos, porque ele baixa as dependências e o Postgres.
4. Abra **http://localhost:5173**.
   - **Senha da área:** o valor de `APP_SENHA_AREA` no `.env`.
   - **Login do administrador:** `APP_ADMIN_EMAIL` e `APP_ADMIN_SENHA`.
   - **Operador de exemplo** (só no perfil dev): `operador@vaolivre.exemplo` com a senha `APP_OPERADOR_EXEMPLO_SENHA`.
5. **Pelo celular, na mesma rede Wi-Fi:** abra `http://<IP do computador>:5173`. O IP aparece com `ipconfig`, em "Endereço IPv4".

No perfil dev, mudou HTML ou CSS? É só recarregar a página.

## Comandos

| O que | Comando |
| --- | --- |
| Rodar em desenvolvimento (5173) | `./mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"` |
| Testes | `./mvnw test` |
| Formatar o código | `./mvnw spotless:apply` |
| Verificação completa (lint, formatação, testes e auditoria de dependências) | `./mvnw clean verify` |
| Rodar o jar de produção localmente (4173) | `java -jar target/vao-livre.jar --spring.profiles.active=prod --server.port=4173` |

- **Para o jar local (4173):** suba o banco com `docker compose up -d` e defina `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/vaolivre`, `SPRING_DATASOURCE_USERNAME` e `SPRING_DATASOURCE_PASSWORD`.
- **Dependency-Check:** a primeira execução baixa a base de vulnerabilidades da NVD e pode levar muito tempo. Uma chave gratuita em `NVD_API_KEY` acelera.
- **Testes com banco** (concorrência e permissões): usam Testcontainers e precisam do Docker ligado. Sem ele, aparecem como pulados.

## Variáveis de ambiente (só os nomes)

`POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, `APP_ADMIN_NOME`, `APP_ADMIN_EMAIL`, `APP_ADMIN_SENHA`, `APP_SENHA_AREA`, `APP_OPERADOR_EXEMPLO_SENHA` (só no dev), `MODO_REVISAO`, `NVD_API_KEY`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.

Nenhuma senha fica no código nem no repositório. O `.env` está no `.gitignore`.

## Como o acesso funciona

- **Duas senhas:** primeiro a senha da área, a mesma para toda a equipe; depois o login de cada pessoa. Depois de 5 tentativas erradas, a senha da área fica bloqueada por 15 minutos naquele endereço.
- **Trocar a senha da área:** o administrador troca em **Equipe → Senha da área**, por exemplo quando alguém sai da equipe. Ao trocar, todas as sessões abertas precisam digitar a senha nova.
- **Papéis:** o administrador vê e altera tudo. O operador registra entradas e saídas e vê o histórico.
- **Ações graves pedem a senha de novo:** alterar vagas, desativar cliente, mudar papel, desativar pessoa e trocar a senha da área. Todas ficam na auditoria.
- **Senha esquecida:** o administrador define uma senha temporária em **Equipe**, e a pessoa troca no primeiro acesso.

## Estrutura

Cada parte tem a mesma pasta em `src/main/java/br/com/vaolivre/<parte>/` (código) e em `src/main/resources/partes/<parte>/` (tela, CSS, JS e textos). As tabelas são criadas só pelas migrations em `src/main/resources/db/migration/`.

| Parte | O que faz |
| --- | --- |
| `acesso` | Senha da área, login e limite de tentativas |
| `portaria` | Entrada e saída, a regra de vagas e a garagem animada |
| `painel` | Indicadores do dia e ocupação por cliente |
| `clientes`, `contratos`, `veiculos` | Cadastros |
| `estadias`, `ocorrencias` | Quem está no pátio e cada tentativa (o histórico) |
| `historico`, `auditoria` | Consultas |
| `equipe`, `conta` | Pessoas, papéis e senhas |
| `whatsapp` | Mensagens prontas montadas no Java |
| `ajuda`, `privacidade`, `error` | Páginas de apoio e de erro |

## Publicar (Render)

O `Dockerfile` está pronto para um serviço web com Docker no Render, junto com um Render Postgres. Os valores do `.env` viram variáveis de ambiente do serviço. A publicação ainda não foi feita.
