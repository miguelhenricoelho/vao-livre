-- Vão Livre: estrutura do banco.
-- Regras que limitam algo (vagas, um carro dentro por vez) são garantidas aqui, no banco.

CREATE TABLE usuario (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nome               VARCHAR(120) NOT NULL,
    email              VARCHAR(160) NOT NULL,
    senha_hash         VARCHAR(100) NOT NULL,
    papel              VARCHAR(20)  NOT NULL,
    ativo              BOOLEAN      NOT NULL DEFAULT TRUE,
    deve_trocar_senha  BOOLEAN      NOT NULL DEFAULT FALSE,
    criado_em          TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT usuario_papel_valido CHECK (papel IN ('ADMIN', 'OPERADOR'))
);

CREATE TABLE configuracao (
    id                 SMALLINT     PRIMARY KEY,
    senha_area_hash    VARCHAR(100) NOT NULL,
    versao_senha_area  INTEGER      NOT NULL DEFAULT 1,
    whatsapp_suporte   VARCHAR(13),
    atualizado_em      TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT configuracao_linha_unica CHECK (id = 1),
    CONSTRAINT configuracao_whatsapp_formato CHECK (whatsapp_suporte IS NULL OR whatsapp_suporte ~ '^[0-9]{12,13}$')
);

CREATE TABLE cliente (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo          VARCHAR(2)   NOT NULL,
    nome          VARCHAR(160) NOT NULL,
    documento     VARCHAR(14)  NOT NULL,
    responsavel   VARCHAR(120),
    telefone      VARCHAR(13),
    email         VARCHAR(160),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMPTZ  NOT NULL DEFAULT now(),
    atualizado_em TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT cliente_tipo_valido CHECK (tipo IN ('PF', 'PJ')),
    CONSTRAINT cliente_documento_unico UNIQUE (documento),
    CONSTRAINT cliente_documento_formato CHECK (
        (tipo = 'PF' AND documento ~ '^[0-9]{11}$') OR (tipo = 'PJ' AND documento ~ '^[0-9]{14}$')
    ),
    CONSTRAINT cliente_telefone_formato CHECK (telefone IS NULL OR telefone ~ '^[0-9]{12,13}$')
);

CREATE TABLE contrato (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id         BIGINT      NOT NULL REFERENCES cliente (id),
    vagas_contratadas  INTEGER     NOT NULL,
    inicio             DATE        NOT NULL,
    fim                DATE,
    situacao           VARCHAR(12) NOT NULL,
    criado_em          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT contrato_vagas_positivas CHECK (vagas_contratadas BETWEEN 1 AND 500),
    CONSTRAINT contrato_periodo_valido CHECK (fim IS NULL OR fim >= inicio),
    CONSTRAINT contrato_situacao_valida CHECK (situacao IN ('ATIVO', 'SUSPENSO', 'ENCERRADO'))
);

CREATE TABLE veiculo (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    cliente_id  BIGINT      NOT NULL REFERENCES cliente (id),
    placa       VARCHAR(7)  NOT NULL,
    modelo      VARCHAR(80),
    cor         VARCHAR(30),
    ativo       BOOLEAN     NOT NULL DEFAULT TRUE,
    criado_em   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT veiculo_placa_formato CHECK (placa ~ '^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$')
);

-- Cada estadia aberta ocupa uma vaga numerada do quadro do contrato (1 até o número de vagas).
CREATE TABLE estadia (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    veiculo_id      BIGINT      NOT NULL REFERENCES veiculo (id),
    contrato_id     BIGINT      NOT NULL REFERENCES contrato (id),
    vaga_no_quadro  INTEGER     NOT NULL,
    entrada_em      TIMESTAMPTZ NOT NULL,
    saida_em        TIMESTAMPTZ,
    registrada_por  BIGINT      NOT NULL REFERENCES usuario (id),
    encerrada_por   BIGINT      REFERENCES usuario (id),
    CONSTRAINT estadia_vaga_positiva CHECK (vaga_no_quadro >= 1),
    CONSTRAINT estadia_periodo_valido CHECK (saida_em IS NULL OR saida_em >= entrada_em),
    CONSTRAINT estadia_encerramento_completo CHECK ((saida_em IS NULL) = (encerrada_por IS NULL))
);

-- Toda tentativa na portaria, liberada ou bloqueada. É o histórico de acessos.
CREATE TABLE ocorrencia_portaria (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    placa_informada  VARCHAR(7)  NOT NULL,
    veiculo_id       BIGINT      REFERENCES veiculo (id),
    cliente_id       BIGINT      REFERENCES cliente (id),
    estadia_id       BIGINT      REFERENCES estadia (id),
    tipo             VARCHAR(7)  NOT NULL,
    resultado        VARCHAR(9)  NOT NULL,
    motivo           VARCHAR(24),
    operador_id      BIGINT      NOT NULL REFERENCES usuario (id),
    ocorrido_em      TIMESTAMPTZ NOT NULL,
    CONSTRAINT ocorrencia_tipo_valido CHECK (tipo IN ('ENTRADA', 'SAIDA')),
    CONSTRAINT ocorrencia_resultado_valido CHECK (resultado IN ('LIBERADO', 'BLOQUEADO')),
    CONSTRAINT ocorrencia_motivo_valido CHECK (motivo IS NULL OR motivo IN (
        'VAGAS_ESGOTADAS', 'CONTRATO_SUSPENSO', 'CONTRATO_VENCIDO', 'CONTRATO_FUTURO', 'SEM_CONTRATO',
        'CLIENTE_INATIVO', 'VEICULO_INATIVO', 'PLACA_NAO_CADASTRADA', 'JA_NO_PATIO', 'FORA_DO_PATIO'
    )),
    CONSTRAINT ocorrencia_motivo_quando_bloqueado CHECK ((resultado = 'BLOQUEADO') = (motivo IS NOT NULL))
);

-- Ações administrativas relevantes. O resumo nunca leva dado pessoal.
CREATE TABLE auditoria (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    usuario_id   BIGINT       NOT NULL REFERENCES usuario (id),
    acao         VARCHAR(40)  NOT NULL,
    entidade     VARCHAR(40)  NOT NULL,
    entidade_id  BIGINT,
    resumo       VARCHAR(300) NOT NULL,
    ocorrido_em  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
