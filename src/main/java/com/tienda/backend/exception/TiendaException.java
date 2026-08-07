package com.tienda.backend.exception;

public abstract class TiendaException extends RuntimeException {

    public TiendaException(String mensaje) {
        super(mensaje);
    }
}
