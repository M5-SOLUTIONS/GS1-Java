package br.com.m5_storage.repository;

import br.com.m5_storage.entity.alerta.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    List<Alerta> findByRecursoBaseIdAndResolvidoFalseOrderByDataAlertaDesc(Long baseId);

    List<Alerta> findByRecursoIdAndRecursoBaseIdAndResolvidoFalse(Long recursoId, Long baseId);

    List<Alerta> findByRecursoIdAndResolvidoFalse(Long recursoId);
}