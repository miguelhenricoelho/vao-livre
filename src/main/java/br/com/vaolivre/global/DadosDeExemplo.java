package br.com.vaolivre.global;

import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.clientes.Cliente.DadosDoCliente;
import br.com.vaolivre.clientes.ClientesRepository;
import br.com.vaolivre.clientes.TipoCliente;
import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.contratos.ContratosRepository;
import br.com.vaolivre.equipe.Papel;
import br.com.vaolivre.equipe.Usuario;
import br.com.vaolivre.equipe.UsuariosRepository;
import br.com.vaolivre.portaria.PortariaService;
import br.com.vaolivre.veiculos.Placa;
import br.com.vaolivre.veiculos.Veiculo;
import br.com.vaolivre.veiculos.VeiculosRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Só no perfil dev: clientes, veículos e um operador de exemplo, com nomes claramente fictícios.
 * Nunca roda em produção.
 */
@Component
@Profile("dev")
@Order(2)
public class DadosDeExemplo implements ApplicationRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DadosDeExemplo.class);

  private final ClientesRepository clientes;
  private final ContratosRepository contratos;
  private final VeiculosRepository veiculos;
  private final UsuariosRepository usuarios;
  private final PortariaService portaria;
  private final PasswordEncoder codificador;
  private final Clock relogio;
  private final String senhaDoOperador;

  public DadosDeExemplo(
      ClientesRepository clientes,
      ContratosRepository contratos,
      VeiculosRepository veiculos,
      UsuariosRepository usuarios,
      PortariaService portaria,
      PasswordEncoder codificador,
      Clock relogio,
      @Value("${app.exemplo.operador-senha:}") String senhaDoOperador) {
    this.clientes = clientes;
    this.contratos = contratos;
    this.veiculos = veiculos;
    this.usuarios = usuarios;
    this.portaria = portaria;
    this.codificador = codificador;
    this.relogio = relogio;
    this.senhaDoOperador = senhaDoOperador;
  }

  @Override
  public void run(ApplicationArguments argumentos) {
    if (clientes.count() > 0) {
      return;
    }
    OffsetDateTime agora = OffsetDateTime.now(relogio);
    LocalDate hoje = LocalDate.now(relogio);

    Cliente alfa =
        cliente(
            TipoCliente.PJ, "Construtora Alfa (exemplo)", "11222333000181", "Marina Toledo", agora);
    Cliente beta =
        cliente(TipoCliente.PJ, "Clínica Beta (exemplo)", "11444777000161", "Rafael Moura", agora);
    Cliente helena = cliente(TipoCliente.PF, "Helena Duarte (exemplo)", "52998224725", null, agora);

    contratos.save(new Contrato(alfa, 5, hoje.minusMonths(9), hoje.plusMonths(15), agora));
    contratos.save(new Contrato(beta, 3, hoje.minusMonths(2), hoje.plusDays(20), agora));
    contratos.save(new Contrato(helena, 2, hoje.minusYears(1), null, agora));

    carros(alfa, agora, "BRA2E19", "QTP4A21", "RIO8C33", "MGX7D45", "RJA4F21", "SPA1B02");
    carros(beta, agora, "BET1A23", "BET2B34", "BET3C45");
    carros(helena, agora, "FTR3B56", "HEL5D67");

    Usuario operador = operador();
    for (String placa : List.of("BRA2E19", "QTP4A21", "RIO8C33", "MGX7D45", "BET1A23", "FTR3B56")) {
      portaria.registrarEntrada(placa, operador.getId());
    }
    LOG.info("Dados de exemplo criados (perfil dev).");
  }

  private Cliente cliente(
      TipoCliente tipo, String nome, String documento, String responsavel, OffsetDateTime agora) {
    return clientes.save(
        new Cliente(
            new DadosDoCliente(
                tipo, nome, documento, responsavel, "5511900000001", "contato@exemplo.invalid"),
            agora));
  }

  private void carros(Cliente cliente, OffsetDateTime agora, String... placas) {
    for (String placa : placas) {
      veiculos.save(new Veiculo(cliente, Placa.de(placa), "Carro de exemplo", null, agora));
    }
  }

  private Usuario operador() {
    if (senhaDoOperador == null || senhaDoOperador.length() < 10) {
      throw new IllegalStateException(
          "Perfil dev: defina APP_OPERADOR_EXEMPLO_SENHA (10 caracteres ou mais) no arquivo .env.");
    }
    return usuarios
        .buscarPorEmail("operador@vaolivre.exemplo")
        .orElseGet(() ->
            usuarios.save(
                new Usuario(
                    "Ana Ribeiro (exemplo)",
                    "operador@vaolivre.exemplo",
                    codificador.encode(senhaDoOperador),
                    Papel.OPERADOR,
                    false)));
  }
}
