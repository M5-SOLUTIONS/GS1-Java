package br.com.m5_storage.repository;

import br.com.m5_storage.entity.alerta.Alerta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    // Regra 5: buscar alertas ativos de um recurso
    List<Alerta> findByRecursoIdAndResolvidoFalse(Long recursoId);

    // Dashboard: todos os alertas não resolvidos
    List<Alerta> findByResolvidoFalseOrderByDataAlertaDesc();
}