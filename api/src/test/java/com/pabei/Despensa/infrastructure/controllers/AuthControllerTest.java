package com.pabei.Despensa.infrastructure.controllers;

import com.pabei.Despensa.domain.dto.LoginRequest;
import com.pabei.Despensa.domain.dto.RegistroRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.ports.LoginPort;
import com.pabei.Despensa.domain.ports.RegistroPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private RegistroPort registroPort;

    @Mock
    private LoginPort loginPort;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerRegistraUsuarioYDevuelveEstadoCreated() {
        // Given
        RegistroRequest request = new RegistroRequest(
                "Ana", "Garcia", "ana@example.com", "password"
        );
        UsuarioResponse usuarioResponse = new UsuarioResponse(
                7L, "Ana", "Garcia", "ana@example.com"
        );
        when(registroPort.registrar(request)).thenReturn(usuarioResponse);

        // When
        ResponseEntity<UsuarioResponse> response = authController.register(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(usuarioResponse, response.getBody());
        verify(registroPort).registrar(request);
        verifyNoMoreInteractions(registroPort);
        verifyNoInteractions(loginPort);
    }

    @Test
    void loginAutenticaUsuarioYDevuelveEstadoOk() {
        // Given
        LoginRequest request = new LoginRequest("ana@example.com", "password");
        UsuarioResponse usuarioResponse = new UsuarioResponse(
                7L, "Ana", "Garcia", "ana@example.com"
        );
        when(loginPort.login(request)).thenReturn(usuarioResponse);

        // When
        ResponseEntity<UsuarioResponse> response = authController.login(request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(usuarioResponse, response.getBody());
        verify(loginPort).login(request);
        verifyNoMoreInteractions(loginPort);
        verifyNoInteractions(registroPort);
    }
}
