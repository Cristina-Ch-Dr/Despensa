package com.pabei.Despensa.infrastructure.controllers;

import com.pabei.Despensa.domain.ports.AgregarAmigoPort;
import com.pabei.Despensa.infrastructure.exceptions.ApiExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AgregarAmigoValidationTest {

    private final AgregarAmigoPort agregarAmigoPort = mock(AgregarAmigoPort.class);
    private final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    private MockMvc mockMvc;

    @BeforeEach
    void configurarMockMvc() {
        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new AgregarAmigoController(agregarAmigoPort))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @AfterEach
    void cerrarValidador() {
        validator.close();
    }

    @Test
    void enviarSolicitudConCuerpoVacioDevuelve400YErroresDeCampo() throws Exception {
        mockMvc.perform(post("/api/amigos/solicitudes")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("La petición contiene campos ausentes o inválidos."))
                .andExpect(jsonPath("$.errores.length()").value(2));

        verifyNoInteractions(agregarAmigoPort);
    }

    @Test
    void enviarSolicitudConEmailInvalidoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/amigos/solicitudes")
                        .contentType("application/json")
                        .content("""
                                {"solicitanteId": 1, "emailDestinatario": "no-es-un-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0].campo").value("emailDestinatario"));

        verifyNoInteractions(agregarAmigoPort);
    }

    @Test
    void enviarSolicitudConIdNoPositivoDevuelve400() throws Exception {
        mockMvc.perform(post("/api/amigos/solicitudes")
                        .contentType("application/json")
                        .content("""
                                {"solicitanteId": 0, "emailDestinatario": "bea@example.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0].campo").value("solicitanteId"));

        verifyNoInteractions(agregarAmigoPort);
    }

    @Test
    void aceptarSolicitudSinIdDestinatarioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/amigos/solicitudes/10/aceptar")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0].mensaje").value("El ID del destinatario es obligatorio."));

        verifyNoInteractions(agregarAmigoPort);
    }

    @Test
    void aceptarSolicitudConIdNoPositivoEnRutaDevuelve400() throws Exception {
        mockMvc.perform(post("/api/amigos/solicitudes/0/aceptar")
                        .contentType("application/json")
                        .content("""
                                {"destinatarioId": 2}
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(agregarAmigoPort);
    }
}
