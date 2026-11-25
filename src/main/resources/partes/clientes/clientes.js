// Máscaras de campo (só aparência). A validação de verdade é feita no Java.
const MASCARAS = {
  documento(digitos) {
    const d = digitos.slice(0, 14);
    if (d.length <= 11) {
      return d
        .replace(/(\d{3})(\d)/, '$1.$2')
        .replace(/(\d{3})\.(\d{3})(\d)/, '$1.$2.$3')
        .replace(/(\d{3})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3-$4');
    }
    return d
      .replace(/(\d{2})(\d)/, '$1.$2')
      .replace(/(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(/(\d{2})\.(\d{3})\.(\d{3})(\d)/, '$1.$2.$3/$4')
      .replace(/(\d{4})(\d)/, '$1-$2');
  },
  telefone(digitos) {
    const d = digitos.slice(0, 11);
    if (d.length <= 2) {
      return d;
    }
    const corte = d.length > 10 ? 7 : 6;
    const meio = d.slice(2, corte);
    const fim = d.slice(corte);
    return `(${d.slice(0, 2)}) ${meio}${fim ? '-' + fim : ''}`;
  },
  placa(texto) {
    return texto.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 7);
  },
};

document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-mascara]').forEach((campo) => {
    const mascara = MASCARAS[campo.dataset.mascara];
    if (!mascara) {
      return;
    }
    campo.addEventListener('input', () => {
      const bruto = campo.dataset.mascara === 'placa' ? campo.value : campo.value.replace(/\D/g, '');
      const formatado = mascara(bruto);
      if (formatado !== campo.value) {
        campo.value = formatado;
      }
    });
  });
});
