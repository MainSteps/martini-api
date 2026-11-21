package com.mainsteps.martiniapi.repository;

import com.mainsteps.martiniapi.model.Division;
import com.mainsteps.martiniapi.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla de solicitudes.
 * Spring Data JPA implementa estos métodos automáticamente
 * a partir del nombre (findBy... OrderBy...).
 */
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findAllByOrderByCreadaEnDesc();

    List<Solicitud> findByDivisionOrderByCreadaEnDesc(Division division);
}
