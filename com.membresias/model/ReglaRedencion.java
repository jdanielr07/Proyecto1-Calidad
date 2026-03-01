package com.membresias.model;

/**
 * Regla que define las condiciones para redimir puntos por beneficios.
 */
public class ReglaRedencion {

    private String id;
    private String descripcion;
    private int puntosMinimosRedencion;
    private boolean activa;

    public ReglaRedencion(String id, String descripcion, int puntosMinimosRedencion) {
        this.id = id;
        this.descripcion = descripcion;
        this.puntosMinimosRedencion = puntosMinimosRedencion;
        this.activa = true;
    }

    public String getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public int getPuntosMinimosRedencion() { return puntosMinimosRedencion; }
    public boolean isActiva() { return activa; }

    public void setActiva(boolean activa) { this.activa = activa; }
    public void setPuntosMinimosRedencion(int min) { this.puntosMinimosRedencion = min; }

    @Override
    public String toString() {
        return "ReglaRedencion{id='" + id + "', puntosMinimos=" + puntosMinimosRedencion + ", activa=" + activa + "}";
    }
}