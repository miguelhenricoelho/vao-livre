package br.com.vaolivre.clientes;

import java.util.List;

/** O que a ficha do cliente mostra. Nunca a entidade inteira. */
public record FichaDoCliente(
    long id,
    String nome,
    String tipo,
    String documento,
    String responsavel,
    String telefone,
    String email,
    boolean ativo,
    String clienteDesde,
    DadosDoContrato contrato,
    List<LinhaVeiculo> veiculos) {

  public record DadosDoContrato(long id, int vagas, String situacao, String inicio, String fim) {}

  public record LinhaVeiculo(
      long id, String placa, String descricao, boolean ativo, boolean noPatio) {}

  public long veiculosAtivos() {
    return veiculos.stream().filter(LinhaVeiculo::ativo).count();
  }
}
