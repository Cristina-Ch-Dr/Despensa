package com.pabei.Despensa.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productoId;

    private String nombre;
    private String descripcion;
    private Double precio;
    private String categoria;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @ManyToMany(mappedBy = "productos")
    private Set<Lista> listas = new HashSet<>();
}
