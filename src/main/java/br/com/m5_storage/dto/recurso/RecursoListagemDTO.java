package br.com.m5_storage.dto.recurso;

import br.com.m5_storage.entity.recurso.StatusRecurso;

import java.time.LocalDateTime;

public record RecursoListagemDTO(
        Long id,
        String nome,
        String categoria,
        Double quantidade,
        Double minimo,
        Boolean critico,
        StatusRecurso status,
        LocalDateTime ultimaAtualizacao
) {}