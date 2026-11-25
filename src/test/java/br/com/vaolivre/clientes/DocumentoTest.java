package br.com.vaolivre.clientes;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DocumentoTest {

  @Test
  void aceitaCpfECnpjValidos() {
    assertThat(Documento.valido(TipoCliente.PF, "52998224725")).isTrue();
    assertThat(Documento.valido(TipoCliente.PJ, "11222333000181")).isTrue();
  }

  @Test
  void recusaDigitoVerificadorErradoRepetidoOuTamanhoTrocado() {
    assertThat(Documento.valido(TipoCliente.PF, "52998224726")).isFalse();
    assertThat(Documento.valido(TipoCliente.PF, "11111111111")).isFalse();
    assertThat(Documento.valido(TipoCliente.PJ, "11222333000182")).isFalse();
    assertThat(Documento.valido(TipoCliente.PF, "11222333000181")).isFalse();
    assertThat(Documento.valido(TipoCliente.PJ, null)).isFalse();
  }
}
