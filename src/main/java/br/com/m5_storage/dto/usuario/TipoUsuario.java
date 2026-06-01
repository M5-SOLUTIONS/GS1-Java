package br.com.m5_storage.dto.usuario;

/**
 * Regra 2: espelha o discriminator value da SINGLE_TABLE inheritance.
 * Usado nos DTOs para entrada/saída; a entidade usa instanceof para a lógica.
 */
public enum TipoUsuario {
    VIEWER,
    OPERATOR
}