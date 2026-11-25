package br.com.vaolivre.estadias;

import br.com.vaolivre.contratos.Contrato;
import br.com.vaolivre.equipe.Usuario;
import br.com.vaolivre.veiculos.Veiculo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.OffsetDateTime;

/** Um veículo no pátio, ocupando uma vaga do contrato, da entrada até a saída. */
@Entity
@Table(name = "estadia")
public class Estadia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "veiculo_id")
  private Veiculo veiculo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "contrato_id")
  private Contrato contrato;

  @Column(name = "vaga_no_quadro", nullable = false)
  private int vagaNoQuadro;

  @Column(name = "entrada_em", nullable = false)
  private OffsetDateTime entradaEm;

  @Column(name = "saida_em")
  private OffsetDateTime saidaEm;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "registrada_por")
  private Usuario registradaPor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "encerrada_por")
  private Usuario encerradaPor;

  protected Estadia() {}

  public Estadia(
      Veiculo veiculo,
      Contrato contrato,
      int vagaNoQuadro,
      OffsetDateTime entradaEm,
      Usuario registradaPor) {
    this.veiculo = veiculo;
    this.contrato = contrato;
    this.vagaNoQuadro = vagaNoQuadro;
    this.entradaEm = entradaEm;
    this.registradaPor = registradaPor;
  }

  public void encerrar(OffsetDateTime agora, Usuario operador) {
    this.saidaEm = agora;
    this.encerradaPor = operador;
  }

  public boolean isAberta() {
    return saidaEm == null;
  }

  public Duration permanencia() {
    return Duration.between(entradaEm, saidaEm == null ? entradaEm : saidaEm);
  }

  public Long getId() {
    return id;
  }

  public Veiculo getVeiculo() {
    return veiculo;
  }

  public Contrato getContrato() {
    return contrato;
  }

  public int getVagaNoQuadro() {
    return vagaNoQuadro;
  }

  public OffsetDateTime getEntradaEm() {
    return entradaEm;
  }

  public OffsetDateTime getSaidaEm() {
    return saidaEm;
  }
}
