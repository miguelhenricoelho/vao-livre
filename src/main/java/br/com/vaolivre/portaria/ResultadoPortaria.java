package br.com.vaolivre.portaria;

import br.com.vaolivre.ocorrencias.Motivo;
import br.com.vaolivre.ocorrencias.Resultado;
import br.com.vaolivre.ocorrencias.TipoOcorrencia;
import java.util.List;

/** O que a tela da portaria mostra depois de um registro. Já vem pronto do servidor. */
public record ResultadoPortaria(
    TipoOcorrencia tipo,
    Resultado resultado,
    Motivo motivo,
    String placa,
    String hora,
    String data,
    String cliente,
    Integer vaga,
    int emUso,
    int vagas,
    String permanencia,
    String dataDoContrato,
    List<PlacaNoPatio> ocupando,
    String whatsappAviso,
    QuadroDeVagas quadro) {

  public record PlacaNoPatio(String placa, String desde) {}

  public boolean isLiberado() {
    return resultado == Resultado.LIBERADO;
  }

  public boolean isEntrada() {
    return tipo == TipoOcorrencia.ENTRADA;
  }

  /** Chave do texto do resultado em partes/portaria/conteudo.properties. */
  public String chaveDaFrase() {
    if (isLiberado()) {
      return isEntrada() ? "portaria.frase.entrada" : "portaria.frase.saida";
    }
    return "portaria.motivo." + motivo.name();
  }
}
