package br.com.m5_storage.dto.movimentacao;

import br.com.m5_storage.entity.movimentacao.TipoMovimentacao;

import java.time.LocalDateTime;

public record MovimentacaoListagemDTO(
        Long id,
        Long recursoId,
        String recursoNome,
        Long usuarioId,
        String usuarioNome,
        Long baseId,
        String baseNome,
        TipoMovimentacao tipoMovimentacao,
        Double quantidade,
        String descricao,
        LocalDateTime dataMovimentacao
) {}