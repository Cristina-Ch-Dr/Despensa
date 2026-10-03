package com.pabei.Despensa.application.services;

import com.pabei.Despensa.domain.dto.LoginRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;
import com.pabei.Despensa.domain.exceptions.CredencialesInvalidasException;
import com.pabei.Despensa.domain.model.Usuario;
import com.pabei.Despensa.domain.ports.LoginPort;
import com.pabei.Despensa.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginService implements LoginPort {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UsuarioResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new CredencialesInvalidasException();
        }

        return mapearAResponse(usuario);
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
