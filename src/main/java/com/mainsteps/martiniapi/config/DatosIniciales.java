package com.mainsteps.martiniapi.config;

import com.mainsteps.martiniapi.model.Division;
import com.mainsteps.martiniapi.model.EstadoSolicitud;
import com.mainsteps.martiniapi.model.ProductoInteres;
import com.mainsteps.martiniapi.model.Solicitud;
import com.mainsteps.martiniapi.repository.SolicitudRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Carga 3 solicitudes de ejemplo solo si la tabla está vacía.
 * No corre en el perfil "test", para no interferir con las pruebas.
 */
@Component
@Profile("!test")
public class DatosIniciales implements CommandLineRunner {

    private final SolicitudRepository repositorio;

    public DatosIniciales(SolicitudRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void run(String... args) {
        if (repositorio.count() > 0) {
            return;
        }

        long ahora = System.currentTimeMillis();

        Solicitud una = new Solicitud();
        una.setNombre("Ana Soto");
        una.setTelefono("+56 9 8765 4321");
        una.setDivision(Division.PANADERIA_PASTELERIA);
        una.setComuna("Ñuñoa");
        una.setProducto(ProductoInteres.MANTECA_UNIGRAL);
        una.setEstado(EstadoSolicitud.PENDIENTE);
        una.setCreadaEn(ahora - 86_400_000L); // hace 1 día

        Solicitud dos = new Solicitud();
        dos.setNombre("Carlos Muñoz");
        dos.setTelefono("+56 9 5123 7788");
        dos.setDivision(Division.FOOD_SERVICE);
        dos.setComuna("Las Condes");
        dos.setProducto(ProductoInteres.CHOCOLATE_CARAVELLA);
        dos.setEstado(EstadoSolicitud.PENDIENTE);
        dos.setCreadaEn(ahora - 3_600_000L); // hace 1 hora

        Solicitud tres = new Solicitud();
        tres.setNombre("Rosa Pérez");
        tres.setTelefono("+56 9 9444 2211");
        tres.setDivision(Division.GELATERIA);
        tres.setComuna("San Miguel");
        tres.setProducto(ProductoInteres.MONNALISA);
        tres.setEstado(EstadoSolicitud.CONTACTADO);
        tres.setCreadaEn(ahora - 172_800_000L); // hace 2 días

        repositorio.save(una);
        repositorio.save(dos);
        repositorio.save(tres);
    }
}
