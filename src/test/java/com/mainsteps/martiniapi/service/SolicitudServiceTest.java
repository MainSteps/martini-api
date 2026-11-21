package com.mainsteps.martiniapi.service;

import com.mainsteps.martiniapi.exception.RecursoNoEncontradoException;
import com.mainsteps.martiniapi.model.Division;
import com.mainsteps.martiniapi.model.EstadoSolicitud;
import com.mainsteps.martiniapi.model.ProductoInteres;
import com.mainsteps.martiniapi.model.Solicitud;
import com.mainsteps.martiniapi.repository.SolicitudRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

    @Mock
    private SolicitudRepository repositorio;

    @InjectMocks
    private SolicitudService servicio;

    private Solicitud guardada;

    @BeforeEach
    void setUp() {
        guardada = new Solicitud();
        guardada.setId(1L);
        guardada.setNombre("Ana Soto");
        guardada.setTelefono("+56 9 8765 4321");
        guardada.setDivision(Division.PANADERIA_PASTELERIA);
        guardada.setComuna("Ñuñoa");
        guardada.setProducto(ProductoInteres.MANTECA_UNIGRAL);
        guardada.setEstado(EstadoSolicitud.PENDIENTE);
        guardada.setCreadaEn(1_737_000_000_000L);
    }

    @Test
    void listarSinFiltroDevuelveTodasOrdenadas() {
        when(repositorio.findAllByOrderByCreadaEnDesc()).thenReturn(List.of(guardada));

        List<Solicitud> resultado = servicio.listar(null);

        assertThat(resultado).containsExactly(guardada);
        verify(repositorio).findAllByOrderByCreadaEnDesc();
    }

    @Test
    void listarConDivisionFiltraPorDivision() {
        when(repositorio.findByDivisionOrderByCreadaEnDesc(Division.GELATERIA)).thenReturn(List.of());

        List<Solicitud> resultado = servicio.listar(Division.GELATERIA);

        assertThat(resultado).isEmpty();
        verify(repositorio).findByDivisionOrderByCreadaEnDesc(Division.GELATERIA);
    }

    @Test
    void obtenerPorIdLanzaSiNoExiste() {
        when(repositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtenerPorId(99L))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessage("No existe una solicitud con id 99");
    }

    @Test
    void crearAsignaEstadoPendienteFechaEIdNulo() {
        Solicitud entrada = new Solicitud();
        entrada.setId(50L);
        entrada.setNombre("Carlos Muñoz");
        entrada.setTelefono("+56 9 5123 7788");
        entrada.setDivision(Division.FOOD_SERVICE);
        entrada.setComuna("Las Condes");
        entrada.setProducto(ProductoInteres.CHOCOLATE_CARAVELLA);
        entrada.setEstado(null);
        entrada.setCreadaEn(0L);

        when(repositorio.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        long antes = System.currentTimeMillis();
        servicio.crear(entrada);
        long despues = System.currentTimeMillis();

        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);
        verify(repositorio).save(captor.capture());
        Solicitud resultado = captor.getValue();
        assertThat(resultado.getId()).isNull();
        assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitud.PENDIENTE);
        assertThat(resultado.getCreadaEn()).isBetween(antes, despues);
    }

    @Test
    void crearConservaCreadaEnSiVienerConValor() {
        Solicitud entrada = new Solicitud();
        entrada.setNombre("Carlos Muñoz");
        entrada.setTelefono("+56 9 5123 7788");
        entrada.setDivision(Division.FOOD_SERVICE);
        entrada.setComuna("Las Condes");
        entrada.setProducto(ProductoInteres.CHOCOLATE_CARAVELLA);
        entrada.setCreadaEn(1_700_000_000_000L);

        when(repositorio.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        servicio.crear(entrada);

        ArgumentCaptor<Solicitud> captor = ArgumentCaptor.forClass(Solicitud.class);
        verify(repositorio).save(captor.capture());
        assertThat(captor.getValue().getCreadaEn()).isEqualTo(1_700_000_000_000L);
    }

    @Test
    void actualizarModificaCamposYConservaFecha() {
        when(repositorio.findById(1L)).thenReturn(Optional.of(guardada));
        when(repositorio.save(any(Solicitud.class))).thenAnswer(inv -> inv.getArgument(0));

        Solicitud cambios = new Solicitud();
        cambios.setNombre("Ana Soto Actualizada");
        cambios.setTelefono("+56 9 1111 2222");
        cambios.setDivision(Division.RETAIL);
        cambios.setComuna("Providencia");
        cambios.setProducto(ProductoInteres.BRAVO_CREM);
        cambios.setEstado(EstadoSolicitud.CONTACTADO);

        Solicitud actualizada = servicio.actualizar(1L, cambios);

        assertThat(actualizada.getNombre()).isEqualTo("Ana Soto Actualizada");
        assertThat(actualizada.getTelefono()).isEqualTo("+56 9 1111 2222");
        assertThat(actualizada.getDivision()).isEqualTo(Division.RETAIL);
        assertThat(actualizada.getComuna()).isEqualTo("Providencia");
        assertThat(actualizada.getProducto()).isEqualTo(ProductoInteres.BRAVO_CREM);
        assertThat(actualizada.getEstado()).isEqualTo(EstadoSolicitud.CONTACTADO);
        assertThat(actualizada.getCreadaEn()).isEqualTo(1_737_000_000_000L);
        assertThat(actualizada.getId()).isEqualTo(1L);
    }

    @Test
    void eliminarLanzaSiNoExiste() {
        when(repositorio.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.eliminar(7L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
