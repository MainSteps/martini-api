package com.mainsteps.martiniapi.exception;

/**
 * Se lanza cuando no existe una solicitud con el id pedido.
 * El manejador global la convierte en una respuesta HTTP 404.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
