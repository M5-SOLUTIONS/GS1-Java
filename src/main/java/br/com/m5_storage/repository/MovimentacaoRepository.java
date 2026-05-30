package br.com.m5_storage.repository;

import br.com.m5_storage.entity.movimentacao.Movimentacao;
import br.com.m5_storage.entity.movimentacao.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {

    // Regra 21: verificar movimentações antes de deletar recurso
    boolean existsByRecursoId(Long recursoId);

    // Regra 19: histórico por recurso
    List<Movimentacao> findByRecursoIdOrderByDataMovimentacaoDesc(Long recursoId);

    // Regra 11: histórico por usuário
    List<Movimentacao> findByUsuarioIdOrderByDataMovimentacaoDesc(Long usuarioId);

    // Regra 10/20: histórico por setor
    List<Movimentacao> findBySetorIdOrderByDataMovimentacaoDesc(Long setorId);

    // Filtro por setor + tipo
    List<Movimentacao> findBySetorIdAndTipoMovimentacaoOrderByDataMovimentacaoDesc(
            Long setorId, TipoMovimentacao tipo);

    // Histórico por base (via setor)
    List<Movimentacao> findBySetor_BaseIdOrderByDataMovimentacaoDesc(Long baseId);
}