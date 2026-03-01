package com.quickbite.service;

import com.quickbite.exception.SuscripcionInactivaException;
import com.quickbite.exception.SuscripcionNoEncontradaException;
import com.quickbite.exception.PuntosInsuficientesException;
import com.quickbite.model.*;
import com.quickbite.repository.SuscripcionRepository;
import com.quickbite.repository.PuntosRepository;

import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona los puntos de lealtad de los suscriptores de QuickBite.
 * Los clientes acumulan puntos por cada pedido y los canjean por recompensas.
 *
 * HU-4: Acumular puntos por pedido
 * HU-5: Canjear puntos por recompensas
 * HU-8: Consultar historial de movimientos de puntos
 * HU-10: Reporte de puntos acumulados y canjeados
 */
public class PuntosService {

    private final SuscripcionRepository suscripcionRepository;
    private final PuntosRepository puntosRepository;

    public PuntosService(SuscripcionRepository suscripcionRepository, PuntosRepository puntosRepository) {
        this.suscripcionRepository = suscripcionRepository;
        this.puntosRepository = puntosRepository;
    }

    /**
     * HU-4: Acumula puntos de lealtad en la suscripción activa de un cliente.
     * Los puntos se otorgan al realizar un pedido en QuickBite.
     *
     * @param suscripcionId ID de la suscripción del cliente.
     * @param cantidad      Cantidad de puntos a acumular (debe ser mayor a 0).
     * @param descripcion   Descripción del pedido que generó los puntos.
     * @return El movimiento de acumulación registrado.
     * @throws SuscripcionNoEncontradaException si la suscripción no existe.
     * @throws SuscripcionInactivaException     si la suscripción está inactiva.
     * @throws IllegalArgumentException         si la cantidad de puntos es inválida.
     */
    public MovimientoPuntos acumularPuntos(String suscripcionId, int cantidad, String descripcion) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos a acumular debe ser mayor a 0.");
        }
        Suscripcion suscripcion = obtenerSuscripcionActivaOLanzarExcepcion(suscripcionId);

        suscripcion.setPuntosDeLealtad(suscripcion.getPuntosDeLealtad() + cantidad);
        suscripcionRepository.guardar(suscripcion);

        MovimientoPuntos movimiento = new MovimientoPuntos(
                generarId(), suscripcionId, TipoMovimiento.ACUMULACION, cantidad, descripcion);
        puntosRepository.guardar(movimiento);
        return movimiento;
    }

    /**
     * HU-5: Canjea puntos de lealtad de una suscripción para obtener una recompensa de QuickBite.
     * Ejemplos de recompensas: delivery gratis, descuento en el próximo pedido.
     *
     * @param suscripcionId ID de la suscripción del cliente.
     * @param recompensa    Recompensa que se desea canjear.
     * @return El movimiento de redención registrado.
     * @throws SuscripcionNoEncontradaException si la suscripción no existe.
     * @throws SuscripcionInactivaException     si la suscripción está inactiva.
     * @throws PuntosInsuficientesException     si no hay suficientes puntos de lealtad.
     * @throws IllegalArgumentException         si la recompensa no está disponible.
     */
    public MovimientoPuntos canjearRecompensa(String suscripcionId, Recompensa recompensa) {
        if (recompensa == null || !recompensa.isDisponible()) {
            throw new IllegalArgumentException("La recompensa no está disponible o es nula.");
        }
        Suscripcion suscripcion = obtenerSuscripcionActivaOLanzarExcepcion(suscripcionId);

        int puntosDisponibles = suscripcion.getPuntosDeLealtad();
        int puntosRequeridos = recompensa.getPuntosRequeridos();

        if (puntosDisponibles < puntosRequeridos) {
            throw new PuntosInsuficientesException(puntosDisponibles, puntosRequeridos);
        }

        suscripcion.setPuntosDeLealtad(puntosDisponibles - puntosRequeridos);
        suscripcionRepository.guardar(suscripcion);

        MovimientoPuntos movimiento = new MovimientoPuntos(
                generarId(), suscripcionId, TipoMovimiento.REDENCION,
                puntosRequeridos, "Canje: " + recompensa.getNombre());
        puntosRepository.guardar(movimiento);
        return movimiento;
    }

    /**
     * HU-8: Obtiene el historial completo de movimientos de puntos de una suscripción.
     *
     * @param suscripcionId ID de la suscripción.
     * @return Lista de movimientos de puntos (acumulaciones y canjes).
     * @throws SuscripcionNoEncontradaException si la suscripción no existe.
     */
    public List<MovimientoPuntos> consultarHistorial(String suscripcionId) {
        obtenerSuscripcionOLanzarExcepcion(suscripcionId);
        return puntosRepository.obtenerPorSuscripcionId(suscripcionId);
    }

    /**
     * HU-10: Calcula el total de puntos acumulados de una suscripción.
     *
     * @param suscripcionId ID de la suscripción.
     * @return Total de puntos acumulados por pedidos.
     */
    public int obtenerTotalAcumulado(String suscripcionId) {
        return puntosRepository.obtenerPorSuscripcionId(suscripcionId).stream()
                .filter(m -> m.getTipo() == TipoMovimiento.ACUMULACION)
                .mapToInt(MovimientoPuntos::getCantidad)
                .sum();
    }

    /**
     * HU-10: Calcula el total de puntos canjeados de una suscripción.
     *
     * @param suscripcionId ID de la suscripción.
     * @return Total de puntos canjeados por recompensas.
     */
    public int obtenerTotalCanjeado(String suscripcionId) {
        return puntosRepository.obtenerPorSuscripcionId(suscripcionId).stream()
                .filter(m -> m.getTipo() == TipoMovimiento.REDENCION)
                .mapToInt(MovimientoPuntos::getCantidad)
                .sum();
    }

    // ── Métodos auxiliares privados ───────────────────────────────────────────

    private Suscripcion obtenerSuscripcionActivaOLanzarExcepcion(String suscripcionId) {
        Suscripcion suscripcion = obtenerSuscripcionOLanzarExcepcion(suscripcionId);
        if (!suscripcion.estaActiva()) {
            throw new SuscripcionInactivaException(suscripcionId);
        }
        return suscripcion;
    }

    private Suscripcion obtenerSuscripcionOLanzarExcepcion(String suscripcionId) {
        return suscripcionRepository.buscarPorId(suscripcionId)
                .orElseThrow(() -> new SuscripcionNoEncontradaException(suscripcionId));
    }

    private String generarId() {
        return UUID.randomUUID().toString();
    }
}