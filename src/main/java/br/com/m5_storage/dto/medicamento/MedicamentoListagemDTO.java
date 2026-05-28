package br.com.m5_storage.dto.medicamento;

import br.com.m5_storage.entity.recurso.StatusRecurso;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MedicamentoListagemDTO(
        Long id,
        String nome,
        String categoria,
        Double quantidade,
        Double minimo,
        Boolean critico,
        StatusRecurso status,
        Long baseId,
        String baseNome,
        LocalDate validade,
        LocalDateTime ultimaAtualizacao
) {}