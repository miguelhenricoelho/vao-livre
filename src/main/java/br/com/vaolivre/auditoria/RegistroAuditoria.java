package br.com.vaolivre.auditoria;

import br.com.vaolivre.equipe.Usuario;
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

/** Ação administrativa registrada. Só é criada, nunca alterada. */
@Entity
@Table(name = "auditoria")
public class RegistroAuditoria {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_id")
  private Usuario usuario;

  @Column(nullable = false)
  private String acao;

  @Column(nullable = false)
  private String entidade;

  @Column(name = "entidade_id")
  private Long entidadeId;

  @Column(nullable = false)
  private String resumo;

  @Column(name = "ocorrido_em", nullable = false)
  private OffsetDateTime ocorridoEm;

  protected RegistroAuditoria() {}

  public RegistroAuditoria(
      Usuario usuario,
      String acao,
      String entidade,
      Long entidadeId,
      String resumo,
      OffsetDateTime agora) {
    this.usuario = usuario;
    this.acao = acao;
    this.entidade = entidade;
    this.entidadeId = entidadeId;
    this.resumo = resumo;
    this.ocorridoEm = agora;
  }

  public Long getId() {
    return id;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public String getAcao() {
    return acao;
  }

  public String getEntidade() {
    return entidade;
  }

  public Long getEntidadeId() {
    return entidadeId;
  }

  public String getResumo() {
    return resumo;
  }

  public OffsetDateTime getOcorridoEm() {
    return ocorridoEm;
  }
}
