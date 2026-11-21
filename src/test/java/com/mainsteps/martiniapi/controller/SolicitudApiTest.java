package com.mainsteps.martiniapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mainsteps.martiniapi.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SolicitudApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SolicitudRepository repositorio;

    @BeforeEach
    void limpiar() {
        repositorio.deleteAll();
    }

    private String json(Map<String, Object> campos) throws Exception {
        return objectMapper.writeValueAsString(campos);
    }

    private Map<String, Object> solicitudValida() {
        Map<String, Object> alta = new LinkedHashMap<>();
        alta.put("nombre", "Ana Soto");
        alta.put("telefono", "+56 9 8765 4321");
        alta.put("division", "PANADERIA_PASTELERIA");
        alta.put("comuna", "Ñuñoa");
        alta.put("producto", "MANTECA_UNIGRAL");
        return alta;
    }

    @Test
    void crudCompletoYErrores() throws Exception {
        Map<String, Object> alta = solicitudValida();

        MvcResult creado = mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(alta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Ana Soto"))
                .andExpect(jsonPath("$.telefono").value("+56 9 8765 4321"))
                .andExpect(jsonPath("$.division").value("PANADERIA_PASTELERIA"))
                .andExpect(jsonPath("$.comuna").value("Ñuñoa"))
                .andExpect(jsonPath("$.producto").value("MANTECA_UNIGRAL"))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.creadaEn").isNumber())
                .andReturn();

        long id = objectMapper.readTree(creado.getResponse().getContentAsString()).get("id").asLong();
        long creadaEn = objectMapper.readTree(creado.getResponse().getContentAsString()).get("creadaEn").asLong();

        mockMvc.perform(get("/api/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id));

        mockMvc.perform(get("/api/solicitudes").param("division", "PANADERIA_PASTELERIA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(get("/api/solicitudes").param("division", "RETAIL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/solicitudes/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comuna").value("Ñuñoa"));

        Map<String, Object> cambios = new LinkedHashMap<>(alta);
        cambios.put("comuna", "Providencia");
        cambios.put("estado", "CONTACTADO");

        mockMvc.perform(put("/api/solicitudes/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(cambios)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comuna").value("Providencia"))
                .andExpect(jsonPath("$.estado").value("CONTACTADO"))
                .andExpect(jsonPath("$.creadaEn").value(creadaEn));

        mockMvc.perform(get("/api/solicitudes/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.estado").value(404))
                .andExpect(jsonPath("$.error").value("No encontrado"))
                .andExpect(jsonPath("$.mensaje").value("No existe una solicitud con id 9999"));

        Map<String, Object> invalida = new LinkedHashMap<>();
        invalida.put("nombre", "A");
        invalida.put("telefono", "123");
        invalida.put("comuna", "X");

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(invalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.estado").value(400))
                .andExpect(jsonPath("$.error").value("Validación fallida"))
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.telefono").exists())
                .andExpect(jsonPath("$.errores.comuna").exists())
                .andExpect(jsonPath("$.errores.division").exists())
                .andExpect(jsonPath("$.errores.producto").exists());

        mockMvc.perform(delete("/api/solicitudes/" + id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/solicitudes/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void postAceptaTelefonosEnFormatosChilenosYOrdenaPorFecha() throws Exception {
        Map<String, Object> soloDigitos = solicitudValida();
        soloDigitos.put("nombre", "Luis Pérez");
        soloDigitos.put("telefono", "912345678");
        soloDigitos.put("division", "RETAIL");
        soloDigitos.put("comuna", "Maipú");
        soloDigitos.put("producto", "BRAVO_CREM");
        soloDigitos.put("creadaEn", 1_000L);

        Map<String, Object> conPrefijo = solicitudValida();
        conPrefijo.put("nombre", "Marta Díaz");
        conPrefijo.put("telefono", "+56912345678");
        conPrefijo.put("division", "RETAIL");
        conPrefijo.put("comuna", "Puente Alto");
        conPrefijo.put("producto", "LINEA_VEGANA_ORASI");
        conPrefijo.put("creadaEn", 2_000L);

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(soloDigitos)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.telefono").value("912345678"));

        mockMvc.perform(post("/api/solicitudes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(conPrefijo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.telefono").value("+56912345678"));

        mockMvc.perform(get("/api/solicitudes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Marta Díaz"))
                .andExpect(jsonPath("$[1].nombre").value("Luis Pérez"));
    }
}
