package br.com.m5_storage.repository;

import br.com.m5_storage.entity.movimentacao.Movimentacao;
import br.com.m5_storage.entity.movimentacao.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    // Regra 19: verificar se recurso possui movimentações antes de remover
    boolean existsByRecursoId(Long recursoId);

    // Regra 20: histórico por recurso
    List<Movimentacao> findByRecursoIdOrderByDataMovimentacaoDesc(Long recursoId);

    // Regra 10: histórico por usuário
    List<Movimentacao> findByUsuarioIdOrderByDataMovimentacaoDesc(Long usuarioId);

    // Filtro por tipo
    List<Movimentacao> findByRecursoIdAndTipoMovimentacao(Long recursoId, TipoMovimentacao tipo);
}
