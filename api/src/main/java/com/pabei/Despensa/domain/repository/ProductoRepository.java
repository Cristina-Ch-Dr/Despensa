package com.pabei.Despensa.domain.repository;

import com.pabei.Despensa.domain.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombre(String nombre);
    List<Producto> findByCategoria(String categoria);
    Optional<Producto> findByNombreAndCategoria(String nombre, String categoria);
}
