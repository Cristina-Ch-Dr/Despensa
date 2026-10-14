package com.pabei.Despensa.infrastructure.controllers;

import com.pabei.Despensa.domain.dto.SolicitudAmistadResponse;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.model.DireccionSolicitudAmistad;
import com.pabei.Despensa.domain.model.EstadoSolicitudAmistad;
import com.pabei.Despensa.domain.ports.AgregarAmigoPort;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/amigos")
@RequiredArgsConstructor
public class AmigosController {

    private final AgregarAmigoPort agregarAmigoPort;

    @GetMapping
    public List<UsuarioResponse> listarAmigos(
            @RequestParam @Positive(message = "El ID del usuario debe ser un número positivo.") Long usuarioId
    ) {
        return agregarAmigoPort.listarAmigos(usuarioId);
    }

    @GetMapping("/solicitudes")
    public List<SolicitudAmistadResponse> listarSolicitudes(
            @RequestParam @Positive(message = "El ID del usuario debe ser un número positivo.") Long usuarioId,
            @RequestParam(defaultValue = "PENDIENTE") EstadoSolicitudAmistad estado,
            @RequestParam DireccionSolicitudAmistad direccion
    ) {
        return agregarAmigoPort.listarSolicitudes(usuarioId, estado, direccion);
    }
}
