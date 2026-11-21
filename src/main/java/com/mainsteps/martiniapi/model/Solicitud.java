package com.mainsteps.martiniapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Solicitud del formulario "Quiero que me contacten" de Martini Conecta.
 * Usa los mismos campos que SolicitudContacto en la app, más un estado
 * (PENDIENTE o CONTACTADO). Se usa la misma clase como entidad JPA y
 * como cuerpo JSON, sin DTO, para que sea fácil de explicar en la defensa.
 */
@Entity
@Table(name = "solicitudes")
public class Solicitud {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener al menos 2 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(
            regexp = "^(?:\\+?56[\\s()-]*)?9[\\s()-]*\\d{4}[\\s()-]*\\d{4}$",
            message = "Ingresa un celular chileno de 9 dígitos"
    )
    @Column(nullable = false, length = 30)
    private String telefono;

    @NotNull(message = "La división es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Division division;

    @NotBlank(message = "La comuna es obligatoria")
    @Size(min = 2, max = 80, message = "La comuna debe tener al menos 2 caracteres")
    @Column(nullable = false, length = 80)
    private String comuna;

    @NotNull(message = "El producto es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ProductoInteres producto;

    /** Epoch en milisegundos, igual que creadaEn en la app. */
    @Column(nullable = false, updatable = false)
    private Long creadaEn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoSolicitud estado = EstadoSolicitud.PENDIENTE;

    public Solicitud() {
    }

    @PrePersist
    public void alCrear() {
        if (creadaEn == null || creadaEn == 0L) {
            creadaEn = System.currentTimeMillis();
        }
        if (estado == null) {
            estado = EstadoSolicitud.PENDIENTE;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre == null ? null : nombre.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono == null ? null : telefono.trim();
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna == null ? null : comuna.trim();
    }

    public ProductoInteres getProducto() {
        return producto;
    }

    public void setProducto(ProductoInteres producto) {
        this.producto = producto;
    }

    public Long getCreadaEn() {
        return creadaEn;
    }

    public void setCreadaEn(Long creadaEn) {
        this.creadaEn = creadaEn;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitud estado) {
        this.estado = estado;
    }
}
