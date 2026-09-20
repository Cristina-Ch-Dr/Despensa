package com.pabei.Despensa.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long usuarioId;
    private String nombre;
    private String apellidos;
    private String email;
}
