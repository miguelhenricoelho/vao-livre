// Portaria: só comportamento visual. A máscara da placa e a manobra da garagem.
// Quem decide liberar ou bloquear é o Java; este arquivo só toca o resultado que já veio
// pronto na página (texto e desenho no estado final). Sem JavaScript, nada se perde.

const CHEGADA = 'cubic-bezier(0.22, 0.8, 0.24, 1)';
const MANOBRA = 'cubic-bezier(0.55, 0, 0.35, 1)';
const CANCELA = 'cubic-bezier(0.3, 0, 0.2, 1)';
const ABERTA = 80;
const CORREDOR = 40;
const FORA = -60;
const NA_CANCELA = 84;
const RE_ATE = 48;

const em = (x, y, graus) => `translate(${x}px, ${y}px) rotate(${graus}deg)`;

function prepararCampo() {
  const campo = document.querySelector('[data-placa]');
  if (!campo) {
    return;
  }
  campo.addEventListener('input', () => {
    const limpo = campo.value.toUpperCase().replace(/[^A-Z0-9]/g, '').slice(0, 7);
    if (campo.value !== limpo) {
      campo.value = limpo;
    }
  });
  if (window.matchMedia('(hover: hover) and (pointer: fine)').matches) {
    campo.focus({ preventScroll: true });
  }
}

function podeAnimar() {
  return (
    !window.matchMedia('(prefers-reduced-motion: reduce)').matches &&
    'animate' in Element.prototype
  );
}

function cancela(planta, abre, fecha, total) {
  planta.querySelectorAll('[data-cancela], [data-cancela-faixas]').forEach((braco) => {
    braco.animate(
      [
        { transform: 'rotate(0deg)', offset: 0 },
        { transform: 'rotate(0deg)', offset: abre[0] / total, easing: CANCELA },
        { transform: `rotate(${ABERTA}deg)`, offset: abre[1] / total },
        { transform: `rotate(${ABERTA}deg)`, offset: fecha[0] / total, easing: CANCELA },
        { transform: 'rotate(0deg)', offset: 1 },
      ],
      { duration: total },
    );
  });
}

function surgir(elemento, inicio, fim, total, comoTraco = false) {
  if (!elemento) {
    return;
  }
  const de = comoTraco ? { transform: 'scaleX(0)' } : { opacity: 0 };
  const para = comoTraco ? { transform: 'scaleX(1)' } : { opacity: 1 };
  elemento.animate(
    [
      { ...de, offset: 0 },
      { ...de, offset: inicio / total, easing: CANCELA },
      { ...para, offset: fim / total },
      { ...para, offset: 1 },
    ],
    { duration: total },
  );
}

// Caminho do carro pelo corredor até a vaga. Fileiras abaixo da primeira descem pela faixa livre.
function caminhoDeEntrada(d) {
  const quadros = [
    { transform: em(FORA, CORREDOR, 0), offset: 0, easing: CHEGADA },
    { transform: em(NA_CANCELA - 4, CORREDOR, 0), offset: 0.3 },
    { transform: em(NA_CANCELA - 4, CORREDOR, 0), offset: 0.46, easing: MANOBRA },
  ];
  if (d.corredorY > CORREDOR) {
    quadros.push({ transform: em(d.faixaX, CORREDOR + 20, 90), offset: 0.56 });
    quadros.push({ transform: em(d.faixaX, d.corredorY - 20, 90), offset: 0.68 });
    quadros.push({ transform: em(d.faixaX + 36, d.corredorY, 0), offset: 0.76 });
  }
  quadros.push({ transform: em(d.x - 40, d.corredorY, 0), offset: 0.84 });
  quadros.push({ transform: em(d.x - 6, d.corredorY + 36, 60), offset: 0.92 });
  quadros.push({ transform: em(d.x, d.y, 90), offset: 1 });
  return quadros;
}

function caminhoDeSaida(d) {
  const quadros = [
    { transform: em(d.x, d.y, 90), opacity: 1, offset: 0, easing: MANOBRA },
    { transform: em(d.x + 8, d.corredorY + 60, 120), opacity: 1, offset: 0.2 },
    { transform: em(d.x + 30, d.corredorY, 180), opacity: 1, offset: 0.4 },
  ];
  if (d.corredorY > CORREDOR) {
    quadros.push({ transform: em(d.faixaX + 36, d.corredorY, 180), opacity: 1, offset: 0.5 });
    quadros.push({ transform: em(d.faixaX, CORREDOR + 20, 270), opacity: 1, offset: 0.56 });
  }
  quadros.push({ transform: em(172, CORREDOR, 180), opacity: 1, offset: 0.58, easing: CHEGADA });
  quadros.push({ transform: em(FORA, CORREDOR, 180), opacity: 1, offset: 1 });
  return quadros;
}

const MANOBRAS = {
  entrada(planta, carro, d) {
    const total = 1800;
    carro.animate(caminhoDeEntrada(d), { duration: 1450, fill: 'backwards' });
    cancela(planta, [300, 650], [1450, 1800], total);
    surgir(planta.querySelector('[data-destaque]'), 1450, 1800, total);
    return total;
  },

  bloqueio(planta, carro) {
    const total = 1450;
    carro.animate(
      [
        { transform: em(FORA, CORREDOR, 0), offset: 0, easing: CHEGADA },
        { transform: em(NA_CANCELA, CORREDOR, 0), offset: 500 / 1150 },
        { transform: em(NA_CANCELA, CORREDOR, 0), offset: 650 / 1150, easing: MANOBRA },
        { transform: em(RE_ATE, CORREDOR, 0), offset: 1 },
      ],
      { duration: 1150, fill: 'backwards' },
    );
    surgir(planta.querySelector('[data-destaque]'), 1150, 1450, total);
    surgir(planta.querySelector('[data-risco]'), 1150, 1450, total, true);
    return total;
  },

  saida(planta, carro, d) {
    const total = 1600;
    carro.animate(caminhoDeSaida(d), { duration: 1300 });
    cancela(planta, [450, 750], [1300, 1600], total);
    return total;
  },
};

function tocar(planta) {
  const carro = planta.querySelector('[data-carro-manobra]');
  const manobra = MANOBRAS[planta.dataset.manobra];
  if (!carro || !manobra) {
    return;
  }
  const dados = {
    x: Number(planta.dataset.destinoX),
    y: Number(planta.dataset.destinoY),
    corredorY: Number(planta.dataset.corredorY || CORREDOR),
    faixaX: Number(planta.dataset.faixaX),
  };
  const total = manobra(planta, carro, dados);
  const traco = document.querySelector('.garagem__contador--mudou .garagem__contador-texto');
  const aceitaPseudo =
    typeof KeyframeEffect !== 'undefined' && 'pseudoElement' in KeyframeEffect.prototype;
  if (traco && aceitaPseudo) {
    traco.animate([{ transform: 'scaleX(0)' }, { transform: 'scaleX(1)' }], {
      duration: total,
      easing: MANOBRA,
      pseudoElement: '::after',
    });
  }
}

// A manobra só começa quando a garagem aparece na tela (no celular ela fica abaixo do resultado).
function tocarQuandoVisivel() {
  const planta = document.querySelector('.garagem__planta[data-manobra]');
  if (!planta || !podeAnimar()) {
    return;
  }
  if (!('IntersectionObserver' in window)) {
    tocar(planta);
    return;
  }
  const observador = new IntersectionObserver(
    (entradas) => {
      if (entradas.some((entrada) => entrada.isIntersecting)) {
        observador.disconnect();
        tocar(planta);
      }
    },
    { threshold: 0.5 },
  );
  observador.observe(planta);
}

document.addEventListener('DOMContentLoaded', () => {
  prepararCampo();
  tocarQuandoVisivel();
});
