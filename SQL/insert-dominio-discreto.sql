SET AUTOCOMMIT = 0;
START TRANSACTION;


INSERT INTO SITUACAO_CONTA (nome) values ('ATIVA');
INSERT INTO SITUACAO_CONTA (nome) values ('BLOQUEADA');
INSERT INTO SITUACAO_CONTA (nome) values ('CANCELADA');


INSERT INTO TIPO_ACAO_TRANSACAO (nome) values ('APORTE');
INSERT INTO TIPO_ACAO_TRANSACAO (nome) values ('TRANSFERENCIA');
INSERT INTO TIPO_ACAO_TRANSACAO (nome) values ('ESTORNO');


INSERT INTO TIPO_CONTA (nome) values ('MATRIZ');
INSERT INTO TIPO_CONTA (nome) values ('FILIAL');

INSERT INTO TIPO_PESSOA (nome) values ('FISICA');
INSERT INTO TIPO_PESSOA (nome) values ('JURIDICA');


INSERT INTO TIPO_STATUS_TRANSACAO (nome) values ('SUCESSO');
INSERT INTO TIPO_STATUS_TRANSACAO (nome) values ('FALHA');


