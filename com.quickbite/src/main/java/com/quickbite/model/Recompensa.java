package com.quickbite.model;

/**
 * Representa una recompensa que los suscriptores de QuickBite pueden canjear
 * usando sus puntos de lealtad acumulados.
 *
 * Ejemplos de recompensas: delivery gratis, descuento en el próximo pedido,
 * postre gratis, bebida adicional, etc.
 */
public class Recompensa {

    private String id;
    private String nombre;
    private String descripcion;
    private int puntosRequeridos;
    private boolean disponible;

    public Recompensa(String id, String nombre, String descripcion, int puntosRequeridos) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.puntosRequeridos = puntosRequeridos;
        this.disponible = true;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int getPuntosRequeridos() { return puntosRequeridos; }
    public boolean isDisponible() { return disponible; }

    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    public void setPuntosRequeridos(int puntosRequeridos) { this.puntosRequeridos = puntosRequeridos; }

    @Override
    public String toString() {
        return "Recompensa{id='" + id + "', nombre='" + nombre +
               "', puntosRequeridos=" + puntosRequeridos + ", disponible=" + disponible + "}";
    }
}