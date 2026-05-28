package br.com.m5_storage.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            IdNaoEncontradoException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso não encontrado",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {

                    String fieldName =
                            ((FieldError) error).getField();

                    String message =
                            error.getDefaultMessage();

                    errors.put(fieldName, message);
                });

        ValidationErrorResponse response =
                new ValidationErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro de validação",
                        request.getRequestURI(),
                        LocalDateTime.now(),
                        errors
                );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Erro de integridade no banco de dados",
                "Já existe um registro com esses dados ou existe relacionamento inválido",
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Violação de restrição",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "Tipo de conteúdo não suportado",
                "Utilize Content-Type: application/json",
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(error);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                "Método HTTP não permitido",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(error);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<ErrorResponse> handleMissingPath(
            MissingPathVariableException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Parâmetro da URL ausente",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro interno do servidor",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    public static class ErrorResponse {

        private int status;
        private String error;
        private String message;
        private String path;
        private LocalDateTime timestamp;

        public ErrorResponse(int status,
                             String error,
                             String message,
                             String path,
                             LocalDateTime timestamp) {

            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public String getPath() {
            return path;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    public static class ValidationErrorResponse {

        private int status;
        private String error;
        private String path;
        private LocalDateTime timestamp;
        private Map<String, String> fields;

        public ValidationErrorResponse(int status,
                                       String error,
                                       String path,
                                       LocalDateTime timestamp,
                                       Map<String, String> fields) {

            this.status = status;
            this.error = error;
            this.path = path;
            this.timestamp = timestamp;
            this.fields = fields;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getPath() {
            return path;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public Map<String, String> getFields() {
            return fields;
        }
    }
}
