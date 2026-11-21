package com.mainsteps.martiniapi.service;

import com.mainsteps.martiniapi.exception.RecursoNoEncontradoException;
import com.mainsteps.martiniapi.model.Division;
import com.mainsteps.martiniapi.model.EstadoSolicitud;
import com.mainsteps.martiniapi.model.Solicitud;
import com.mainsteps.martiniapi.repository.SolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de las solicitudes de contacto.
 * El controlador no habla con la base: pasa por esta capa.
 */
@Service
public class SolicitudService {

    private final SolicitudRepository repositorio;

    public SolicitudService(SolicitudRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listar(Division division) {
        if (division == null) {
            return repositorio.findAllByOrderByCreadaEnDesc();
        }
        return repositorio.findByDivisionOrderByCreadaEnDesc(division);
    }

    @Transactional(readOnly = true)
    public Solicitud obtenerPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una solicitud con id " + id));
    }

    @Transactional
    public Solicitud crear(Solicitud solicitud) {
        // El id lo asigna la base. Si no viene estado o fecha, se completan aquí.
        solicitud.setId(null);
        if (solicitud.getEstado() == null) {
            solicitud.setEstado(EstadoSolicitud.PENDIENTE);
        }
        if (solicitud.getCreadaEn() == null || solicitud.getCreadaEn() == 0L) {
            solicitud.setCreadaEn(System.currentTimeMillis());
        }
        return repositorio.save(solicitud);
    }

    @Transactional
    public Solicitud actualizar(Long id, Solicitud datos) {
        Solicitud existente = obtenerPorId(id);
        existente.setNombre(datos.getNombre());
        existente.setTelefono(datos.getTelefono());
        existente.setDivision(datos.getDivision());
        existente.setComuna(datos.getComuna());
        existente.setProducto(datos.getProducto());
        if (datos.getEstado() != null) {
            existente.setEstado(datos.getEstado());
        }
        // creadaEn no se toca: se conserva la fecha original.
        return repositorio.save(existente);
    }

    @Transactional
    public void eliminar(Long id) {
        Solicitud existente = obtenerPorId(id);
        repositorio.delete(existente);
    }
}
