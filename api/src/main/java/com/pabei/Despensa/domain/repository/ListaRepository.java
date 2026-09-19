package com.pabei.Despensa.domain.repository;

import com.pabei.Despensa.domain.model.Lista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ListaRepository extends JpaRepository<Lista, Long> {
    List<Lista> findByNombre(String nombre);
    List<Lista> findByGrupoId(Long grupoId);
    Optional<Lista> findByNombreAndGrupoId(String nombre, Long grupoId);
}
