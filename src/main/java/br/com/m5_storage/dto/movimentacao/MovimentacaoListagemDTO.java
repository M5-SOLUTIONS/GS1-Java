package br.com.m5_storage.dto.movimentacao;

import br.com.m5_storage.entity.movimentacao.TipoMovimentacao;
import java.time.LocalDateTime;

// Regra 19: histórico permanente — inclui setor
public record MovimentacaoListagemDTO(
        Long id,
        Long recursoId,
        String recursoNome,
        Long setorId,
        String setorNome,
        Long usuarioId,
        String usuarioNome,
        TipoMovimentacao tipoMovimentacao,
        Double quantidade,
        String descricao,
        LocalDateTime dataMovimentacao
) {}