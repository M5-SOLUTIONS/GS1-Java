package br.com.m5_storage.repository;

import br.com.m5_storage.entity.recurso.Recurso;
import br.com.m5_storage.entity.recurso.StatusRecurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoRepository extends JpaRepository<Recurso, Long> {

    // Regra 14/17: dashboard por status
    List<Recurso> findByStatus(StatusRecurso status);

    // Regra 5/7: recursos críticos abaixo do mínimo
    @Query("SELECT r FROM Recurso r WHERE r.critico = true AND r.quantidade <= r.minimo")
    List<Recurso> findRecursosCriticos();

    // Regra 9: recursos por setor
    List<Recurso> findBySetorId(Long setorId);

    // Regra 9/20: recursos por base (via setor)
    List<Recurso> findBySetor_BaseId(Long baseId);
}