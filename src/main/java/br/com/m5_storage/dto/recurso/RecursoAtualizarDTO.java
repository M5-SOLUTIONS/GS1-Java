package br.com.m5_storage.dto.recurso;

import jakarta.validation.constraints.*;

public record RecursoAtualizarDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String nome,

        @NotBlank(message = "Categoria é obrigatória")
        @Size(max = 50)
        String categoria,

        @NotNull(message = "Quantidade é obrigatória")
        @Positive(message = "Quantidade deve ser maior que zero")
        Double quantidade,

        @NotNull(message = "Mínimo é obrigatório")
        @Positive(message = "Mínimo deve ser maior que zero")
        Double minimo,

        Boolean critico
) {}