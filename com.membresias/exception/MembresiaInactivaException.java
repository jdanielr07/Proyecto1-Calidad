package com.membresias.exception;

/**
 * Excepción lanzada cuando se intenta operar sobre una membresía inactiva.
 */
public class MembresiaInactivaException extends RuntimeException {

    public MembresiaInactivaException(String membresiaId) {
        super("La membresía con ID: " + membresiaId + " se encuentra inactiva.");
    }
}