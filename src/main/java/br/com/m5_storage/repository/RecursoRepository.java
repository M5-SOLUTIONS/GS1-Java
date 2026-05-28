package br.com.m5_storage.repository;

import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    List<Recurso> findByStatus(StatusRecurso status);

    List<Recurso> findByCriticoTrueAndQuantidadeLessThanEqualMinimo();
}
