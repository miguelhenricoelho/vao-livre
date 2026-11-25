package br.com.vaolivre.acesso;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

/** Linha única de configuração: a senha da área da equipe e o WhatsApp do suporte. */
@Entity
@Table(name = "configuracao")
public class Configuracao {

  public static final short ID_UNICO = 1;

  @Id private Short id;

  @Column(name = "senha_area_hash", nullable = false)
  private String senhaAreaHash;

  @Column(name = "versao_senha_area", nullable = false)
  private int versaoSenhaArea;

  @Column(name = "whatsapp_suporte")
  private String whatsappSuporte;

  @Column(name = "atualizado_em", nullable = false)
  private OffsetDateTime atualizadoEm;

  protected Configuracao() {}

  public Configuracao(String senhaAreaHash, OffsetDateTime agora) {
    this.id = ID_UNICO;
    this.senhaAreaHash = senhaAreaHash;
    this.versaoSenhaArea = 1;
    this.atualizadoEm = agora;
  }

  /** Troca a senha e sobe a versão: todas as sessões abertas precisam digitar a senha nova. */
  public void trocarSenhaDaArea(String novoHash, OffsetDateTime agora) {
    this.senhaAreaHash = novoHash;
    this.versaoSenhaArea++;
    this.atualizadoEm = agora;
  }

  public void definirWhatsappSuporte(String numero, OffsetDateTime agora) {
    this.whatsappSuporte = numero;
    this.atualizadoEm = agora;
  }

  public String getSenhaAreaHash() {
    return senhaAreaHash;
  }

  public int getVersaoSenhaArea() {
    return versaoSenhaArea;
  }

  public String getWhatsappSuporte() {
    return whatsappSuporte;
  }
}
