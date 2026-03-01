package com.quickbite.exception;

public class SuscripcionInactivaException extends RuntimeException {
    public SuscripcionInactivaException(String id) {
        super("La suscripcion con ID: " + id + " se encuentra inactiva.");
    }
}
