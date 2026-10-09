package com.pabei.Despensa.infrastructure.exceptions;

import com.pabei.Despensa.domain.exceptions.ContrasenasNoCoincidentesException;
import com.pabei.Despensa.domain.exceptions.CredencialesInvalidasException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadConflictoException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadInvalidaException;
import com.pabei.Despensa.domain.exceptions.SolicitudAmistadNoEncontradaException;
import com.pabei.Despensa.domain.exceptions.UsuarioNoEncontradoException;
import com.pabei.Despensa.domain.exceptions.UsuarioYaExisteException;
import jakarta.validation.ConstraintViolationException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.validation.FieldError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({
            UsuarioNoEncontradoException.class,
            SolicitudAmistadNoEncontradaException.class
    })
    public ProblemDetail manejarNoEncontrado(RuntimeException exception, HttpServletRequest request) {
        return crearProblema(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler({
            UsuarioYaExisteException.class,
            SolicitudAmistadConflictoException.class
    })
    public ProblemDetail manejarConflicto(RuntimeException exception, HttpServletRequest request) {
        return crearProblema(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler({
            SolicitudAmistadInvalidaException.class,
            ContrasenasNoCoincidentesException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            ConstraintViolationException.class
    })
    public ProblemDetail manejarPeticionInvalida(Exception exception, HttpServletRequest request) {
        String detalle = exception instanceof SolicitudAmistadInvalidaException
                || exception instanceof ContrasenasNoCoincidentesException
                ? exception.getMessage()
                : "La petición contiene datos ausentes o con formato inválido.";
        return crearProblema(HttpStatus.BAD_REQUEST, detalle, request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail manejarValidacionDeParametros(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = crearProblema(
                HttpStatus.BAD_REQUEST,
                "La petición contiene campos ausentes o inválidos.",
                request
        );
        List<Map<String, String>> errores = new ArrayList<>();
        exception.getParameterValidationResults().forEach(resultado ->
                resultado.getResolvableErrors().forEach(error -> errores.add(Map.of(
                        "campo", resultado.getMethodParameter().getParameterName() == null
                                ? "parámetro"
                                : resultado.getMethodParameter().getParameterName(),
                        "mensaje", error.getDefaultMessage() == null
                                ? "El valor no es válido."
                                : error.getDefaultMessage()
                )))
        );
        problem.setProperty("errores", errores);
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarErroresDeCampos(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        ProblemDetail problem = crearProblema(
                HttpStatus.BAD_REQUEST,
                "La petición contiene campos ausentes o inválidos.",
                request
        );
        List<Map<String, String>> errores = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapearError)
                .toList();
        problem.setProperty("errores", errores);
        return problem;
    }

    @ExceptionHandler(CredencialesInvalidasException.class)
    public ProblemDetail manejarCredencialesInvalidas(
            CredencialesInvalidasException exception,
            HttpServletRequest request
    ) {
        return crearProblema(HttpStatus.UNAUTHORIZED, exception.getMessage(), request);
    }

    private ProblemDetail crearProblema(HttpStatus status, String detalle, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detalle);
        problem.setInstance(URI.create(request.getRequestURI()));
        return problem;
    }

    private Map<String, String> mapearError(FieldError error) {
        return Map.of("campo", error.getField(), "mensaje", error.getDefaultMessage());
    }
}
