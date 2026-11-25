package br.com.vaolivre.clientes;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

/** Cliente, contrato e veículos numa tela só (resposta 6.2: cadastro rápido). */
public record CadastroRapidoForm(
    @Valid ClienteForm cliente,
    @NotNull(message = "Digite quantas vagas o cliente contratou.")
        @Min(value = 1, message = "O contrato precisa de pelo menos 1 vaga.")
        @Max(value = 500, message = "Use até 500 vagas.")
        Integer vagas,
    @NotNull(message = "Digite o início do contrato.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate inicio,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
    @Valid List<VeiculoNovo> veiculos) {

  public static final int LINHAS_INICIAIS = 3;
  public static final int LINHAS_A_MAIS = 3;

  public static CadastroRapidoForm vazio(LocalDate hoje) {
    return new CadastroRapidoForm(
        ClienteForm.vazio(), 1, hoje, null, linhasVazias(LINHAS_INICIAIS));
  }

  /** Funciona sem JavaScript: o botão "Mais linhas" reenvia o formulário com linhas extras. */
  public CadastroRapidoForm comMaisLinhas() {
    List<VeiculoNovo> todas = new ArrayList<>(veiculos == null ? List.of() : veiculos);
    todas.addAll(linhasVazias(LINHAS_A_MAIS));
    return new CadastroRapidoForm(cliente, vagas, inicio, fim, todas);
  }

  public List<VeiculoNovo> veiculosPreenchidos() {
    return veiculos == null
        ? List.of()
        : veiculos.stream().filter(VeiculoNovo::preenchido).toList();
  }

  private static List<VeiculoNovo> linhasVazias(int quantidade) {
    List<VeiculoNovo> linhas = new ArrayList<>();
    for (int i = 0; i < quantidade; i++) {
      linhas.add(new VeiculoNovo("", "", ""));
    }
    return linhas;
  }
}
