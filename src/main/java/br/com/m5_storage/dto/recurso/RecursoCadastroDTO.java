package br.com.m5_storage.dto.recurso;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record RecursoCadastroDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100)
        String nome,

        @NotBlank(message = "Categoria é obrigatória")
        @Size(max = 50)
        String categoria,

        @NotNull(message = "Quantidade é obrigatória")
        @PositiveOrZero(message = "Quantidade não pode ser negativa")
        Double quantidade,

        @NotNull(message = "Mínimo é obrigatório")
        @PositiveOrZero(message = "Mínimo não pode ser negativo")
        Double minimo,

        Boolean critico,

        @NotNull(message = "Base é obrigatória")
        Long baseId
) {}