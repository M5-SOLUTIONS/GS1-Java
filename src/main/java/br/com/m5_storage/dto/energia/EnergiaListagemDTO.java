package br.com.m5_storage.dto.energia;

import br.com.m5_storage.entity.recurso.StatusRecurso;

import java.time.LocalDateTime;

public record EnergiaListagemDTO(

        Long id,
        String nome,
        String categoria,
        Double quantidade,
        Double minimo,
        Boolean critico,
        StatusRecurso status,
        String tipoEnergia,
        Double porcentagem,
        Long baseId,
        String baseNome,
        LocalDateTime ultimaAtualizacao
) {}