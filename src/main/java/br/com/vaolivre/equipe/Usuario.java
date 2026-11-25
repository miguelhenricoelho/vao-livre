package br.com.vaolivre.equipe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Pessoa da equipe do estacionamento. Nunca é um cliente. */
@Entity
@Table(name = "usuario")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nome;

  @Column(nullable = false)
  private String email;

  @Column(name = "senha_hash", nullable = false)
  private String senhaHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Papel papel;

  @Column(nullable = false)
  private boolean ativo;

  @Column(name = "deve_trocar_senha", nullable = false)
  private boolean deveTrocarSenha;

  protected Usuario() {}

  public Usuario(
      String nome, String email, String senhaHash, Papel papel, boolean deveTrocarSenha) {
    this.nome = nome;
    this.email = email.trim().toLowerCase();
    this.senhaHash = senhaHash;
    this.papel = papel;
    this.ativo = true;
    this.deveTrocarSenha = deveTrocarSenha;
  }

  public void trocarSenha(String novoHash, boolean temporaria) {
    this.senhaHash = novoHash;
    this.deveTrocarSenha = temporaria;
  }

  public void mudarPapel(Papel novoPapel) {
    this.papel = novoPapel;
  }

  public void desativar() {
    this.ativo = false;
  }

  public void reativar() {
    this.ativo = true;
  }

  public Long getId() {
    return id;
  }

  public String getNome() {
    return nome;
  }

  public String getEmail() {
    return email;
  }

  public String getSenhaHash() {
    return senhaHash;
  }

  public Papel getPapel() {
    return papel;
  }

  public boolean isAtivo() {
    return ativo;
  }

  public boolean isDeveTrocarSenha() {
    return deveTrocarSenha;
  }
}
