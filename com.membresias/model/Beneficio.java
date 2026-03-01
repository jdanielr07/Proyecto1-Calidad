package com.membresias.model;

/**
 * Representa un beneficio que puede ser redimido con puntos.
 */
public class Beneficio {

    private String id;
    private String nombre;
    private int puntosRequeridos;
    private boolean disponible;

    public Beneficio(String id, String nombre, int puntosRequeridos) {
        this.id = id;
        this.nombre = nombre;
        this.puntosRequeridos = puntosRequeridos;
        this.disponible = true;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPuntosRequeridos() { return puntosRequeridos; }
    public boolean isDisponible() { return disponible; }

    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setPuntosRequeridos(int puntosRequeridos) { this.puntosRequeridos = puntosRequeridos; }

    @Override
    public String toString() {
        return "Beneficio{id='" + id + "', nombre='" + nombre +
               "', puntosRequeridos=" + puntosRequeridos + "}";
    }
}