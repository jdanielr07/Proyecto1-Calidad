package com.membresias.exception;

/**
 * Excepción lanzada cuando una membresía no tiene suficientes puntos para redimir un beneficio.
 */
public class PuntosInsuficientesException extends RuntimeException {

    public PuntosInsuficientesException(int disponibles, int requeridos) {
        super("Puntos insuficientes. Disponibles: " + disponibles + ", Requeridos: " + requeridos);
    }
}