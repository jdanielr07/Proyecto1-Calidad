package com.quickbite.exception;

public class SuscripcionNoEncontradaException extends RuntimeException {
    public SuscripcionNoEncontradaException(String id) {
        super("No se encontro una suscripcion con el ID: " + id);
    }
}
