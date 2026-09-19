package com.pabei.Despensa.domain.repository;

import com.pabei.Despensa.domain.model.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    List<Grupo> findByNombre(String nombre);
    Optional<Grupo> findByNombreAndUsuariosId(String nombre, Long usuarioId);
}
