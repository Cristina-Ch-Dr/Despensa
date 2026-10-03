package com.pabei.Despensa.application.service;

import com.pabei.Despensa.application.services.LoginService;
import com.pabei.Despensa.domain.dto.LoginRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.exceptions.CredencialesInvalidasException;
import com.pabei.Despensa.domain.model.Usuario;
import com.pabei.Despensa.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginService loginService;

    @Test
    void loginConCredencialesValidasDevuelveDatosPublicosDelUsuario() {
        // Given
        LoginRequest request = new LoginRequest("ana@example.com", "password-plano");
        Usuario usuario = new Usuario();
        usuario.setUsuarioId(7L);
        usuario.setNombre("Ana");
        usuario.setApellidos("Garcia");
        usuario.setEmail("ana@example.com");
        usuario.setPassword("password-encriptado");
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("password-plano", "password-encriptado")).thenReturn(true);

        // When
        UsuarioResponse response = loginService.login(request);

        // Then
        assertEquals(new UsuarioResponse(7L, "Ana", "Garcia", "ana@example.com"), response);
        verify(usuarioRepository).findByEmail("ana@example.com");
        verify(passwordEncoder).matches("password-plano", "password-encriptado");
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder);
    }

    @Test
    void loginConEmailInexistenteLanzaExcepcionSinValidarPassword() {
        // Given
        LoginRequest request = new LoginRequest("ausente@example.com", "password-plano");
        when(usuarioRepository.findByEmail("ausente@example.com")).thenReturn(Optional.empty());

        // When
        CredencialesInvalidasException exception = assertThrows(
                CredencialesInvalidasException.class,
                () -> loginService.login(request)
        );

        // Then
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository).findByEmail("ausente@example.com");
        verifyNoMoreInteractions(usuarioRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void loginConPasswordIncorrectaLanzaExcepcion() {
        // Given
        LoginRequest request = new LoginRequest("ana@example.com", "incorrecta");
        Usuario usuario = new Usuario();
        usuario.setPassword("password-encriptado");
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("incorrecta", "password-encriptado")).thenReturn(false);

        // When
        CredencialesInvalidasException exception = assertThrows(
                CredencialesInvalidasException.class,
                () -> loginService.login(request)
        );

        // Then
        assertEquals("Credenciales inválidas", exception.getMessage());
        verify(usuarioRepository).findByEmail("ana@example.com");
        verify(passwordEncoder).matches("incorrecta", "password-encriptado");
        verifyNoMoreInteractions(usuarioRepository, passwordEncoder);
    }
}
