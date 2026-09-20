package com.pabei.Despensa.application.services;

import com.pabei.Despensa.domain.dto.RegistroRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.exceptions.ContrasenasNoCoincidentesException;
import com.pabei.Despensa.domain.exceptions.UsuarioYaExisteException;
import com.pabei.Despensa.domain.model.Usuario;
import com.pabei.Despensa.domain.ports.RegistroPort;
import com.pabei.Despensa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RegistroService implements RegistroPort {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse registrar(RegistroRequest request) {
        validarContrasenas(request);
        validarEmailNoExiste(request.getEmail());

        Usuario usuario = crearUsuario(request);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        return mapearAResponse(usuarioGuardado);
    }

    private void validarContrasenas(RegistroRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirmacion())) {
            throw new ContrasenasNoCoincidentesException();
        }
    }

    private void validarEmailNoExiste(String email) {
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            throw new UsuarioYaExisteException(email);
        });
    }

    private Usuario crearUsuario(RegistroRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        return usuario;
    }

    private UsuarioResponse mapearAResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getUsuarioId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getEmail()
        );
    }
}
