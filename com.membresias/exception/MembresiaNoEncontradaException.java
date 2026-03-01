package com.membresias.exception;

/**
 * Excepción lanzada cuando no se encuentra una membresía con el ID indicado.
 */
public class MembresiaNoEncontradaException extends RuntimeException {

    public MembresiaNoEncontradaException(String membresiaId) {
        super("No se encontró una membresía con el ID: " + membresiaId);
    }
}