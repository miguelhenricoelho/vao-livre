package br.com.vaolivre.contratos;

import br.com.vaolivre.clientes.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/** Quantas vagas o cliente contratou e por quanto tempo. */
@Entity
@Table(name = "contrato")
public class Contrato {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  @Column(name = "vagas_contratadas", nullable = false)
  private int vagasContratadas;

  @Column(nullable = false)
  private LocalDate inicio;

  private LocalDate fim;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SituacaoContrato situacao;

  @Column(name = "criado_em", nullable = false)
  private OffsetDateTime criadoEm;

  protected Contrato() {}

  public Contrato(
      Cliente cliente,
      int vagasContratadas,
      LocalDate inicio,
      LocalDate fim,
      OffsetDateTime agora) {
    this.cliente = cliente;
    this.vagasContratadas = vagasContratadas;
    this.inicio = inicio;
    this.fim = fim;
    this.situacao = SituacaoContrato.ATIVO;
    this.criadoEm = agora;
  }

  public void alterar(int vagas, LocalDate novoInicio, LocalDate novoFim, SituacaoContrato nova) {
    this.vagasContratadas = vagas;
    this.inicio = novoInicio;
    this.fim = novoFim;
    this.situacao = nova;
  }

  public boolean vencidoEm(LocalDate hoje) {
    return fim != null && fim.isBefore(hoje);
  }

  public boolean aindaNaoComecouEm(LocalDate hoje) {
    return inicio.isAfter(hoje);
  }

  public Long getId() {
    return id;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public int getVagasContratadas() {
    return vagasContratadas;
  }

  public LocalDate getInicio() {
    return inicio;
  }

  public LocalDate getFim() {
    return fim;
  }

  public SituacaoContrato getSituacao() {
    return situacao;
  }
}
