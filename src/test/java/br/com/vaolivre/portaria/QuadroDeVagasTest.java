package br.com.vaolivre.portaria;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.vaolivre.portaria.QuadroDeVagas.EstadiaNoQuadro;
import br.com.vaolivre.portaria.QuadroDeVagas.Manobra;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class QuadroDeVagasTest {

  @Test
  void primeiraVagaLivreRespeitaAsOcupadas() {
    assertThat(QuadroDeVagas.primeiraVagaLivre(5, List.of(1, 2, 3, 5))).contains(4);
    assertThat(QuadroDeVagas.primeiraVagaLivre(3, List.of())).contains(1);
    assertThat(QuadroDeVagas.primeiraVagaLivre(2, List.of(1, 2))).isEmpty();
  }

  @Test
  void desenhaUmaVagaPorVagaContratadaComAsOcupadasDoBanco() {
    QuadroDeVagas quadro =
        QuadroDeVagas.montar(
            "Construtora Alfa",
            5,
            List.of(
                new EstadiaNoQuadro(1, "BRA2E19", "07:42"),
                new EstadiaNoQuadro(4, "RJA4F21", "08:12")),
            Optional.of(Manobra.entrada(4)));

    assertThat(quadro.lista()).hasSize(5);
    assertThat(quadro.emUso()).isEqualTo(2);
    assertThat(quadro.lista().get(3).ocupada()).isTrue();
    assertThat(quadro.lista().get(3).destaque()).isTrue();
    assertThat(quadro.lista().get(1).ocupada()).isFalse();
    assertThat(quadro.manobra().destinoX()).isEqualTo(quadro.lista().get(3).centroX());
    assertThat(quadro.fileiras()).hasSize(1);
  }

  @Test
  void quebraEmFileirasDeDezVagas() {
    QuadroDeVagas quadro = QuadroDeVagas.montar("Grande", 23, List.of(), Optional.empty());

    assertThat(quadro.fileiras()).hasSize(3);
    assertThat(quadro.lista().get(10).centroY()).isGreaterThan(quadro.lista().get(9).centroY());
    assertThat(quadro.manobra()).isNull();
  }
}
