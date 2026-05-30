package br.com.m5_storage.exception;

/**
 * Regra 2: lançada quando um Viewer tenta executar
 * uma operação exclusiva de Operator (escrita).
 */
public class OperadorNecessarioException extends RuntimeException {

    public OperadorNecessarioException() {
        super("Acesso negado: apenas Operators podem executar esta operação.");
    }

    public OperadorNecessarioException(String message) {
        super(message);
    }
}