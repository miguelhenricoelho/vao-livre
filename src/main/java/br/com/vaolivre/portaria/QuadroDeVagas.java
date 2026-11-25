package br.com.vaolivre.portaria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A garagem do cliente desenhada: uma vaga por vaga contratada, com as ocupadas vindas do banco.
 * Aqui só se calcula a geometria do desenho (em unidades do SVG); quem decide quem entra é o {@link
 * PortariaService}.
 */
public record QuadroDeVagas(
    String titulo,
    int vagas,
    int emUso,
    int largura,
    int altura,
    List<Fileira> fileiras,
    List<VagaDesenhada> lista,
    Manobra manobra) {

  public static final int CANCELA_X = 128;
  public static final int CANCELA_Y = 76;
  static final int FAIXA_LIVRE_X = 152;
  static final int VAGA_INICIO_X = 176;
  static final int VAGA_LARGURA = 64;
  static final int POR_FILEIRA = 10;
  static final int FILEIRA_ALTURA = 200;
  static final int CORREDOR_Y = 40;

  /** Um carro dentro, na vaga numerada do quadro. */
  public record EstadiaNoQuadro(int vaga, String placa, String desde) {}

  public record Fileira(int corredorY, int faixaY, int topo, int base, List<Integer> divisorias) {}

  public record VagaDesenhada(
      int numero,
      int x,
      int centroX,
      int centroY,
      int numeroY,
      boolean ocupada,
      String placa,
      String desde,
      boolean destaque) {}

  /** O que a animação vai tocar. Só apresentação: o resultado já foi decidido e gravado. */
  public record Manobra(String tipo, int vaga, int destinoX, int destinoY, int corredorY) {

    public static Manobra entrada(int vaga) {
      return new Manobra("entrada", vaga, 0, 0, 0);
    }

    public static Manobra saida(int vaga) {
      return new Manobra("saida", vaga, 0, 0, 0);
    }

    public static Manobra bloqueio() {
      return new Manobra("bloqueio", 0, 0, 0, CORREDOR_Y);
    }

    public boolean isBloqueio() {
      return "bloqueio".equals(tipo);
    }
  }

  public static QuadroDeVagas montar(
      String titulo, int vagas, List<EstadiaNoQuadro> ocupadas, Optional<Manobra> pedida) {
    Map<Integer, EstadiaNoQuadro> porVaga =
        ocupadas.stream().collect(Collectors.toMap(EstadiaNoQuadro::vaga, Function.identity()));
    int colunas = Math.min(vagas, POR_FILEIRA);
    int numeroDeFileiras = (vagas + POR_FILEIRA - 1) / POR_FILEIRA;
    int largura = VAGA_INICIO_X + colunas * VAGA_LARGURA + 24;
    // Sem vagas (placa não cadastrada), o desenho mostra só a cancela e a espera.
    int altura = 224 + (Math.max(numeroDeFileiras, 1) - 1) * FILEIRA_ALTURA;

    List<Fileira> fileiras = new ArrayList<>();
    for (int f = 0; f < numeroDeFileiras; f++) {
      int corredor = CORREDOR_Y + f * FILEIRA_ALTURA;
      int naFileira = Math.min(POR_FILEIRA, vagas - f * POR_FILEIRA);
      List<Integer> divisorias = new ArrayList<>();
      for (int i = 0; i <= naFileira; i++) {
        divisorias.add(VAGA_INICIO_X + i * VAGA_LARGURA);
      }
      fileiras.add(new Fileira(corredor, corredor + 44, corredor + 52, corredor + 172, divisorias));
    }

    Integer vagaDaManobra = pedida.map(Manobra::vaga).orElse(0);
    boolean manobraDeEntrada = pedida.map(m -> "entrada".equals(m.tipo())).orElse(false);
    List<VagaDesenhada> lista = new ArrayList<>();
    for (int numero = 1; numero <= vagas; numero++) {
      int coluna = (numero - 1) % POR_FILEIRA;
      int corredor = CORREDOR_Y + ((numero - 1) / POR_FILEIRA) * FILEIRA_ALTURA;
      int x = VAGA_INICIO_X + coluna * VAGA_LARGURA;
      EstadiaNoQuadro estadia = porVaga.get(numero);
      lista.add(
          new VagaDesenhada(
              numero,
              x,
              x + VAGA_LARGURA / 2,
              corredor + 112,
              corredor + 166,
              estadia != null,
              estadia == null ? null : estadia.placa(),
              estadia == null ? null : estadia.desde(),
              manobraDeEntrada && numero == vagaDaManobra));
    }

    Manobra manobra = pedida.map(m -> posicionar(m, lista)).orElse(null);
    return new QuadroDeVagas(
        titulo, vagas, ocupadas.size(), largura, altura, fileiras, lista, manobra);
  }

  private static Manobra posicionar(Manobra pedida, List<VagaDesenhada> lista) {
    if (pedida.isBloqueio() || pedida.vaga() < 1 || pedida.vaga() > lista.size()) {
      return Manobra.bloqueio();
    }
    VagaDesenhada vaga = lista.get(pedida.vaga() - 1);
    return new Manobra(
        pedida.tipo(), vaga.numero(), vaga.centroX(), vaga.centroY(), vaga.centroY() - 112);
  }

  /** A primeira vaga livre do quadro, de 1 até o número de vagas contratadas. */
  public static Optional<Integer> primeiraVagaLivre(int vagas, List<Integer> ocupadas) {
    for (int numero = 1; numero <= vagas; numero++) {
      if (!ocupadas.contains(numero)) {
        return Optional.of(numero);
      }
    }
    return Optional.empty();
  }

  public boolean isCheio() {
    return emUso >= vagas;
  }

  /** Para o atributo data- do desenho (o JavaScript só lê, não calcula nada). */
  public int faixaLivreX() {
    return FAIXA_LIVRE_X;
  }

  public int cancelaX() {
    return CANCELA_X;
  }

  public int cancelaY() {
    return CANCELA_Y;
  }

  public int corredorY() {
    return CORREDOR_Y;
  }
}
