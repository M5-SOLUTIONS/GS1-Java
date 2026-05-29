package br.com.m5_storage.dto.alerta;

import java.time.LocalDateTime;

// Regra 6: alerta associado a recurso, setor e base
public record AlertaListagemDTO(
        Long id,
        Long recursoId,
        String recursoNome,
        Long setorId,
        String setorNome,
        Long baseId,
        String mensagem,
        String nivel,
        Boolean resolvido,
        LocalDateTime dataAlerta
) {}