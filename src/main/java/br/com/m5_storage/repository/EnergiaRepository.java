package br.com.m5_storage.repository;

import br.com.m5_storage.entity.recurso.Energia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnergiaRepository extends JpaRepository<Energia, Long> {

    // Regra 12: buscar por tipo de energia (Solar, Nuclear, Bateria...)
    List<Energia> findByTipoEnergia(String tipoEnergia);
}

