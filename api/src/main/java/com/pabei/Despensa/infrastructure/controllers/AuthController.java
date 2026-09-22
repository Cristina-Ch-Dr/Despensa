package com.pabei.Despensa.infrastructure.controllers;

import com.pabei.Despensa.domain.dto.LoginRequest;
import com.pabei.Despensa.domain.dto.RegistroRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.ports.LoginPort;
import com.pabei.Despensa.domain.ports.RegistroPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistroPort registroPort;
    private final LoginPort loginPort;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@RequestBody RegistroRequest request) {
        UsuarioResponse response = registroPort.registrar(request);
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody LoginRequest request) {
        UsuarioResponse response = loginPort.login(request);
        return ResponseEntity.ok(response);
    }
}
