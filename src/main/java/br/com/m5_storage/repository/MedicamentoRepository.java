package br.com.m5_storage.repository;

import br.com.m5_storage.entity.recurso.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicamentoRepository extends JpaRepository<Medicamento, Long> {

    List<Medicamento> findByValidadeBeforeOrderByValidadeAsc(LocalDate data);
}
