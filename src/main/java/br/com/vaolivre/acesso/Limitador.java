package br.com.vaolivre.acesso;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Limite de tentativas por chave (normalmente o IP), com Bucket4j. Guardado na memória: ao
 * reiniciar o servidor, a contagem recomeça.
 */
public final class Limitador {

  private static final int MAXIMO_DE_CHAVES = 10_000;

  private final int tentativas;
  private final Duration janela;
  private final Map<String, Bucket> baldes = new ConcurrentHashMap<>();

  public Limitador(int tentativas, Duration janela) {
    this.tentativas = tentativas;
    this.janela = janela;
  }

  /** Gasta uma tentativa. Devolve false quando a chave já esgotou as tentativas da janela. */
  public boolean tentar(String chave) {
    if (baldes.size() > MAXIMO_DE_CHAVES) {
      baldes.clear();
    }
    return baldes.computeIfAbsent(chave, k -> novoBalde()).tryConsume(1);
  }

  public void liberar(String chave) {
    baldes.remove(chave);
  }

  public int getTentativas() {
    return tentativas;
  }

  public long getMinutos() {
    return janela.toMinutes();
  }

  private Bucket novoBalde() {
    return Bucket.builder()
        .addLimit(
            Bandwidth.builder().capacity(tentativas).refillIntervally(tentativas, janela).build())
        .build();
  }
}
