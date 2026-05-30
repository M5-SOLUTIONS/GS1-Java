package br.com.m5_storage.repository;

import br.com.m5_storage.entity.alerta.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Regra 5/8: alertas ativos por recurso
    List<Alerta> findByRecursoIdAndResolvidoFalse(Long recursoId);

    // Regra 14: dashboard — todos os alertas ativos
    List<Alerta> findByResolvidoFalseOrderByDataAlertaDesc();

    // Regra 6/20: alertas ativos por setor
    List<Alerta> findByRecurso_Setor_IdAndResolvidoFalseOrderByDataAlertaDesc(Long setorId);

    // Regra 6: alertas ativos por base
    List<Alerta> findByRecurso_Setor_BaseIdAndResolvidoFalseOrderByDataAlertaDesc(Long baseId);
}