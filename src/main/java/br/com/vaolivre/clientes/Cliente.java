package br.com.vaolivre.clientes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/** Empresa ou pessoa física com vagas contratadas. */
@Entity
@Table(name = "cliente")
public class Cliente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoCliente tipo;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false)
  private String documento;

  private String responsavel;
  private String telefone;
  private String email;

  @Column(nullable = false)
  private boolean ativo;

  @Column(name = "criado_em", nullable = false)
  private OffsetDateTime criadoEm;

  @Column(name = "atualizado_em", nullable = false)
  private OffsetDateTime atualizadoEm;

  protected Cliente() {}

  public Cliente(DadosDoCliente dados, OffsetDateTime agora) {
    this.ativo = true;
    this.criadoEm = agora;
    atualizar(dados, agora);
  }

  /** Só os campos do formulário mudam; ativo e datas de criação ficam protegidos. */
  public final void atualizar(DadosDoCliente dados, OffsetDateTime agora) {
    this.tipo = dados.tipo();
    this.nome = dados.nome();
    this.documento = dados.documento();
    this.responsavel = dados.responsavel();
    this.telefone = dados.telefone();
    this.email = dados.email();
    this.atualizadoEm = agora;
  }

  public void desativar(OffsetDateTime agora) {
    this.ativo = false;
    this.atualizadoEm = agora;
  }

  public void reativar(OffsetDateTime agora) {
    this.ativo = true;
    this.atualizadoEm = agora;
  }

  public Long getId() {
    return id;
  }

  public TipoCliente getTipo() {
    return tipo;
  }

  public String getNome() {
    return nome;
  }

  public String getDocumento() {
    return documento;
  }

  public String getResponsavel() {
    return responsavel;
  }

  public String getTelefone() {
    return telefone;
  }

  public String getEmail() {
    return email;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public OffsetDateTime getCriadoEm() {
    return criadoEm;
  }

  /** Dados já normalizados (só dígitos no documento e no telefone). */
  public record DadosDoCliente(
      TipoCliente tipo,
      String nome,
      String documento,
      String responsavel,
      String telefone,
      String email) {}
}
