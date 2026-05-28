package br.com.m5_storage.repository;

import br.com.m5_storage.entity.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Regra 13: email único — usado para validar duplicidade
    boolean existsByEmail(String email);

    Optional<Usuario> findByEmail(String email);
}
