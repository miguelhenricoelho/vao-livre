package br.com.vaolivre.ocorrencias;

import br.com.vaolivre.clientes.Cliente;
import br.com.vaolivre.equipe.Usuario;
import br.com.vaolivre.estadias.Estadia;
import br.com.vaolivre.veiculos.Placa;
import br.com.vaolivre.veiculos.Veiculo;
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
import java.time.OffsetDateTime;

/** Uma tentativa na portaria, liberada ou bloqueada. Só é criada, nunca alterada. */
@Entity
@Table(name = "ocorrencia_portaria")
public class Ocorrencia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "placa_informada", nullable = false)
  private String placaInformada;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "veiculo_id")
  private Veiculo veiculo;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "cliente_id")
  private Cliente cliente;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "estadia_id")
  private Estadia estadia;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoOcorrencia tipo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Resultado resultado;

  @Enumerated(EnumType.STRING)
  private Motivo motivo;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "operador_id")
  private Usuario operador;

  @Column(name = "ocorrido_em", nullable = false)
  private OffsetDateTime ocorridoEm;

  protected Ocorrencia() {}

  private Ocorrencia(
      TipoOcorrencia tipo,
      Resultado resultado,
      Motivo motivo,
      Placa placa,
      Veiculo veiculo,
      Cliente cliente,
      Estadia estadia,
      Usuario operador,
      OffsetDateTime agora) {
    this.tipo = tipo;
    this.resultado = resultado;
    this.motivo = motivo;
    this.placaInformada = placa.valor();
    this.veiculo = veiculo;
    this.cliente = cliente;
    this.estadia = estadia;
    this.operador = operador;
    this.ocorridoEm = agora;
  }

  public static Ocorrencia liberada(
      TipoOcorrencia tipo, Estadia estadia, Usuario operador, OffsetDateTime agora) {
    Veiculo veiculo = estadia.getVeiculo();
    return new Ocorrencia(
        tipo,
        Resultado.LIBERADO,
        null,
        new Placa(veiculo.getPlaca()),
        veiculo,
        veiculo.getCliente(),
        estadia,
        operador,
        agora);
  }

  public static Ocorrencia bloqueada(
      TipoOcorrencia tipo,
      Motivo motivo,
      Placa placa,
      Veiculo veiculo,
      Usuario operador,
      OffsetDateTime agora) {
    Cliente cliente = veiculo == null ? null : veiculo.getCliente();
    return new Ocorrencia(
        tipo, Resultado.BLOQUEADO, motivo, placa, veiculo, cliente, null, operador, agora);
  }

  public Long getId() {
    return id;
  }

  public String getPlacaInformada() {
    return placaInformada;
  }

  public Veiculo getVeiculo() {
    return veiculo;
  }

  public Cliente getCliente() {
    return cliente;
  }

  public Estadia getEstadia() {
    return estadia;
  }

  public TipoOcorrencia getTipo() {
    return tipo;
  }

  public Resultado getResultado() {
    return resultado;
  }

  public Motivo getMotivo() {
    return motivo;
  }

  public Usuario getOperador() {
    return operador;
  }

  public OffsetDateTime getOcorridoEm() {
    return ocorridoEm;
  }
}
