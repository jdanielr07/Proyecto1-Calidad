package com.quickbite.model;

import java.time.LocalDate;

/**
 * Representa un movimiento de puntos de lealtad en la cuenta de un suscriptor de QuickBite.
 * Puede ser una acumulación (por realizar un pedido) o una redención (por canjear una recompensa).
 */
public class MovimientoPuntos {

    private String id;
    private String suscripcionId;
    private TipoMovimiento tipo;
    private int cantidad;
    private LocalDate fecha;
    private String descripcion;

    public MovimientoPuntos(String id, String suscripcionId, TipoMovimiento tipo,
                            int cantidad, String descripcion) {
        this.id = id;
        this.suscripcionId = suscripcionId;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = LocalDate.now();
        this.descripcion = descripcion;
    }

    public String getId() { return id; }
    public String getSuscripcionId() { return suscripcionId; }
    public TipoMovimiento getTipo() { return tipo; }
    public int getCantidad() { return cantidad; }
    public LocalDate getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return "MovimientoPuntos{id='" + id + "', suscripcionId='" + suscripcionId +
               "', tipo=" + tipo + ", cantidad=" + cantidad + ", fecha=" + fecha + "}";
    }
}