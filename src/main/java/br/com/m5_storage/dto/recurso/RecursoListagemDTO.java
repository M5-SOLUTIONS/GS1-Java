package br.com.m5_storage.dto.recurso;

import br.com.m5_storage.entity.recurso.StatusRecurso;
import java.time.LocalDateTime;

// Regra 14/17/18: status, capacidadeMaxima e setor na resposta
public record RecursoListagemDTO(
        Long id,
        String nome,
        String categoria,
        Double quantidade,
        Double minimo,
        Double capacidadeMaxima,
        Boolean critico,
        StatusRecurso status,
        LocalDateTime ultimaAtualizacao,
        Long setorId,
        String setorNome,
        Long baseId
) {}