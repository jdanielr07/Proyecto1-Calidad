package com.quickbite.model;

import java.time.LocalDate;

/**
 * Representa una suscripción de un cliente al programa de lealtad de QuickBite.
 * Los clientes suscritos acumulan puntos de lealtad por cada pedido realizado
 * y pueden canjearlos por recompensas como delivery gratis o descuentos.
 */
public class Suscripcion {

    private String id;
    private String clienteId;
    private EstadoSuscripcion estado;
    private LocalDate fechaInicio;
    private int puntosDeLealtad;

    public Suscripcion(String id, String clienteId, LocalDate fechaInicio) {
        this.id = id;
        this.clienteId = clienteId;
        this.fechaInicio = fechaInicio;
        this.estado = EstadoSuscripcion.ACTIVA;
        this.puntosDeLealtad = 0;
    }

    public String getId() { return id; }
    public String getClienteId() { return clienteId; }
    public EstadoSuscripcion getEstado() { return estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public int getPuntosDeLealtad() { return puntosDeLealtad; }

    public void setEstado(EstadoSuscripcion estado) { this.estado = estado; }
    public void setPuntosDeLealtad(int puntosDeLealtad) { this.puntosDeLealtad = puntosDeLealtad; }

    /**
     * Indica si la suscripción está activa.
     */
    public boolean estaActiva() {
        return this.estado == EstadoSuscripcion.ACTIVA;
    }

    @Override
    public String toString() {
        return "Suscripcion{id='" + id + "', clienteId='" + clienteId +
               "', estado=" + estado + ", puntosDeLealtad=" + puntosDeLealtad + "}";
    }
}