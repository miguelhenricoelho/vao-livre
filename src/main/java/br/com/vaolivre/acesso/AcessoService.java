package br.com.vaolivre.acesso;

import br.com.vaolivre.auditoria.AuditoriaService;
import br.com.vaolivre.global.Formatos;
import br.com.vaolivre.global.RegraDeNegocioException;
import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Primeira camada da área da equipe: a senha da área, conferida no servidor e guardada só como
 * hash. Tentativas erradas seguidas bloqueiam o IP por alguns minutos.
 */
@Service
public class AcessoService {

  public static final String ATRIBUTO_DA_SESSAO = "vaolivre.senhaDaArea";
  public static final int TAMANHO_MINIMO_DA_SENHA = 10;

  private final ConfiguracaoRepository configuracoes;
  private final PasswordEncoder codificador;
  private final AuditoriaService auditoria;
  private final Clock relogio;
  private final Limitador limiteDaArea = new Limitador(5, Duration.ofMinutes(15));

  public AcessoService(
      ConfiguracaoRepository configuracoes,
      PasswordEncoder codificador,
      AuditoriaService auditoria,
      Clock relogio) {
    this.configuracoes = configuracoes;
    this.codificador = codificador;
    this.auditoria = auditoria;
    this.relogio = relogio;
  }

  public enum ResultadoDaArea {
    LIBERADA,
    SENHA_ERRADA,
    BLOQUEADA
  }

  /** Resultado da conferência. Quando liberada, a versão vai para a sessão. */
  public record Conferencia(ResultadoDaArea resultado, int versao) {}

  @Transactional(readOnly = true)
  public Conferencia conferirSenhaDaArea(String senha, String ip) {
    if (!limiteDaArea.tentar(ip)) {
      return new Conferencia(ResultadoDaArea.BLOQUEADA, 0);
    }
    Configuracao configuracao = configuracao();
    if (senha == null || !codificador.matches(senha, configuracao.getSenhaAreaHash())) {
      return new Conferencia(ResultadoDaArea.SENHA_ERRADA, 0);
    }
    limiteDaArea.liberar(ip);
    return new Conferencia(ResultadoDaArea.LIBERADA, configuracao.getVersaoSenhaArea());
  }

  @Transactional(readOnly = true)
  public int versaoAtual() {
    return configuracoes.versaoDaSenhaDaArea().orElse(0);
  }

  @Transactional(readOnly = true)
  public String whatsappDoSuporte() {
    return configuracao().getWhatsappSuporte();
  }

  /** Troca a senha da área: todo mundo precisa digitá-la de novo. Ação grave, auditada. */
  @Transactional
  public void trocarSenhaDaArea(String nova, long administradorId) {
    if (nova == null || nova.length() < TAMANHO_MINIMO_DA_SENHA) {
      throw new RegraDeNegocioException(
          "A senha da área precisa ter pelo menos " + TAMANHO_MINIMO_DA_SENHA + " caracteres.");
    }
    configuracao().trocarSenhaDaArea(codificador.encode(nova), OffsetDateTime.now(relogio));
    auditoria.registrar(
        administradorId,
        "Trocou a senha da área",
        "configuracao",
        null,
        "Todas as sessões precisam digitar a senha nova.");
  }

  @Transactional
  public void definirWhatsappDoSuporte(String digitado, long administradorId) {
    String numero = Formatos.telefoneParaGuardar(digitado);
    if (numero != null && (numero.length() < 12 || numero.length() > 13)) {
      throw new RegraDeNegocioException(
          "Confira o WhatsApp do suporte: DDD e número, como (11) 90000-0000.");
    }
    configuracao().definirWhatsappSuporte(numero, OffsetDateTime.now(relogio));
    auditoria.registrar(
        administradorId,
        "Alterou o WhatsApp do suporte",
        "configuracao",
        null,
        "Número atualizado.");
  }

  public int tentativasDaArea() {
    return limiteDaArea.getTentativas();
  }

  public long minutosDeBloqueio() {
    return limiteDaArea.getMinutos();
  }

  private Configuracao configuracao() {
    return configuracoes
        .unica()
        .orElseThrow(() -> new IllegalStateException("Configuração inicial ausente"));
  }
}
