package com.pabei.Despensa.domain.ports;

import com.pabei.Despensa.domain.dto.LoginRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;

public interface LoginPort {
    UsuarioResponse login(LoginRequest request);
}
