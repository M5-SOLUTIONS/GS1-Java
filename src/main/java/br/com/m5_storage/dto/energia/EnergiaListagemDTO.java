package br.com.m5_storage.dto.energia;

import br.com.m5_storage.entity.recurso.StatusRecurso;

import java.time.LocalDateTime;

// Regra 12: inclui tipoEnergia e porcentagem calculada
public record EnergiaListagemDTO(
        Long id,
        String nome,
        String categoria,
        Double quantidade,
        Double minimo,
        Boolean critico,
        StatusRecurso status,
        String tipoEnergia,
        Double porcentagem,       // calculado no service — não persiste
        LocalDateTime ultimaAtualizacao
) {}