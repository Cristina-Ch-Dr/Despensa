package com.pabei.Despensa.domain.repository;

import com.pabei.Despensa.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    List<Usuario> findByNombre(String nombre);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByNombreAndApellidos(String nombre, String apellidos);
}
