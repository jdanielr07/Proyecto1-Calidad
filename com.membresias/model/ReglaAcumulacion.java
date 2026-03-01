package com.membresias.model;

/**
 * Regla que define cuántos puntos se acumulan por unidad de uso del servicio.
 */
public class ReglaAcumulacion {

    private String id;
    private String descripcion;
    private int puntosPorUnidad;
    private boolean activa;

    public ReglaAcumulacion(String id, String descripcion, int puntosPorUnidad) {
        this.id = id;
        this.descripcion = descripcion;
        this.puntosPorUnidad = puntosPorUnidad;
        this.activa = true;
    }

    public String getId() { return id; }
    public String getDescripcion() { return descripcion; }
    public int getPuntosPorUnidad() { return puntosPorUnidad; }
    public boolean isActiva() { return activa; }

    public void setActiva(boolean activa) { this.activa = activa; }
    public void setPuntosPorUnidad(int puntosPorUnidad) { this.puntosPorUnidad = puntosPorUnidad; }

    @Override
    public String toString() {
        return "ReglaAcumulacion{id='" + id + "', puntosPorUnidad=" + puntosPorUnidad + ", activa=" + activa + "}";
    }
}