package com.pabei.Despensa.infrastructure.exceptions;

import com.pabei.Despensa.domain.exceptions.CredencialesInvalidasException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadConflictoException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadInvalidaException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadNoEncontradaException;
import com.pabei.Despensa.domain.exceptions.UsuarioNoEncontradoException;
import com.pabei.Despensa.domain.exceptions.UsuarioYaExisteException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();
    private final HttpServletRequest request = crearRequest("/api/amigos/solicitudes");

    @Test
    void usuarioInexistenteDevuelve404() {
        ProblemDetail problem = handler.manejarNoEncontrado(
                new UsuarioNoEncontradoException("usuario"), request
        );

        assertError(problem, HttpStatus.NOT_FOUND, "No se encontró el usuario: usuario");
    }

    @Test
    void solicitudInexistenteDevuelve404() {
        ProblemDetail problem = handler.manejarNoEncontrado(
                new SolicitudAmistadNoEncontradaException(15L), request
        );

        assertError(problem, HttpStatus.NOT_FOUND, "No se encontró la solicitud de amistad con ID 15");
    }

    @Test
    void operacionInvalidaDevuelve400() {
        ProblemDetail problem = handler.manejarPeticionInvalida(
                new SolicitudAmistadInvalidaException("No puedes enviarte una solicitud de amistad."),
                request
        );

        assertError(problem, HttpStatus.BAD_REQUEST, "No puedes enviarte una solicitud de amistad.");
    }

    @Test
    void conflictoDeAmistadDevuelve409() {
        ProblemDetail problem = handler.manejarConflicto(
                new SolicitudAmistadConflictoException("Ya existe una solicitud pendiente."),
                request
        );

        assertError(problem, HttpStatus.CONFLICT, "Ya existe una solicitud pendiente.");
    }

    @Test
    void usuarioDuplicadoDevuelve409() {
        ProblemDetail problem = handler.manejarConflicto(
                new UsuarioYaExisteException("ana@example.com"), request
        );

        assertError(problem, HttpStatus.CONFLICT, "El usuario con email ana@example.com ya existe");
    }

    @Test
    void credencialesInvalidasDevuelven401() {
        ProblemDetail problem = handler.manejarCredencialesInvalidas(
                new CredencialesInvalidasException(), request
        );

        assertError(problem, HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }

    private HttpServletRequest crearRequest(String path) {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getRequestURI()).thenReturn(path);
        return mockRequest;
    }

    private void assertError(ProblemDetail problem, HttpStatus status, String detail) {
        assertEquals(status.value(), problem.getStatus());
        assertEquals(detail, problem.getDetail());
        assertEquals(URI.create("/api/amigos/solicitudes"), problem.getInstance());
    }
}
