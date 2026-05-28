-- =========================================
-- T_BASES (NOVA ENTIDADE RAIZ)
-- =========================================
CREATE TABLE t_bases (
                         id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         nome VARCHAR2(100) NOT NULL
);

-- =========================================
-- T_USUARIOS
-- =========================================
CREATE TABLE t_usuarios (
                            id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            nome VARCHAR2(100) NOT NULL,
                            email VARCHAR2(150) UNIQUE NOT NULL,
                            senha VARCHAR2(100) NOT NULL,
                            base_id NUMBER NOT NULL,

                            CONSTRAINT fk_usuario_base
                                FOREIGN KEY (base_id)
                                    REFERENCES t_bases(id)
);

-- =========================================
-- T_RECURSOS (TABELA PAI)
-- =========================================
CREATE TABLE t_recursos (
                            id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            base_id NUMBER NOT NULL,

                            nome VARCHAR2(100) NOT NULL,
                            categoria VARCHAR2(50) NOT NULL,
                            quantidade NUMBER NOT NULL,
                            minimo NUMBER NOT NULL,
                            capacidade_maxima NUMBER NOT NULL,

                            critico NUMBER(1) DEFAULT 0,
                            status VARCHAR2(30),
                            nivel VARCHAR2(30),
                            ultima_atualizacao TIMESTAMP,

                            CONSTRAINT fk_recurso_base
                                FOREIGN KEY (base_id)
                                    REFERENCES t_bases(id)
);

-- =========================================
-- T_RECURSO_ENERGIA (HERANÇA)
-- =========================================
CREATE TABLE t_recurso_energia (
                                   id NUMBER PRIMARY KEY,
                                   tipo_energia VARCHAR2(50),

                                   CONSTRAINT fk_energia_recurso
                                       FOREIGN KEY (id)
                                           REFERENCES t_recursos(id)
);

-- =========================================
-- T_RECURSO_MEDICAMENTO (HERANÇA)
-- =========================================
CREATE TABLE t_recurso_medicamento (
                                       id NUMBER PRIMARY KEY,
                                       validade DATE,

                                       CONSTRAINT fk_medicamento_recurso
                                           FOREIGN KEY (id)
                                               REFERENCES t_recursos(id)
);

-- =========================================
-- T_MOVIMENTACOES
-- =========================================
CREATE TABLE t_movimentacoes (
                                 id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                 base_id NUMBER NOT NULL,
                                 usuario_id NUMBER NOT NULL,
                                 recurso_id NUMBER NOT NULL,

                                 tipo_movimentacao VARCHAR2(30) NOT NULL,
                                 quantidade NUMBER NOT NULL,
                                 descricao VARCHAR2(255),
                                 data_movimentacao TIMESTAMP,

                                 CONSTRAINT fk_mov_base
                                     FOREIGN KEY (base_id)
                                         REFERENCES t_bases(id),

                                 CONSTRAINT fk_mov_usuario
                                     FOREIGN KEY (usuario_id)
                                         REFERENCES t_usuarios(id),

                                 CONSTRAINT fk_mov_recurso
                                     FOREIGN KEY (recurso_id)
                                         REFERENCES t_recursos(id)
);

-- =========================================
-- T_ALERTAS
-- =========================================
CREATE TABLE t_alertas (
                           id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                           base_id NUMBER NOT NULL,
                           recurso_id NUMBER NOT NULL,

                           mensagem VARCHAR2(255),
                           nivel VARCHAR2(30),
                           resolvido NUMBER(1) DEFAULT 0,
                           data_alerta TIMESTAMP,

                           CONSTRAINT fk_alerta_base
                               FOREIGN KEY (base_id)
                                   REFERENCES t_bases(id),

                           CONSTRAINT fk_alerta_recurso
                               FOREIGN KEY (recurso_id)
                                   REFERENCES t_recursos(id)
);