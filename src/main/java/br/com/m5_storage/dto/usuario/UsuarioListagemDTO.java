package br.com.m5_storage.dto.usuario;

// Senha nunca exposta; inclui tipoUsuario para que o cliente
// saiba o papel do usuário sem precisar de outro endpoint.
public record UsuarioListagemDTO(
        Long id,
        String nome,
        String email,
        Long baseId,
        TipoUsuario tipoUsuario
) {}