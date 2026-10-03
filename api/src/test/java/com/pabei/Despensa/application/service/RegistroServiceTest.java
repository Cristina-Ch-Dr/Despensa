package com.pabei.Despensa.application.service;

import com.pabei.Despensa.application.services.RegistroService;
import com.pabei.Despensa.domain.dto.RegistroRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.exceptions.UsuarioYaExisteException;
import com.pabei.Despensa.domain.model.Usuario;
import com.pabei.Despensa.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistroServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistroService registroService;

    @Test
    void registrarCuandoElEmailNoExisteGuardaElUsuarioConPasswordEncriptada() {
        // Given
        RegistroRequest request = new RegistroRequest(
                "Ana", "Garcia", "ana@example.com", "password-plano"
        );
        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setUsuarioId(7L);
        usuarioGuardado.setNombre("Ana");
        usuarioGuardado.setApellidos("Garcia");
        usuarioGuardado.setEmail("ana@example.com");
        usuarioGuardado.setPassword("password-encriptado");
        when(usuarioRepository.findByEmail("ana@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password-plano")).thenReturn("password-encriptado");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        // When
        UsuarioResponse response = registroService.registrar(request);

        // Then
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario usuarioEnviado = usuarioCaptor.getValue();
        assertEquals("Ana", usuarioEnviado.getNombre());
        assertEquals("Garcia", usuarioEnviado.getApellidos());
        assertEquals("ana@example.com", usuarioEnviado.getEmail());
        assertEquals("password-encriptado", usuarioEnviado.getPassword());
        assertEquals(new UsuarioResponse(7L, "Ana", "Garcia", "ana@example.com"), response);
        verify(usuarioRepository).findByEmail("ana@example.com");
        verify(passwordEncoder).encode("password-plano");
    }

    @Test
    void registrarCuandoElEmailYaExisteLanzaExcepcionSinCodificarNiGuardar() {
        // Given
        String email = "ana@example.com";
        RegistroRequest request = new RegistroRequest("Ana", "Garcia", email, "password-plano");
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(new Usuario()));

        // When
        UsuarioYaExisteException exception = assertThrows(
                UsuarioYaExisteException.class,
                () -> registroService.registrar(request)
        );

        // Then
        assertEquals("El usuario con email " + email + " ya existe", exception.getMessage());
        verify(usuarioRepository).findByEmail(email);
        verify(usuarioRepository, never()).save(any(Usuario.class));
        verifyNoInteractions(passwordEncoder);
    }
}
