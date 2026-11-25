package br.com.vaolivre.veiculos;

import br.com.vaolivre.clientes.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "veiculo")
public class Veiculo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  @Column(nullable = false)
  private String placa;

  private String modelo;
  private String cor;

  @Column(nullable = false)
  private boolean ativo;

  @Column(name = "criado_em", nullable = false)
  private OffsetDateTime criadoEm;

  protected Veiculo() {}

  public Veiculo(Cliente cliente, Placa placa, String modelo, String cor, OffsetDateTime agora) {
    this.cliente = cliente;
    this.placa = placa.valor();
    this.modelo = modelo;
    this.cor = cor;
    this.ativo = true;
    this.criadoEm = agora;
  }

  public void desativar() {
    this.ativo = false;
  }

  public Long getId() {
    return id;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public String getPlaca() {
    return placa;
  }

  public String getModelo() {
    return modelo;
  }

  public String getCor() {
    return cor;
  }

  public boolean isAtivo() {
    return ativo;
  }
}
