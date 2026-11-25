// Comportamento visual comum: menu do celular. Nenhuma regra do sistema aqui.
document.documentElement.classList.add('js');

document.addEventListener('DOMContentLoaded', () => {
  const menu = document.querySelector('.menu-celular');
  const abrir = document.querySelector('[data-abrir-menu]');
  const fechar = document.querySelector('[data-fechar-menu]');
  if (!menu || !abrir || !fechar) {
    return;
  }

  const definirAberto = (aberto) => {
    menu.classList.toggle('esta-aberto', aberto);
    abrir.setAttribute('aria-expanded', String(aberto));
    menu.toggleAttribute('inert', !aberto);
    document.body.style.overflow = aberto ? 'hidden' : '';
    (aberto ? fechar : abrir).focus();
  };

  menu.setAttribute('role', 'dialog');
  menu.setAttribute('aria-modal', 'true');
  menu.toggleAttribute('inert', true);
  abrir.setAttribute('aria-expanded', 'false');
  abrir.setAttribute('aria-controls', menu.id);

  abrir.addEventListener('click', (evento) => {
    evento.preventDefault();
    definirAberto(true);
  });
  fechar.addEventListener('click', () => definirAberto(false));
  document.addEventListener('keydown', (evento) => {
    if (evento.key === 'Escape' && menu.classList.contains('esta-aberto')) {
      definirAberto(false);
    }
  });
  window.matchMedia('(min-width: 64em)').addEventListener('change', (consulta) => {
    if (consulta.matches && menu.classList.contains('esta-aberto')) {
      definirAberto(false);
    }
  });
});
