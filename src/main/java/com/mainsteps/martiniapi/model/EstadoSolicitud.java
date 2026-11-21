package com.mainsteps.martiniapi.model;

/**
 * Estado de una solicitud de contacto.
 * PENDIENTE: recién enviada, todavía no se contacta al cliente.
 * CONTACTADO: el equipo de ventas ya se comunicó.
 */
public enum EstadoSolicitud {
    PENDIENTE,
    CONTACTADO
}
