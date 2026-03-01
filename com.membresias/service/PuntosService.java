package com.membresias.service;

import com.membresias.exception.MembresiaInactivaException;
import com.membresias.exception.MembresiaNoEncontradaException;
import com.membresias.exception.PuntosInsuficientesException;
import com.membresias.model.*;
import com.membresias.repository.MembresiaRepository;
import com.membresias.repository.PuntosRepository;

import java.util.List;
import java.util.UUID;

/**
 * Servicio que gestiona los movimientos de puntos de las membresías.
 * HU 4 - Acumular puntos
 * HU 5 - Redimir puntos por beneficios
 * HU 8 - Consultar historial de movimientos
 * HU 10 - Reporte de puntos acumulados y redimidos
 */
public class PuntosService {

    private final MembresiaRepository membresiaRepository;
    private final PuntosRepository puntosRepository;

    public PuntosService(MembresiaRepository membresiaRepository, PuntosRepository puntosRepository) {
        this.membresiaRepository = membresiaRepository;
        this.puntosRepository = puntosRepository;
    }

    /**
     * HU 4 - Acumula puntos en una membresía activa.
     *
     * @param membresiaId ID de la membresía.
     * @param cantidad    Cantidad de puntos a acumular (debe ser mayor a 0).
     * @param descripcion Descripción del motivo de acumulación.
     * @return El movimiento de puntos registrado.
     * @throws MembresiaNoEncontradaException si la membresía no existe.
     * @throws MembresiaInactivaException     si la membresía está inactiva.
     * @throws IllegalArgumentException       si la cantidad es inválida.
     */
    public Punto acumularPuntos(String membresiaId, int cantidad, String descripcion) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos a acumular debe ser mayor a 0.");
        }
        Membresia membresia = obtenerMembresiaActivaOLanzarExcepcion(membresiaId);

        membresia.setPuntosAcumulados(membresia.getPuntosAcumulados() + cantidad);
        membresiaRepository.guardar(membresia);

        Punto punto = new Punto(generarId(), membresiaId, TipoMovimiento.ACUMULACION, cantidad, descripcion);
        puntosRepository.guardar(punto);
        return punto;
    }

    /**
     * HU 5 - Redime puntos de una membresía para obtener un beneficio.
     *
     * @param membresiaId ID de la membresía.
     * @param beneficio   Beneficio que se desea redimir.
     * @return El movimiento de redención registrado.
     * @throws MembresiaNoEncontradaException si la membresía no existe.
     * @throws MembresiaInactivaException     si la membresía está inactiva.
     * @throws PuntosInsuficientesException   si no hay suficientes puntos.
     * @throws IllegalArgumentException       si el beneficio no está disponible.
     */
    public Punto redimirPuntos(String membresiaId, Beneficio beneficio) {
        if (beneficio == null || !beneficio.isDisponible()) {
            throw new IllegalArgumentException("El beneficio no está disponible o es nulo.");
        }
        Membresia membresia = obtenerMembresiaActivaOLanzarExcepcion(membresiaId);

        int puntosDisponibles = membresia.getPuntosAcumulados();
        int puntosRequeridos = beneficio.getPuntosRequeridos();

        if (puntosDisponibles < puntosRequeridos) {
            throw new PuntosInsuficientesException(puntosDisponibles, puntosRequeridos);
        }

        membresia.setPuntosAcumulados(puntosDisponibles - puntosRequeridos);
        membresiaRepository.guardar(membresia);

        Punto punto = new Punto(generarId(), membresiaId, TipoMovimiento.REDENCION,
                puntosRequeridos, "Redención: " + beneficio.getNombre());
        puntosRepository.guardar(punto);
        return punto;
    }

    /**
     * HU 8 - Obtiene el historial de movimientos de puntos de una membresía.
     *
     * @param membresiaId ID de la membresía.
     * @return Lista de movimientos de puntos.
     * @throws MembresiaNoEncontradaException si la membresía no existe.
     */
    public List<Punto> consultarHistorial(String membresiaId) {
        obtenerMembresiaOLanzarExcepcion(membresiaId);
        return puntosRepository.obtenerPorMembresiaId(membresiaId);
    }

    /**
     * HU 10 - Calcula el total de puntos acumulados de una membresía.
     *
     * @param membresiaId ID de la membresía.
     * @return Total de puntos acumulados.
     */
    public int obtenerTotalAcumulado(String membresiaId) {
        return puntosRepository.obtenerPorMembresiaId(membresiaId).stream()
                .filter(p -> p.getTipo() == TipoMovimiento.ACUMULACION)
                .mapToInt(Punto::getCantidad)
                .sum();
    }

    /**
     * HU 10 - Calcula el total de puntos redimidos de una membresía.
     *
     * @param membresiaId ID de la membresía.
     * @return Total de puntos redimidos.
     */
    public int obtenerTotalRedimido(String membresiaId) {
        return puntosRepository.obtenerPorMembresiaId(membresiaId).stream()
                .filter(p -> p.getTipo() == TipoMovimiento.REDENCION)
                .mapToInt(Punto::getCantidad)
                .sum();
    }

    // --- Métodos auxiliares privados ---

    private Membresia obtenerMembresiaActivaOLanzarExcepcion(String membresiaId) {
        Membresia membresia = obtenerMembresiaOLanzarExcepcion(membresiaId);
        if (!membresia.estaActiva()) {
            throw new MembresiaInactivaException(membresiaId);
        }
        return membresia;
    }

    private Membresia obtenerMembresiaOLanzarExcepcion(String membresiaId) {
        return membresiaRepository.buscarPorId(membresiaId)
                .orElseThrow(() -> new MembresiaNoEncontradaException(membresiaId));
    }

    private String generarId() {
        return UUID.randomUUID().toString();
    }
}