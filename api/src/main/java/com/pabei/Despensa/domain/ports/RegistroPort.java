package com.pabei.Despensa.domain.ports;

import com.pabei.Despensa.domain.dto.RegistroRequest;
import com.pabei.Despensa.domain.dto.UsuarioResponse;

public interface RegistroPort {
    UsuarioResponse registrar(RegistroRequest request);
}
