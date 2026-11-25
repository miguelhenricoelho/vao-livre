# DNA do projeto — sistema de controle de estacionamento

**Dados:** trabalho acadêmico, sem cobrança, com régua de escopo e qualidade do degrau de R$ 20.000 · MVP funcional rodando local, com banco real · tipo de site: **Sistema (área logada)** (Base, 2.5) · Java 25 + Spring Boot 4.2 (adendo 19) · Recursos premium: SIM, com 1 efeito (a animação da portaria, pedida por você) · sem conta de cliente · área da equipe com as duas senhas · prazo: o mais rápido possível, dentro dos prazos fixos da Base.

**Nome:** **Vão Livre**, nome fictício escolhido por você. "Vão livre" é o espaço desimpedido entre dois apoios, e a ideia casa com o que o sistema garante: só entra quem tem espaço livre.

---

## 1. Negócio

Estacionamento que trabalha com vagas contratadas, sem cobrança avulsa. Empresas contratam um número fixo de vagas para os carros dos funcionários, e pessoas físicas contratam vagas para um ou mais carros próprios (1.2, 2.1). Hoje o porteiro confere na mão se o cliente ainda tem vaga antes de liberar a entrada (bloco 8). O sistema troca essa conferência por uma decisão automática na portaria, garantida pelo banco de dados.

## 2. Cliente ideal

Quem usa o sistema é a **equipe do estacionamento**. Os clientes do estacionamento não entram nele.

- **Quem:** o operador da portaria, que registra entradas e saídas o dia todo, quase sempre com um carro esperando, e o administrador, que cadastra clientes, veículos e vagas (6.1).
- **Dor:** conferir na mão se o cliente ainda tem vaga, com o carro parado na cancela (bloco 8). E o medo de liberar o 6º carro de quem só tem 5 vagas (observação da 2.1).
- **Desejo:** "nenhum cliente usando mais vagas do que contratou e cadastro rápido de clientes e veículos" (6.2).
- **Objeções:** "o sistema vai me atrasar na cancela", "e quando o sistema bloquear, o que eu digo ao motorista?" e "cadastrar uma empresa com 30 carros vai levar a tarde inteira". As três são deduzidas da 6.1 e da 6.2, porque a 2.5 não foi respondida.

## 3. Promessa central

**"A placa entra, o sistema responde: libera ou bloqueia, e diz por quê."**

O sistema inteiro sustenta essa frase. A decisão sai em um passo, a regra de vagas nunca estoura (o banco garante) e todo bloqueio vem explicado.

## 4. Voz da marca

- **3 palavras:** confiável, organizada, ágil. São os exemplos que você deu na 4.1, registrados como suposição (Lacunas).
- **Tom (4.3):** formal, simples, sério e discreto. **Sem humor:** sem piada, sem trocadilho, sem 404 engraçada.
- **Fala assim:** como um bom chefe de portaria. Frase curta, o fato primeiro, o número exato e o próximo passo.
- **Não fala assim:** "Ops!", "Algo deu errado :(", "Bem-vindo de volta!", "Soluções inteligentes em mobilidade", nem jargão técnico ("erro 409", "constraint violada").
- **Exemplos:**
  - "Entrada liberada. Construtora Alfa: 4 de 5 vagas em uso."
  - "Entrada bloqueada. As 5 vagas da Construtora Alfa estão em uso. A entrada libera quando um destes veículos sair: BRA2E19, QTP4A21, RIO8C33, SPA1B02, MGX7D45."
  - Lista vazia: "Nenhum veículo no pátio agora."

## 5. Conceito visual

> **Atualizado em 25/11/2026, a seu pedido ("menos cara de IA"):** o conceito do livro de portaria continua, mas o visual mudou.
> - **Paleta:** Papel `#F3F1EC`, Papel-2 `#E7E4DC`, Tinta `#121212` (16,6:1), Tinta-2 `#57544E` (6,7:1) e Laranja-sinal `#D4471C`, este só em gráficos (3,9:1). O texto em laranja usa o Laranja-escuro `#A8360F` (5,8:1).
> - **Layout:** menu no topo, linhas finas no lugar de cartões, botões pretos e rótulos de seção na margem.
> - **Fontes:** títulos em Archivo expandida, texto em Atkinson Hyperlegible Next e placas em Atkinson Hyperlegible Mono.
> - **Motivo gráfico novo:** os pontos de vaga (cheio = ocupada) no painel.
>
> A paleta e a tabela abaixo são as da primeira versão, substituída por esta.

**"O livro de portaria passado a limpo."** O caderno pautado em que o porteiro confere vagas à mão (bloco 8) vira um sistema preciso. Ele mantém o papel claro, as linhas da pauta e as duas canetas de sempre: a **azul**, que registra o que entrou, e a **vermelha**, que risca o que não pode entrar. A diferença para os concorrentes é que nenhum sistema de estacionamento parece um documento bem feito: quase todos parecem um painel de controle escuro.

- **Direções descartadas:**
  - "Central de operações", com fundo escuro, neon e gráficos. É a mais óbvia para "sistema" e cansa a vista num turno de 8 horas.
  - "Sinalização viária", com zebrado amarelo e preto e o azul da placa "E". É o clichê do segmento e soaria como alerta o tempo todo, contra o "discreto" da 4.3.
- **Convenção do segmento que vamos quebrar:** ícone de carrinho, letra "P" azul, zebrado amarelo e preto, cinza-asfalto chapado e dashboard escuro. Aqui o visual vai de papel claro, pauta fina, as duas canetas e a placa como protagonista tipográfica.
- **Referências da sua lista (6.0):**
  - Pentagram: grid rigoroso e tipografia como imagem principal. Aqui, a placa é a imagem.
  - Stripe Press: acabamento de documento impresso, cada tela tratada como uma página do livro de portaria.
  - Bang & Olufsen: controle e pouca cor.
  - **Não pegar:** a frieza da B&O. O calor vem do papel, que puxa levemente para o verde de concreto pintado.

### Paleta

| Token | Cor | Uso |
| --- | --- | --- |
| Concreto | `#EEF0EC` | fundo (neutro frio, cor de piso de concreto pintado) |
| Concreto-2 | `#E1E4DF` | variação do fundo: cards, linhas de tabela, quadro de vagas |
| Asfalto | `#1A2023` | texto e barra lateral |
| Asfalto-2 | `#4F585D` | variação do texto: texto secundário e borda de campo |
| Caneta azul | `#2340A8` | cor de marca: botões, links, "liberado", foco |
| Caneta vermelha | `#A8261D` | acento: "bloqueado", ações destrutivas, erro |
| Transparências (tokens) | Azul 15%, Vermelho 15%, Asfalto 15% | fundo do resultado da portaria e linhas da pauta, sempre como token no `global.css` |

Nada depende só da cor: "liberado" e "bloqueado" sempre têm ícone e palavra junto.

### Contraste (fórmula WCAG, todas as combinações usadas; calculado em 25/11/2026)

| Combinação | Razão | Uso permitido |
| --- | --- | --- |
| Asfalto sobre Concreto | 14,36:1 | todo texto |
| Asfalto sobre Concreto-2 (cards) | 12,84:1 | todo texto |
| Asfalto-2 sobre Concreto | 6,34:1 | texto secundário |
| Asfalto-2 sobre Concreto-2 (cards) | 5,67:1 | texto secundário |
| Caneta azul sobre Concreto | 7,73:1 | links e texto de destaque |
| Caneta azul sobre Concreto-2 | 6,90:1 | links dentro de cards |
| Branco sobre Caneta azul | 8,86:1 | texto de botão principal |
| Caneta vermelha sobre Concreto | 6,19:1 | texto de bloqueio e de erro |
| Caneta vermelha sobre Concreto-2 | 5,53:1 | texto de bloqueio em cards |
| Branco sobre Caneta vermelha | 7,09:1 | botão destrutivo |
| Concreto sobre Asfalto (barra lateral) | 14,36:1 | links da barra lateral |
| Asfalto sobre Azul 15% (sobre Concreto) | 12,86:1 | texto do painel "liberado" |
| Caneta azul sobre Azul 15% | 6,07:1 (medido no esboço) | título do painel "liberado" |
| Asfalto-2 sobre Azul 15% | 4,99:1 (medido no esboço) | linha de registro do painel "liberado" |
| Asfalto sobre Vermelho 15% (sobre Concreto) | 11,27:1 (medido no esboço) | texto do painel "bloqueado" |
| Caneta vermelha sobre Vermelho 15% | 4,85:1 (medido no esboço) | título do painel "bloqueado" e borda do botão do WhatsApp |
| Asfalto-2 sobre Vermelho 15% | 4,98:1 (medido no esboço) | linha de registro do painel "bloqueado" |
| Asfalto-2 sobre Asfalto | 2,26:1 | **proibido** para qualquer texto |

- **Texto sobre foto:** nenhum. O sistema não usa fotos.
- **Botões e campos contra o fundo:** botão Caneta azul sobre Concreto 7,73:1. Borda de campo Asfalto-2 sobre Concreto 6,34:1. Os cards se separam do fundo por uma linha de pauta (Concreto-2 contra Concreto dá 1,12:1, só decorativo, nunca a única pista).
- **Foco do teclado:** contorno Caneta azul nos fundos claros (7,73:1 no Concreto, 6,90:1 nos cards). Na barra lateral Asfalto o contorno é Concreto (14,36:1), porque o azul sobre Asfalto dá só 1,86:1.

### Tipografia

- **Atkinson Hyperlegible Next** para títulos e texto, e **Atkinson Hyperlegible Mono** para placas, horários e números de tabela. As duas são do Google Fonts, com licença SIL Open Font License 1.1, e ficam hospedadas no próprio sistema.
- **Por que:** a família foi desenhada pelo Braille Institute para não confundir letras parecidas. Numa placa, 0 com O e 1 com I é exatamente o erro que libera o carro errado. Por isso a placa é o elemento tipográfico principal, como pede a referência Pentagram.
- **Algarismos:** alinhados (não sobem nem descem na linha). Na Mono, todos têm a mesma largura, então horários e contagens ficam alinhados nas tabelas.
- **Tamanhos reais:**

| Elemento | Desktop | Celular |
| --- | --- | --- |
| Título da página (h1; o sistema não tem hero) | 32 px | 26 px |
| Maior título de seção | 28 px | 24 px |
| Placa no resultado da portaria (número de destaque) | 48 px | 36 px |
| Texto, tabelas, botões e campos | 16 px | 16 px |
| Rótulos e selos (até 4 palavras) | 14 px | 14 px |

- **Sistema:** escala modular 1,25 sobre 16 px, espaçamento em base 4/8, grid de 12 colunas no desktop, raio 4 px (papel, não bolha), sem sombras grandes. A elevação é feita pela linha de pauta.

## 6. Elemento de assinatura e recursos premium

**O "Quadro de vagas", com a manobra na portaria.** O quadro nasce da regra principal (observação da 2.1: "se a empresa tem direito a 5 vagas, até 5 carros entram; o 6º fica bloqueado") e da 6.2. A animação é o seu pedido extra: ela mostra o que o servidor decidiu, nunca decide nada.

### O quadro (parado)

- Cada cliente tem a sua garagem: uma planta vista de cima com **uma vaga desenhada por vaga contratada**. 5 vagas, 5 vagas pintadas.
- Cada vaga ocupada mostra a placa em Mono, em Caneta azul, e o horário de entrada. A vaga livre mostra "Livre".
- Quando o 6º carro é bloqueado, ele aparece **fora da margem**, diante da cancela fechada, com um risco em Caneta vermelha e a palavra "Bloqueado".
- **Tudo vem do banco:** quantas vagas tem o contrato, quais estão ocupadas, por qual placa e desde quando. Cada estadia aberta ocupa uma vaga numerada do quadro (coluna `vaga_no_quadro`; plano curto), então um carro estacionado não muda de lugar no desenho quando outro sai.
- O quadro aparece em três lugares:
  - no resultado da portaria, **com a animação**;
  - na ficha do cliente, parado;
  - no painel, um por cliente, em ordem de ocupação, parado.
- **Funciona sem JavaScript:** o quadro é SVG montado no servidor pelo Thymeleaf, já no estado final (adendo J.2).

### A manobra (a animação da portaria)

**Três direções pensadas:**

1. **Vista lateral da cancela:** o carro de perfil, o braço da cancela subindo. **Descartada por ser a mais óbvia.** É a imagem que todo mundo associa a estacionamento, e de lado não dá para mostrar as vagas do cliente: o carro entraria "em lugar nenhum", e a animação viraria enfeite.
2. **Vista de cima do pátio inteiro:** todas as vagas do estacionamento, e o carro procurando uma. **Descartada.** A regra é por quantidade contratada, não por vaga física. Desenhar o pátio inteiro inventaria posições que o banco não tem e misturaria os carros de todos os clientes numa tela que é sobre um só.
3. **Planta da garagem do cliente, que é o próprio Quadro de vagas.** **Escolhida.** A garagem da animação é exatamente o mapa de vagas do cliente que acabou de chegar à portaria, com as vagas livres e ocupadas vindo do banco. O carro entra pela cancela, que fica na margem do livro, e estaciona na vaga que o servidor reservou. Assim a animação e o elemento de assinatura viram uma coisa só, e tudo que se move corresponde a um dado real.

**Como é o desenho (SVG próprio):**

- Planta vista de cima, em traço de 1,5 px (o mesmo traço dos ícones), no estilo de desenho técnico: as linhas de demarcação são a pauta do livro, em Asfalto 15%.
- **O carro:** silhueta vista de cima, com proporção real de carro de passeio (cerca de 1:2,2). Tem contorno em Asfalto, preenchimento Concreto-2 e só três detalhes em linha: para-brisa, vidro traseiro e a divisão do capô. Sem rosto, sem rodas à mostra, sem brilho e sem cor de carrinho de brinquedo.
- **A cancela:** um pivô e um braço em linha reta na margem esquerda do quadro. O braço fica em Asfalto quando está fechado e ganha a marca Caneta azul quando abre.
- **Cores:** só tokens (Concreto, Concreto-2, Asfalto, Caneta azul, Caneta vermelha e as transparências). Nenhum `rgb()` ou hex fora do `global.css`.
- **Fileiras:** até 10 vagas por fileira no desktop e 5 no celular, com o corredor entre elas. Um cliente com 30 vagas tem 3 fileiras no desktop e 6 no celular.

**O que acontece em cada resultado** (durações dentro de 1,2 a 2 s; cada etapa entre 300 e 700 ms):

| Resultado | Quadro a quadro | Total |
| --- | --- | --- |
| **Liberado** | 0 a 0,4 s: o carro chega pela esquerda e desacelera diante da cancela. 0,3 a 0,6 s: o braço da cancela sobe (gira 80°). 0,6 a 1,3 s: o carro segue pelo corredor e entra de frente na vaga reservada (deslocamento mais um giro de 90°). 1,3 a 1,6 s: o braço desce e a vaga recebe a marca em Caneta azul. | 1,6 s |
| **Bloqueado** (vagas esgotadas) | 0 a 0,5 s: o carro chega e para diante da cancela, que continua fechada. 0,5 a 0,65 s: pausa curta, o carro parado. 0,65 a 1,15 s: o carro dá ré até a margem. 1,15 a 1,45 s: o risco em Caneta vermelha aparece sobre a vaga fora da margem (traço que cresce por `scaleX`). | 1,45 s |
| **Saída** | 0 a 0,5 s: o carro sai da vaga de ré e se alinha no corredor. 0,4 a 0,7 s: o braço da cancela sobe. 0,7 a 1,2 s: o carro atravessa a cancela e sai da planta. 1,2 a 1,5 s: o braço desce e a vaga volta a mostrar "Livre". | 1,5 s |
| **Outros bloqueios** (contrato vencido ou suspenso, placa não cadastrada, veículo desativado) | Mesmo movimento do "Bloqueado". Se a placa não é cadastrada, não há garagem de cliente para mostrar, e a planta mostra só a cancela e o carro. | 1,45 s |

**Quem decide:** o Java. O operador envia a placa; o `PortariaService`, numa transação com a linha do contrato travada, decide, grava e devolve o resultado. A página volta montada pelo servidor com o texto do resultado e o SVG no **estado final**, mais atributos `data-` só de apresentação: o tipo de manobra (`entrada`, `bloqueio` ou `saida`) e a vaga de destino. O `portaria.js` só lê esses atributos e toca a manobra. Ele não sabe nada de vagas, contratos ou limites.

**Texto primeiro, animação depois:** o resultado ("Entrada liberada" ou "Entrada bloqueada"), o motivo, a placa e o contador aparecem prontos no carregamento, antes de qualquer movimento, numa região `role="status"` que o leitor de tela anuncia. Exemplo de bloqueio: "Entrada bloqueada. Construtora Alfa já está com 5 de 5 vagas em uso." O SVG tem `role="img"` e uma descrição curta do estado final.

**Regras técnicas (Base, 6.5, e adendo J.2):**

- Só `transform` e `opacity`, pela Web Animations API, que o CSP permite sem estilo em linha. Nada de animar `left`, `top`, `width` ou traço de SVG; o risco vermelho cresce por `scaleX`.
- Curvas próprias, nunca `linear`:
  - chegada: `cubic-bezier(0.22, 0.8, 0.24, 1)`, que desacelera como um carro freando;
  - manobra: `cubic-bezier(0.55, 0, 0.35, 1)`;
  - cancela: `cubic-bezier(0.3, 0, 0.2, 1)`.
- 60 fps: medido no painel de Performance do Chrome, sem quadro longo durante a manobra. O resultado vai no relatório.
- **Nunca atrasa a portaria:** o campo da placa recebe o foco na hora. Se o operador registra outra placa no meio da manobra, a página seguinte carrega normalmente, e a manobra anterior simplesmente não continua.
- JavaScript em `partes/portaria/portaria.js`, junto com o `portaria.html` e o `portaria.css`. Sem biblioteca e sem framework.

**Plano B** (Base, 6.5, obrigatório):

- **Sem JavaScript, com `prefers-reduced-motion` ligado, ou sem suporte à Web Animations API** (detectado com `'animate' in Element.prototype`): o SVG fica parado no estado final, que já vem pronto do servidor. Liberado: o carro estacionado na vaga. Bloqueado: o carro diante da cancela fechada, com o risco vermelho. Saída: a vaga livre.
- O texto é o mesmo nos dois casos. Nada some e nada fica pela metade.
- **Como vou testar:** com o JavaScript desligado e com o movimento reduzido emulado no navegador. O Safari do iPhone vai para a lista "não testei".

**Efeitos premium (Módulo C, C.1 e C.2; máximo 4):** 1 efeito, a manobra. Ela conta como ilustração animada (C.2), feita em SVG e Web Animations em vez de Rive, para não somar biblioteca. "Recursos premium" passou de NÃO para SIM por causa do seu pedido, e um efeito pedido pelo cliente nunca vai para P1: ele é P0.

**Acabamento (C.4, fora do limite):** seleção de texto e barra de rolagem nas cores da marca, ícones de uma família só (traço fino, combinando com a pauta), 404 e páginas de erro sóbrias e com caminho de volta, e números de tabela em Mono.

**Intro de marca:** sem intro. A C.1.1 proíbe intro em página de sistema, e o gesto forte, o risco da caneta vermelha, já vive no quadro de vagas.

## 7. Ideias selecionadas (aba Ideias por resposta)

1. **Quadro de vagas com a manobra na portaria** como assinatura: a garagem do cliente desenhada com os dados do banco, e o carro entrando, sendo barrado ou saindo conforme o que o servidor decidiu. Nasce da observação da 2.1, da 6.2 e do seu pedido extra.
2. **Portaria em um passo:** um campo de placa só. Se o carro está fora, o sistema propõe a entrada; se já está dentro, propõe a saída. Nasce da 6.1 ("registra a entrada e a saída e o sistema libera ou bloqueia na hora").
3. **Bloqueio explicado:** motivo em uma frase, as placas que ocupam as vagas e desde quando estão lá, para o operador responder ao motorista. Nasce da 6.1 e do bloco 8.
4. **Avisar o cliente em um toque:** no bloqueio, um botão abre o WhatsApp do contato do cliente com a mensagem já escrita (montada no Java). Nasce da 6.2.
5. **Cadastro rápido:** cliente, contrato e todos os veículos numa tela só, com "adicionar outro veículo" sem sair da página. Nasce da 6.2.
6. **Histórico que inclui os bloqueios:** cada tentativa fica registrada, liberada ou bloqueada, com motivo e operador, e tem filtros por período, cliente, placa e resultado. Nasce do bloco 8 ("histórico de acessos").
7. **Painel com o que importa agora:** veículos no pátio, clientes no limite, bloqueios de hoje e contratos vencendo, tudo com números reais do banco. Nasce da 6.2 e do bloco 8.
8. **Placa à prova de erro:** aceita o formato antigo (AAA9999) e o Mercosul (AAA9A99), normaliza maiúsculas e hífen e usa a fonte hiperlegível. Nasce da 6.1.

**Descartadas:**
- Leitura de placa por câmera (OCR): exige equipamento e foge do escopo.
- Vagas numeradas no chão: a regra é por quantidade, não por vaga física.
- Cobrança avulsa de visitante: Pagamentos está em NÃO.
- Tema escuro: fica como P2.
- Aplicativo instalável (PWA): fica como P2.
- Relatório em PDF: fica como P2.
- Condutores autorizados por veículo: fica como P2.

## 8. Ação principal e medição

- **Ação:** o operador registra a entrada ou a saída e recebe a decisão na hora (6.1).
- **CTA principal da portaria:** "Registrar entrada" (ou "Registrar saída", quando o carro já está dentro).
- **CTA principal do cadastro:** "Salvar cliente e veículos".
- **Sucesso:**
  - **Zero estouros de vagas:** provado por um teste automático com entradas simultâneas, e por uma consulta no painel que conta contratos acima do limite (precisa dar sempre 0).
  - **Decisão rápida:** a resposta da portaria sai em menos de 1 segundo no computador local, medida nos testes [REVISAR COM O CLIENTE].
  - **Cadastro rápido:** uma empresa com 5 veículos cadastrada em menos de 2 minutos, cronometrado no esboço [REVISAR COM O CLIENTE].

## 9. Estrutura e funcionalidades

**Tipo de site:** Sistema (área logada). Sem parte pública: veja a **EXCEÇÃO 1**, nas Lacunas.

### Telas, na ordem do uso

| Tela | Endereço | Quem acessa | Papel |
| --- | --- | --- | --- |
| Senha da área | `/entrar` | todos | primeira camada: a senha da área da equipe |
| Login | `/login` | quem passou pela senha da área | segunda camada: e-mail e senha de cada pessoa |
| Portaria | `/portaria` | operador e administrador | campo da placa, resultado com o quadro de vagas, "No pátio agora" |
| Painel | `/painel` | administrador | indicadores, quadro de vagas de todos os clientes, alertas |
| Clientes | `/clientes` | administrador (o operador só vê nome e contato no bloqueio) | lista com busca, filtros, ordenação e paginação |
| Cadastro rápido | `/clientes/novo` | administrador | cliente, contrato e veículos numa tela só |
| Ficha do cliente | `/clientes/{id}` | administrador | dados, contrato, veículos, quadro de vagas e histórico do cliente |
| Contrato | `/contratos/{id}` | administrador | vagas, vigência e situação; mudar vagas pede a senha de novo |
| Veículos | `/veiculos` | administrador | lista, busca e cadastro avulso |
| Histórico | `/historico` | operador (leitura) e administrador | todas as tentativas, com filtros |
| Equipe | `/equipe` | administrador | contas, papéis, desativar pessoa, trocar a senha da área |
| Auditoria | `/auditoria` | administrador | quem fez cada ação administrativa, e quando |
| Minha conta | `/conta` | todos | trocar a própria senha |
| Ajuda | `/ajuda` | todos | como usar a portaria e WhatsApp do suporte |
| Privacidade | `/privacidade` | todos | o que o sistema guarda, por quê, por quanto tempo e quem vê |
| Erros | 403, 404, 500 | todos | identidade do sistema e caminho de volta |

- **Navegação:**
  - No desktop, barra lateral fixa com os links do papel da pessoa e a página atual destacada.
  - No celular e no tablet da guarita, barra no topo com "Menu", que abre o menu em tela cheia com links grandes.
  - A Portaria fica sempre a um toque.
- **Rolagem estilo PicPay:** nenhuma tela. A tabela 2.5 proíbe no painel, e o sistema não tem página pública (EXCEÇÃO 1).

### Regras do sistema (todas no Java; adendo J.2)

1. **Entrada liberada** só se todas estas condições valem:
   - o veículo está cadastrado e ativo;
   - o cliente está ativo;
   - o contrato está ATIVO e dentro da vigência;
   - o veículo ainda não está no pátio;
   - as estadias abertas do contrato são menos que as vagas contratadas.
2. **Bloqueio**, sempre com motivo. Os motivos possíveis são: vagas esgotadas, contrato suspenso, contrato vencido, cliente inativo, veículo desativado, placa não cadastrada ou veículo já no pátio.
3. **Saída sempre registrada:** nunca se bloqueia um carro de sair. Se a placa não está no pátio, o sistema avisa e não cria nada.
4. **Garantia no banco** (adendo J.5):
   - Toda entrada roda numa transação que trava a linha do contrato (`SELECT ... FOR UPDATE`), conta as estadias abertas, reserva a primeira vaga livre do quadro (de 1 até o número de vagas) e só então grava.
   - Um índice único parcial impede o mesmo veículo de ter duas estadias abertas.
   - Outro índice único parcial impede duas estadias abertas na mesma vaga do quadro do mesmo contrato. É uma segunda trava do limite, feita pelo próprio banco.
   - O contrato não pode ter as vagas reduzidas abaixo das que estão em uso naquele momento.
   - O teste de concorrência dispara entradas ao mesmo tempo com uma vaga livre e prova que só uma passa.

### Ferramenta de perguntas (Módulo D, D.0)

Não se aplica: na tabela 2.5 ela é obrigatória "na parte pública", e o sistema não tem parte pública (EXCEÇÃO 1). A decisão da portaria cumpre o papel de "responder algo útil", mas não é uma ferramenta de perguntas no formato D.0, e eu não vou fingir que é.

### Página "Nossa história"

Não se aplica, pela mesma EXCEÇÃO 1.

### Canais de contato

| Onde | Quem manda para quem | Mensagem pronta no WhatsApp (montada no Java, com `URLEncoder` e `%20`) |
| --- | --- | --- |
| Botão "Avisar o cliente", no resultado de bloqueio por vagas esgotadas | portaria → contato do cliente (telefone do cadastro) | "Olá, Construtora Alfa. Aqui é a portaria do Vão Livre. O veículo ABC1D23 chegou às 08:12 de 25/11/2026, mas as 5 vagas contratadas estão em uso: BRA2E19, QTP4A21, RIO8C33, SPA1B02 e MGX7D45. A entrada fica liberada assim que um desses veículos sair. (pelo sistema)" |
| Botão "Avisar o cliente", no bloqueio por contrato vencido ou suspenso | portaria → contato do cliente | "Olá, Construtora Alfa. Aqui é a portaria do Vão Livre. O veículo ABC1D23 não pôde entrar às 08:12 de 25/11/2026 porque o contrato de vagas está vencido. Por favor, fale com a administração do estacionamento. (pelo sistema)" |
| Ajuda, botão "Pedir ajuda ao suporte" | operador → WhatsApp do suporte, que o administrador cadastra em Equipe | "Olá. Sou Ana, da portaria do Vão Livre. Preciso de ajuda na tela Portaria com a placa ABC1D23. (pelo sistema)" |

Os nomes, as placas e os horários das mensagens acima são de exemplo. No sistema, cada campo vem do banco e do que o operador fez.

- **Sem formulário de contato:** os textos dos botões dizem a mesma intenção da mensagem ("Avisar o cliente", "Pedir ajuda ao suporte").
- **Sem botão flutuante e sem redes:** a tabela 2.5 só exige os dois "na parte pública" (EXCEÇÃO 1), e nenhuma rede foi informada (9.4 em branco).
- **Final da mensagem:** "(pelo sistema)" no lugar de "(pelo site)" (EXCEÇÃO 2).

### Papéis e permissões

| Ação | Administrador | Operador |
| --- | --- | --- |
| Registrar entrada e saída | sim | sim |
| Ver pátio e histórico | sim | sim, só leitura |
| Ver dados do cliente | tudo | só nome, contato e quadro de vagas, no bloqueio |
| Cadastrar ou editar cliente, veículo e contrato | sim | não |
| Mudar vagas, desativar cliente ou mudar papel | sim, pedindo a própria senha de novo | não |
| Equipe, senha da área e auditoria | sim | não |

As permissões são conferidas no servidor por rota e por método (Spring Security), nunca só escondendo botão. Os testes provam que um operador recebe 403 nas rotas de administrador e não consegue alterar dados pela URL.

### Módulos ativados

| Módulo | Situação | P0 | P1 |
| --- | --- | --- | --- |
| Autenticação | SIM | duas senhas, login por sessão, troca de senha, bloqueio por tentativas | recuperação pelo administrador (EXCEÇÃO 3) |
| Papéis (RBAC) | SIM | administrador e operador | — |
| Banco e CRUD | SIM | clientes, contratos, veículos, estadias, ocorrências | — |
| Painel | SIM | indicadores e quadro de vagas | gráfico de ocupação por hora do dia |
| Tema claro/escuro | decidido: não | só tema claro | escuro como P2 |
| SEO público | decidido: não | `noindex` e `robots.txt` bloqueando tudo, porque é sistema interno | — |
| Recursos premium | SIM (seu pedido extra) | 1 efeito: a manobra da portaria, com plano B | — |
| Pagamentos, e-mails, E, F, G | NÃO | — | — |

**P0, todos garantidos:** as duas senhas, a portaria com bloqueio garantido no banco e o teste de concorrência, o quadro de vagas com a manobra animada (liberado, bloqueado e saída), o cadastro rápido, as listas de clientes e veículos com busca e paginação, a ficha do cliente com contrato, o histórico com bloqueios, o painel, a equipe e os papéis, a auditoria, a privacidade e os erros.

**P1:** "Avisar o cliente" pelo WhatsApp, Ajuda com WhatsApp do suporte, gráfico de ocupação por hora, recuperação de senha pelo administrador e script de backup do banco (`pg_dump`).

**P2 (só sugestão):** tema escuro, PWA, exportar histórico em CSV ou PDF, condutores autorizados e 2FA.

### Destino de cada formulário

Todos gravam no PostgreSQL local, com validação no servidor (Bean Validation). Nenhum formulário manda e-mail (módulo NÃO).

### Estados por horário

Nenhum. Suponho atendimento 24 horas, sem regra de horário (Lacunas).

### Conta de cliente

Caminho **b**: sem conta de cliente, porque os clientes do estacionamento não usam o sistema; só a equipe entra. A área da equipe tem as duas senhas (Módulo A, A.1). O "link discreto no rodapé" não se aplica, porque o sistema inteiro é a área da equipe e a entrada é a própria tela `/entrar`.

### Serviços pagos

Nenhum. Tudo roda local: Java, PostgreSQL no Docker e fontes hospedadas no próprio sistema. Publicar no Render não é obrigatório e, se você quiser, eu mostro o plano com os custos antes (adendo J.7).

### Ordem de corte, se o prazo apertar

1. "Avisar o cliente" pelo WhatsApp (os botões saem e o resultado continua explicado).
2. O gráfico de ocupação por hora vira só os números.
3. A tela de auditoria fica só com a lista, sem filtros (a gravação do log continua).
4. A Ajuda vira um texto curto, sem WhatsApp.
5. Os filtros do histórico ficam só em período e placa.

**Nunca saem**, porque são pedidos com destaque (bloco 8, bloco 13 e o seu pedido extra): login da equipe com papéis, cadastro de clientes, veículos e vagas, a portaria com bloqueio garantido no banco, o histórico, o modelo de banco, a cara de sistema de empresa e a manobra animada.

A manobra só pode ficar mais simples, e só com a sua aprovação. Na pior hipótese, a saída usa o mesmo movimento da entrada ao contrário, e o risco vermelho do bloqueio aparece sem crescer. O quadro de vagas sustenta a promessa e é o último a mudar.

---

## Plano curto (Base, seção 4)

**Stack, em uma linha:** Java 25 (LTS) + Spring Boot 4.2.0 + Thymeleaf + Spring Security + Spring Data JPA + PostgreSQL com Flyway, build com Maven Wrapper. É a stack fixa do adendo 19, com as versões estáveis conferidas hoje.

### Bibliotecas no `pom.xml` (uma linha cada)

| Biblioteca | Para quê |
| --- | --- |
| spring-boot-starter-web | servidor HTTP e controllers |
| spring-boot-starter-thymeleaf | telas montadas no servidor |
| thymeleaf-extras-springsecurity | mostrar o menu certo para cada papel no HTML (a proteção real fica no servidor) |
| spring-boot-starter-security | login por sessão, papéis, CSRF e cabeçalhos |
| spring-boot-starter-data-jpa | acesso ao banco e a trava da linha do contrato |
| spring-boot-starter-validation | Bean Validation nos formulários |
| postgresql (driver) | conexão com o PostgreSQL |
| flyway-core e flyway-database-postgresql | tabelas só por migration |
| bucket4j | limite de tentativas no login e na senha da área |
| spring-boot-devtools (só no dev) | recarregar HTML e CSS sem reiniciar |
| spring-boot-docker-compose (só no dev) | subir o Postgres local sozinho |
| spring-boot-starter-test e spring-security-test | JUnit 5, Mockito, MockMvc, `@WithMockUser` |
| testcontainers (postgresql) | testar migrations e concorrência num Postgres de verdade |
| Plugins: Checkstyle, Spotless, OWASP Dependency-Check | lint, formatação e auditoria de dependências no `./mvnw verify` |

### Modelo de dados (8 tabelas, todas por migration do Flyway, `ddl-auto=validate`)

| Tabela | Campos principais | Restrições que garantem a regra |
| --- | --- | --- |
| `usuario` | nome, e-mail, senha_hash (BCrypt), papel, ativo, deve_trocar_senha | e-mail único (sem diferenciar maiúsculas); papel só ADMIN ou OPERADOR (CHECK) |
| `configuracao` | senha_area_hash, versao_senha_area, whatsapp_suporte | uma linha só (CHECK id = 1); a versão sobe quando a senha da área muda e derruba todas as sessões |
| `cliente` | tipo (PF ou PJ), nome ou razão social, documento (CPF ou CNPJ), telefone, e-mail, ativo | documento único; tamanho do documento conferido pelo tipo (CHECK) |
| `contrato` | cliente, vagas_contratadas, início, fim, situação | vagas > 0 (CHECK); fim ≥ início; **um contrato ATIVO por cliente** (índice único parcial) |
| `veiculo` | cliente, placa, modelo, cor, ativo | formato de placa antigo ou Mercosul (CHECK com expressão regular); **placa única entre os ativos** (índice parcial), o que permite registrar um carro vendido para outro cliente |
| `estadia` | veículo, contrato, vaga_no_quadro, entrada_em, saída_em, registrada_por, encerrada_por | saída ≥ entrada; vaga_no_quadro ≥ 1; **uma estadia aberta por veículo** (índice único parcial onde saída é nula); **uma estadia aberta por vaga do quadro em cada contrato** (índice único parcial em contrato + vaga_no_quadro) |
| `ocorrencia_portaria` | placa informada, veículo, tipo (entrada ou saída), resultado (liberado ou bloqueado), motivo, operador, ocorrido_em | motivo obrigatório quando bloqueado (CHECK); índices por data e por veículo para o histórico |
| `auditoria` | usuário, ação, entidade, id da entidade, ocorrido_em, resumo | só inserção; o resumo nunca leva dado pessoal |

Datas em `timestamptz`, mostradas no fuso de Brasília. Exibição em dd/mm/aaaa.

**Por que este modelo é melhor que o modelo básico cliente, carro e vaga:**

1. O **contrato é separado do cliente**. Mudar de 5 para 8 vagas não apaga a história, e contrato vencido ou suspenso bloqueia sozinho.
2. **Estadia** (quem está no pátio) é diferente de **ocorrência** (cada tentativa). Assim o histórico mostra também os bloqueios, que é o que prova que a regra funciona.
3. O **limite é garantido no banco**, com trava de linha e dois índices únicos dentro de transação, e não só conferido na tela.
4. **PF e PJ ficam numa tabela só**, com CHECK pelo tipo, em vez de duas tabelas quase iguais.
5. A placa é única só entre os veículos ativos, o que permite que um carro mude de dono sem perder o histórico.
6. A **auditoria** registra quem mudou vagas, papéis ou desativou alguém.

### Estrutura de pastas (adendo J.3)

```
pom.xml, mvnw, mvnw.cmd, compose.yaml, .env.example, README.md
src/main/java/br/com/vaolivre/
  Aplicacao.java
  global/       GlobalConfig.java, GlobalSeguranca.java, GlobalErros.java
  acesso/       AcessoController.java, AcessoService.java (senha da área), LoginLimite.java
  portaria/     PortariaController.java, PortariaService.java, PortariaForm.java,
                LimiteDeVagasAtingidoException.java, ResultadoPortaria.java
  estadias/     Estadia.java, EstadiasRepository.java
  ocorrencias/  Ocorrencia.java, OcorrenciasRepository.java
  painel/       PainelController.java, PainelService.java
  clientes/     ClientesController.java, ClientesService.java, ClientesRepository.java,
                Cliente.java, ClienteForm.java, CadastroRapidoForm.java
  contratos/    ContratosController.java, ContratosService.java, ContratosRepository.java,
                Contrato.java, ContratoForm.java
  veiculos/     VeiculosController.java, VeiculosService.java, VeiculosRepository.java,
                Veiculo.java, VeiculoForm.java, Placa.java
  historico/    HistoricoController.java, HistoricoService.java, FiltroHistorico.java
  equipe/       EquipeController.java, EquipeService.java, UsuariosRepository.java,
                Usuario.java, UsuarioForm.java
  auditoria/    AuditoriaController.java, AuditoriaService.java, AuditoriaRepository.java,
                RegistroAuditoria.java
  conta/        ContaController.java, ContaService.java, TrocaSenhaForm.java
  whatsapp/     MensagensWhatsApp.java (monta as mensagens e os links)
  ajuda/        AjudaController.java
  privacidade/  PrivacidadeController.java
src/main/resources/
  partes/global/      global.css, global.html, global.js (menu do celular), fontes/
  partes/acesso/      acesso.html, acesso.css, conteudo.properties
  partes/portaria/    portaria.html, quadro.html (SVG do quadro), portaria.css,
                      portaria.js (máscara da placa e manobra; nenhuma regra), conteudo.properties
  partes/painel/      painel.html, painel.css, conteudo.properties
  partes/clientes/    clientes.html, cliente.html, cadastro.html, clientes.css, clientes.js, conteudo.properties
  partes/contratos/   contratos.html, contratos.css, conteudo.properties
  partes/veiculos/    veiculos.html, veiculos.css, conteudo.properties
  partes/historico/   historico.html, historico.css, conteudo.properties
  partes/equipe/      equipe.html, equipe.css, conteudo.properties
  partes/auditoria/   auditoria.html, auditoria.css, conteudo.properties
  partes/conta/       conta.html, conta.css, conteudo.properties
  partes/ajuda/       ajuda.html, ajuda.css, conteudo.properties
  partes/privacidade/ privacidade.html, privacidade.css, conteudo.properties
  partes/erros/       erros.html, erros.css, conteudo.properties
  db/migration/       V1__estrutura.sql, V2__indices_e_restricoes.sql
  application.properties, application-dev.properties, application-prod.properties
src/test/java/br/com/vaolivre/
  portaria/  PortariaServiceTest.java, PortariaConcorrenciaTest.java (Testcontainers),
             QuadroDeVagasTest.java (o SVG final e os atributos da manobra batem com o banco)
  global/    PermissoesTest.java (MockMvc), ModelosNaoBaixamTest.java
  veiculos/  PlacaTest.java
  ...
```

Estadias e ocorrências são partes só de dados (Java), sem tela própria: as telas delas são a Portaria e o Histórico. Os dados de exemplo (nomes claramente fictícios) entram só no perfil `dev`, numa migration separada, nunca no `prod`.

### Como você vai abrir (adendo J.4)

- **Desenvolvimento:** `./mvnw spring-boot:run` com o perfil `dev` → `http://localhost:5173`.
- **Produção local:** `java -jar target/vao-livre.jar --spring.profiles.active=prod --server.port=4173` → `http://localhost:4173`.
- **Antes de construir, a máquina precisa de:** Java 25 (Temurin) e Docker Desktop, que ainda não estão instalados (o passo a passo já te mandei).
- **Dependency-Check:** a primeira execução baixa a base de vulnerabilidades e pode levar vários minutos. Uma chave gratuita da NVD deixa isso bem mais rápido; ela fica numa variável de ambiente, nunca no repositório.

---

## 10. Lacunas

**O briefing veio incompleto:** endereço, horário, as 3 palavras, logo, visual, WhatsApp, e-mail e redes ficaram em branco. Resolvi tudo com suposição. As 3 perguntas da versão anterior já foram respondidas:

- **Nome:** Vão Livre.
- **Prazo:** o mais rápido possível. Valem os prazos fixos da Base como teto.
- **Foto do quadro do professor:** não existe. O DNA não faz comparação com ela.

### Conflito registrado (Base, seção 1, ordem de prioridade)

Você pediu que o contador "atualize (ex.: 4 de 5 → 5 de 5)". A regra fixa da Base (6.4) diz que **número nunca mostra um valor diferente do final** e nunca espera animação. **Vence a Base.**

- O texto mostra "5 de 5 vagas em uso" desde o primeiro instante.
- A mudança aparece no desenho: o carro entra e a 5ª vaga recebe a marca azul.
- O número ganha só um sublinhado em Caneta azul, que cresce junto com a manobra. É um detalhe permitido pela 6.4.

Assim, quem olha vê o que mudou, e quem só lê o texto nunca vê um número errado.

### Suposições registradas

- **3 palavras:** confiável, organizada, ágil (os exemplos da 4.1).
- **Visual (5.6):** claro, com personalidade contida, calmo.
- **Logo (5.1 e 5.7 em branco):** caminho "manter". Como não há logo, o nome entra escrito na fonte do sistema, e o logo é provisório.
- **Funcionamento:** 24 horas, sem regra de horário.
- **Liberação manual:** o operador não libera acima do limite. Só o administrador aumenta as vagas no contrato, e isso pede a senha dele de novo e fica na auditoria. É o que garante a 6.2.
- **Contrato vencido ou suspenso:** bloqueia a entrada de todos os veículos do cliente.
- **Veículo:** pertence a um cliente só. A vaga é contada, não é uma vaga física pintada no pátio. A "vaga no quadro" é a posição no desenho do cliente, guardada no banco para o carro não mudar de lugar no desenho.
- **Vagas do contrato:** não podem ser reduzidas abaixo das que estão em uso.
- **Desenho com muitas vagas:** fileiras de 10 no desktop e de 5 no celular.
- **Saída:** nunca é bloqueada.
- **Senha esquecida:** o administrador redefine com uma senha temporária, porque E-mails está em NÃO (EXCEÇÃO 3).
- **Histórico:** guardado por 12 meses e depois apagado [REVISAR COM O CLIENTE].
- **Dados de exemplo:** só no perfil `dev`, com nomes claramente fictícios.

### Perguntas

Nenhuma. As 3 da versão anterior foram respondidas.

### Confirmações (uma linha)

As 3 palavras, a guarda do histórico por 12 meses e as metas de "menos de 1 segundo" e "menos de 2 minutos" [REVISAR COM O CLIENTE].

### Fotos e vídeos para pedir ao cliente

Nada a pedir. O sistema não usa fotos (a identidade vem da tipografia, da pauta e das duas canetas), o carro e a garagem são desenhados em SVG próprio, e o logo é o nome escrito.

### Fora do degrau

Nada. O escopo cabe no degrau de R$ 15.000 ("uma função de sistema sob medida, testes automáticos e backup"), que está abaixo da régua de R$ 20.000. O que o degrau de R$ 20.000 soma (loja, pagamento e e-mails) está em NÃO. A manobra é 1 efeito premium, e o degrau permite até 3 (R$ 15.000) ou 4 (R$ 20.000).

### Exceções (para você aprovar)

- **EXCEÇÃO 1 — sistema sem parte pública.** A tabela 2.5 pede, para Sistema, uma parte pública com WhatsApp, redes, ferramenta de perguntas, "Nossa história", rolagem PicPay, botão flutuante e mapa.
  - **Motivo:** quem usa o sistema é a equipe da portaria, não o público. O pedido é um sistema de controle (6.1, bloco 8), e um site institucional junto deixaria o trabalho maior sem servir à regra principal (Base, seção 1: menor complexidade necessária).
  - **Se você preferir com parte pública:** uma página "Início" com "Nossa história" e "Contato" soma cerca de 3 dias e precisa do nome, do endereço, do WhatsApp e das redes.
- **EXCEÇÃO 2 — "(pelo sistema)" no fim das mensagens do WhatsApp**, no lugar de "(pelo site)". A regra serve para quem recebe saber de onde veio a conversa, e aqui ela vem do sistema da portaria.
- **EXCEÇÃO 3 — recuperação de senha sem e-mail.** O Módulo A pede recuperação por link, mas E-mails está em NÃO. Por isso o administrador redefine a senha com uma senha temporária, que a pessoa troca no primeiro login. A recuperação por e-mail fica como P2.

### Regras de conselho

Não se aplica. Estacionamento não é profissão regulamentada por conselho (Receitas, R.2).

### Receita do segmento (não está na lista R.1, montada aqui)

**Estacionamento com vagas contratadas (sistema de portaria):**
- **Seções essenciais:** portaria, pátio agora, clientes, contratos, veículos, histórico.
- **O que vende:** decisão em um passo, regra que não estoura, cadastro rápido, histórico com bloqueios.
- **Clichê a quebrar:** "P" azul, zebrado amarelo e preto, ícone de carro, dashboard escuro com neon.
- **Cuidados:** LGPD. Nome, CPF, telefone e placa ligada a uma pessoa são dados pessoais. O sistema guarda só o necessário para o contrato e para a portaria, diz para que serve na tela Privacidade, mostra a cada papel só o que ele precisa e permite anonimizar o cliente que sair. Não há dado sensível (saúde, biometria, crianças).

### Fontes (consultadas em 25/11/2026)

- [Oracle — Java SE Support Roadmap](https://www.oracle.com/java/technologies/java-se-support-roadmap.html): o Java 25 é a versão LTS mais recente.
- [Maven Central — versões do Spring Boot](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot/maven-metadata.xml): a 4.2.0 é a versão estável mais recente, publicada em 19/11/2026.
- [Google Fonts — Atkinson Hyperlegible Mono](https://fonts.google.com/specimen/Atkinson+Hyperlegible+Mono) e [repositório da licença OFL 1.1](https://github.com/googlefonts/atkinson-hyperlegible-next-mono).
- [Atkinson Hyperlegible Next — repositório e licença OFL 1.1](https://github.com/googlefonts/atkinson-hyperlegible-next).
- [Lei 13.709/2018 (LGPD) — Planalto](https://www.planalto.gov.br/ccivil_03/_ato2015-2018/2018/lei/l13709.htm).

### Conferência do DNA (itens copiados da Base, seção 2.3)

- [x] Cada decisão importante aponta para uma resposta do cliente. — Assinatura: observação da 2.1 e o seu pedido extra. Conceito: bloco 8. Portaria: 6.1. Cadastro: 6.2. Tom: 4.3.
- [x] Tom tirado da 4.3, com os pontos de humor listados (ou "sem humor"). — Sem humor.
- [x] Tabela de contraste completa: texto sobre cards, sobre foto, botões contra o fundo e foco do teclado, todos calculados. — Calculado pela fórmula WCAG; sem texto sobre foto porque não há fotos.
- [x] Fontes com origem e licença; tamanhos reais do hero e dos títulos. — Google Fonts, OFL 1.1; sem hero, h1 de 32/26 px.
- [x] Canais de contato: WhatsApp com mensagem pronta em cada ponto e todas as redes que o cliente informou. — 3 pontos com mensagem; nenhuma rede informada; parte pública pela EXCEÇÃO 1.
- [x] Ordem de corte, exceções marcadas como EXCEÇÃO e no máximo 5 perguntas. — Nenhuma pergunta pendente, 3 exceções e 1 conflito registrado (o contador).
- [x] Cada item que o cliente marcou na pergunta 8 do questionário tem lugar no DNA ou está nas descartadas, com o motivo. — Painel, área com login, controle com bloqueio e histórico: todos P0.
- [n/a] Contas de custo com todos os itens que cobram e todos os limites do plano grátis (ex.: banda, visitas, deploys e tempo de processamento por pedido), tiradas da página oficial de preços de cada serviço. — Sem serviço pago; tudo local. Se for publicar, o plano com custos vem antes.
- [x] Explicações de normas com as minhas palavras e a lista "Fontes" com links no fim. — LGPD explicada com as minhas palavras.
- [x] Nenhuma afirmação, regra de funcionamento ou número que o cliente não deu, sem marca de revisão. — Regras deduzidas estão nas suposições; metas e guarda do histórico marcadas.
- [x] Textos para o cliente sem colchete vazio, com datas reais, e revisados para erro de digitação (espaços, pontuação, acentos).
- [x] Tipo de site dito, com todos os obrigatórios da tabela 2.5 no DNA, e as regras do conselho conferidas quando houver. — Sistema; parte pública na EXCEÇÃO 1; sem conselho.
- [x] Escopo conferido com o degrau de investimento: o que passa dele está na lista "Fora do degrau". — Nada fora.
- [x] Lista "Fotos e vídeos para pedir ao cliente" completa (ou "nada a pedir", com o motivo), com como fazer cada item e a mensagem pronta para o cliente. — Nada a pedir, com o motivo.
- [x] Conta de cliente: o caminho (a ou b) dito no item 9, com o motivo. Com painel: área da equipe com as duas senhas e o link discreto no rodapé. — Caminho b; duas senhas; link do rodapé não se aplica porque o sistema inteiro é a área da equipe.

**O que torna este sistema único, em uma linha:** a garagem desenhada é a do próprio cliente, com os dados do banco, e o 6º carro dá ré diante da cancela fechada porque o servidor disse não. Trocar o logo não funcionaria.

---

## Texto para apresentar o DNA (ao professor)

**Versão curta:**

"Professor, segue a proposta do sistema de controle de estacionamento. A ideia central: o operador digita a placa e o sistema responde na hora se libera ou bloqueia, e diz por quê. A regra das vagas contratadas (5 vagas, o 6º carro espera) é garantida pelo próprio banco de dados, com um teste automático que tenta entrar com dois carros ao mesmo tempo para provar que o limite nunca estoura. Separei contrato, estadia e tentativa de acesso em tabelas próprias, para o histórico mostrar também os bloqueios. Na tela da portaria, uma animação desenhada mostra a garagem do cliente: o carro entra e estaciona, ou para na cancela e dá ré, conforme a decisão que o servidor tomou. Com a aprovação desta proposta, o protótipo das telas fica pronto em até 7 dias."

**Versão completa:**

1. **O que entendemos:** um estacionamento de vagas contratadas, por empresas e por pessoas físicas, em que hoje o porteiro confere na mão se ainda há vaga.
2. **Para quem o sistema fala:** o operador da portaria, que precisa de uma resposta rápida com o carro esperando, e o administrador, que cadastra clientes, veículos e vagas.
3. **A promessa:** "A placa entra, o sistema responde: libera ou bloqueia, e diz por quê."
4. **O jeito de parecer:** o caderno da portaria passado a limpo. Papel claro, linhas finas, a caneta azul para o que entra e a vermelha para o que não pode entrar. A letra foi escolhida para não confundir 0 com O e 1 com I nas placas.
5. **A ideia própria:** o "Quadro de vagas", a garagem de cada cliente desenhada com uma vaga por vaga contratada. Na portaria, o carro entra e estaciona na vaga reservada, ou para diante da cancela fechada e dá ré, e a mensagem com o motivo aparece na hora, sem esperar o desenho.
6. **O que vai ter:** portaria, painel, clientes, contratos, veículos, histórico com bloqueios, equipe com papéis (administrador e operador), auditoria e privacidade.
7. **O modelo de dados:** 8 tabelas criadas por migration, com o limite de vagas, a placa única e "um carro dentro por vez" garantidos por restrições e transação no banco.
8. **Próximo passo:** com a aprovação até 26/11/2026, o protótipo das telas fica pronto até 03/12/2026, para uma nova aprovação antes da construção.
