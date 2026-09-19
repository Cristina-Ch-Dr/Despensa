package com.pabei.Despensa.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grupoId;

    private String nombre;
    private String descripcion;

    @CreationTimestamp
    private LocalDateTime fechaCreacion;

    @ManyToMany(mappedBy = "grupos")
    private Set<Usuario> usuarios = new HashSet<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lista> listas = new HashSet<>();
}
