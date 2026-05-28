-- =========================================
-- T_BASES
-- =========================================
CREATE TABLE t_bases (
                         id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         nome VARCHAR2(100) NOT NULL
);

-- =========================================
-- T_SETOR (NOVO)
-- =========================================
CREATE TABLE t_setores (
                           id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                           base_id NUMBER NOT NULL,

                           nome VARCHAR2(100) NOT NULL,
                           descricao VARCHAR2(255),

    -- Embeddable (exemplo de uso lógico)
    -- aqui você NÃO cria tabela separada, mas representa estrutura reutilizável no Java

                           CONSTRAINT fk_setor_base
                               FOREIGN KEY (base_id)
                                   REFERENCES t_bases(id)
);

-- =========================================
-- T_USUARIOS
-- =========================================
CREATE TABLE t_usuarios (
                            id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                            base_id NUMBER NOT NULL,

                            nome VARCHAR2(100) NOT NULL,
                            email VARCHAR2(150) UNIQUE NOT NULL,
                            senha VARCHAR2(100) NOT NULL,

                            CONSTRAINT fk_usuario_base
                                FOREIGN KEY (base_id)
                                    REFERENCES t_bases(id)
);

-- =========================================
-- T_RECURSOS
-- =========================================
CREATE TABLE t_recursos (
                            id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                            setor_id NUMBER NOT NULL,

                            nome VARCHAR2(100) NOT NULL,
                            categoria VARCHAR2(50) NOT NULL,

                            quantidade NUMBER NOT NULL,
                            minimo NUMBER NOT NULL,
                            capacidade_maxima NUMBER NOT NULL,

                            critico NUMBER(1) DEFAULT 0,
                            status VARCHAR2(30),
                            ultima_atualizacao TIMESTAMP,

                            CONSTRAINT fk_recurso_setor
                                FOREIGN KEY (setor_id)
                                    REFERENCES t_setores(id)
);

-- =========================================
-- T_MOVIMENTACOES
-- =========================================
CREATE TABLE t_movimentacoes (
                                 id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

                                 usuario_id NUMBER NOT NULL,
                                 recurso_id NUMBER NOT NULL,

                                 tipo_movimentacao VARCHAR2(30) NOT NULL,
                                 quantidade NUMBER NOT NULL,
                                 descricao VARCHAR2(255),
                                 data_movimentacao TIMESTAMP NOT NULL,

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

                           recurso_id NUMBER NOT NULL,

                           mensagem VARCHAR2(255),
                           nivel VARCHAR2(30),
                           resolvido NUMBER(1) DEFAULT 0,
                           data_alerta TIMESTAMP,

                           CONSTRAINT fk_alerta_recurso
                               FOREIGN KEY (recurso_id)
                                   REFERENCES t_recursos(id)
);