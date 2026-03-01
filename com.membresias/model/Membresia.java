package com.membresias.model;

import java.time.LocalDate;

/**
 * Representa una membresía dentro del sistema de beneficios.
 */
public class Membresia {

    private String id;
    private String usuarioId;
    private EstadoMembresia estado;
    private LocalDate fechaInicio;
    private int puntosAcumulados;

    public Membresia(String id, String usuarioId, LocalDate fechaInicio) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.fechaInicio = fechaInicio;
        this.estado = EstadoMembresia.ACTIVA;
        this.puntosAcumulados = 0;
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public EstadoMembresia getEstado() { return estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public int getPuntosAcumulados() { return puntosAcumulados; }

    public void setEstado(EstadoMembresia estado) { this.estado = estado; }
    public void setPuntosAcumulados(int puntosAcumulados) { this.puntosAcumulados = puntosAcumulados; }

    /**
     * Indica si la membresía está activa.
     */
    public boolean estaActiva() {
        return this.estado == EstadoMembresia.ACTIVA;
    }

    @Override
    public String toString() {
        return "Membresia{id='" + id + "', usuarioId='" + usuarioId +
               "', estado=" + estado + ", puntos=" + puntosAcumulados + "}";
    }
}