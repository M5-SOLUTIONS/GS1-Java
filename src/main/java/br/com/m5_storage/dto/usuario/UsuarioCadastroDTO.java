package br.com.m5_storage.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioCadastroDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 150, message = "Email deve ter no máximo 150 caracteres")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(max = 100, message = "Senha deve ter no máximo 100 caracteres")
        String senha
) {}