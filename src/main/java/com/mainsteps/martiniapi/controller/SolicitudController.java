package com.mainsteps.martiniapi.controller;

import com.mainsteps.martiniapi.model.Division;
import com.mainsteps.martiniapi.model.Solicitud;
import com.mainsteps.martiniapi.service.SolicitudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints REST de las solicitudes de contacto.
 * URL base: /api/solicitudes
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudService servicio;

    public SolicitudController(SolicitudService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<Solicitud> listar(
            @RequestParam(required = false) Division division,
            @RequestParam(required = false) String telefono) {
        return servicio.listar(division, telefono);
    }

    @GetMapping("/{id}")
    public Solicitud obtener(@PathVariable Long id) {
        return servicio.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<Solicitud> crear(@Valid @RequestBody Solicitud solicitud) {
        Solicitud creada = servicio.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public Solicitud actualizar(@PathVariable Long id, @Valid @RequestBody Solicitud solicitud) {
        return servicio.actualizar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
