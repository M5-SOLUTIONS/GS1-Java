package br.com.m5_storage.dto.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BaseCadastroDTO(

        @NotBlank(message = "O nome da base é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome

) {
}