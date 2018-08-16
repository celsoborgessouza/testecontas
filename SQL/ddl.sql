CREATE TABLE `aporte` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `descricao` varchar(50) NOT NULL,
  `codigo` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_CODIGO_APORTE` (`codigo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `conta` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nome` varchar(255) NOT NULL,
  `data_criacao` datetime NOT NULL,
  `saldo` decimal(10,2) NOT NULL,
  `nivel` int(11) NOT NULL,
  `id_conta_pai` int(11) unsigned DEFAULT NULL,
  `id_tipo_conta` int(11) unsigned NOT NULL,
  `id_situacao_conta` int(11) unsigned NOT NULL,
  `id_pessoa` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `conta_un` (`nome`),
  KEY `conta_tipo_conta_fk` (`id_tipo_conta`),
  KEY `conta_situacao_conta_fk` (`id_situacao_conta`),
  KEY `conta_pessoa_fk` (`id_pessoa`),
  CONSTRAINT `conta_pessoa_fk` FOREIGN KEY (`id_pessoa`) REFERENCES `pessoa` (`id`),
  CONSTRAINT `conta_situacao_conta_fk` FOREIGN KEY (`id_situacao_conta`) REFERENCES `situacao_conta` (`id`),
  CONSTRAINT `conta_tipo_conta_fk` FOREIGN KEY (`id_tipo_conta`) REFERENCES `tipo_conta` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pessoa` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `id_tipo_pessoa` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `pessoa_tipo_pessoa_fk` (`id_tipo_pessoa`),
  CONSTRAINT `pessoa_tipo_pessoa_fk` FOREIGN KEY (`id_tipo_pessoa`) REFERENCES `tipo_pessoa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pessoa_fisica` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cpf` varchar(11) NOT NULL,
  `nome_completo` varchar(255) NOT NULL,
  `data_nascimento` date NOT NULL,
  `id_pessoa` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pessoa_fisica_un` (`cpf`),
  KEY `pessoa_fisica_pessoa_fk` (`id_pessoa`),
  CONSTRAINT `pessoa_fisica_pessoa_fk` FOREIGN KEY (`id_pessoa`) REFERENCES `pessoa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `pessoa_juridica` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `cnpj` varchar(14) NOT NULL,
  `razao_social` varchar(255) NOT NULL,
  `nome_fantasia` varchar(255) NOT NULL,
  `id_pessoa` int(11) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `pessoa_juridica_un` (`cnpj`),
  KEY `pessoa_juridica_pessoa_fk` (`id_pessoa`),
  CONSTRAINT `pessoa_juridica_pessoa_fk` FOREIGN KEY (`id_pessoa`) REFERENCES `pessoa` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `situacao_conta` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nome` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_SITUACAO_CONTA` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tipo_acao_transacao` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_TIPO_ACAO_TRANSACAO` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tipo_conta` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nome` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_TIPO_CONTA` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tipo_pessoa` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nome` varchar(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_TIPO_PESSOA` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='DomÃƒÂ­nio discreto';

CREATE TABLE `tipo_status_transacao` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `nome` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UNIQUE_STATUS_TRANSACAO` (`nome`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `transacao` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `data_transacao` date NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `descricao_status_transacao` varchar(255) NOT NULL,
  `id_tipo_status_transacao` int(11) unsigned NOT NULL,
  `id_tipo_acao_transacao` int(11) unsigned NOT NULL,
  `id_aporte` int(11) unsigned DEFAULT NULL,
  `id_conta_origem` int(11) unsigned DEFAULT NULL,
  `id_conta_destino` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `transacao_aporte_fk` (`id_aporte`),
  KEY `transacao_tipo_status_transacao_fk` (`id_tipo_status_transacao`),
  KEY `transacao_tipo_acao_transacao_fk` (`id_tipo_acao_transacao`),
  KEY `transacao_conta_fk` (`id_conta_origem`),
  KEY `transacao_conta_fk_1` (`id_conta_destino`),
  CONSTRAINT `transacao_aporte_fk` FOREIGN KEY (`id_aporte`) REFERENCES `aporte` (`id`),
  CONSTRAINT `transacao_conta_fk` FOREIGN KEY (`id_conta_origem`) REFERENCES `conta` (`id`),
  CONSTRAINT `transacao_conta_fk_1` FOREIGN KEY (`id_conta_destino`) REFERENCES `conta` (`id`),
  CONSTRAINT `transacao_tipo_acao_transacao_fk` FOREIGN KEY (`id_tipo_acao_transacao`) REFERENCES `tipo_acao_transacao` (`id`),
  CONSTRAINT `transacao_tipo_status_transacao_fk` FOREIGN KEY (`id_tipo_status_transacao`) REFERENCES `tipo_status_transacao` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
