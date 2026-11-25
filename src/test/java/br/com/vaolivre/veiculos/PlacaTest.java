package br.com.vaolivre.veiculos;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.vaolivre.global.RegraDeNegocioException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class PlacaTest {

  @ParameterizedTest
  @CsvSource({"abc1234,ABC1234", "ABC-1234,ABC1234", " rja 4f21 ,RJA4F21", "BRA2E19,BRA2E19"})
  void normalizaFormatoAntigoEMercosul(String digitada, String esperada) {
    assertThat(Placa.de(digitada).valor()).isEqualTo(esperada);
  }

  @ParameterizedTest
  @ValueSource(strings = {"", "AB12345", "ABCD123", "1BC1234", "ABC12345", "ABC1D2"})
  void recusaPlacaForaDoFormato(String digitada) {
    assertThatThrownBy(() -> Placa.de(digitada)).isInstanceOf(RegraDeNegocioException.class);
    assertThat(Placa.valida(digitada)).isFalse();
  }
}
