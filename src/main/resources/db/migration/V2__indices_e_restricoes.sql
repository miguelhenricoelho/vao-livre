-- E-mail da equipe único, sem diferenciar maiúsculas.
CREATE UNIQUE INDEX usuario_email_unico ON usuario (lower(email));

-- Um contrato vigente (ativo ou suspenso) por cliente.
CREATE UNIQUE INDEX contrato_um_vigente_por_cliente ON contrato (cliente_id)
    WHERE situacao IN ('ATIVO', 'SUSPENSO');

-- Placa única entre os veículos ativos (um carro vendido pode ser cadastrado para outro cliente).
CREATE UNIQUE INDEX veiculo_placa_ativa_unica ON veiculo (placa) WHERE ativo;
CREATE INDEX veiculo_por_cliente ON veiculo (cliente_id);

-- Um carro dentro por vez.
CREATE UNIQUE INDEX estadia_aberta_por_veiculo ON estadia (veiculo_id) WHERE saida_em IS NULL;
-- Duas estadias abertas nunca dividem a mesma vaga do quadro do mesmo contrato.
CREATE UNIQUE INDEX estadia_aberta_por_vaga ON estadia (contrato_id, vaga_no_quadro) WHERE saida_em IS NULL;
CREATE INDEX estadia_abertas_por_contrato ON estadia (contrato_id) WHERE saida_em IS NULL;

-- Busca de clientes e histórico.
CREATE INDEX cliente_nome_busca ON cliente (lower(nome));
CREATE INDEX ocorrencia_por_data ON ocorrencia_portaria (ocorrido_em DESC);
CREATE INDEX ocorrencia_por_cliente ON ocorrencia_portaria (cliente_id, ocorrido_em DESC);
CREATE INDEX ocorrencia_por_placa ON ocorrencia_portaria (placa_informada, ocorrido_em DESC);
CREATE INDEX auditoria_por_data ON auditoria (ocorrido_em DESC);
