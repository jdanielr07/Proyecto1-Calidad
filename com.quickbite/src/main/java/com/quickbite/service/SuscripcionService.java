package com.quickbite.service;

import com.quickbite.exception.SuscripcionNoEncontradaException;
import com.quickbite.model.EstadoSuscripcion;
import com.quickbite.model.Suscripcion;
import com.quickbite.repository.SuscripcionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las suscripciones al programa de lealtad de QuickBite.
 *
 * HU-1: Registrar nueva suscripción
 * HU-2: Activar / Desactivar suscripción
 * HU-3: Consultar estado de suscripción
 * HU-9: Reporte de suscripciones activas e inactivas
 */
public class SuscripcionService {

    private final SuscripcionRepository suscripcionRepository;

    public SuscripcionService(SuscripcionRepository suscripcionRepository) {
        this.suscripcionRepository = suscripcionRepository;
    }

    /**
     * HU-1: Registra una nueva suscripción al programa de lealtad de QuickBite.
     *
     * @param id        Identificador único de la suscripción.
     * @param clienteId Identificador del cliente que se suscribe.
     * @return La suscripción recién creada en estado ACTIVA.
     * @throws IllegalArgumentException si el ID o el clienteId son nulos, vacíos o duplicados.
     */
    public Suscripcion registrarSuscripcion(String id, String clienteId) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de suscripción no puede ser nulo o vacío.");
        }
        if (clienteId == null || clienteId.isBlank()) {
            throw new IllegalArgumentException("El ID de cliente no puede ser nulo o vacío.");
        }
        if (suscripcionRepository.existePorId(id)) {
            throw new IllegalArgumentException("Ya existe una suscripción con el ID: " + id);
        }

        Suscripcion suscripcion = new Suscripcion(id, clienteId, LocalDate.now());
        suscripcionRepository.guardar(suscripcion);
        return suscripcion;
    }

    /**
     * HU-2: Activa una suscripción existente en QuickBite.
     *
     * @param suscripcionId ID de la suscripción a activar.
     * @throws SuscripcionNoEncontradaException si no existe la suscripción.
     */
    public void activarSuscripcion(String suscripcionId) {
        Suscripcion suscripcion = obtenerSuscripcionOLanzarExcepcion(suscripcionId);
        suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
        suscripcionRepository.guardar(suscripcion);
    }

    /**
     * HU-2: Desactiva una suscripción existente en QuickBite.
     *
     * @param suscripcionId ID de la suscripción a desactivar.
     * @throws SuscripcionNoEncontradaException si no existe la suscripción.
     */
    public void desactivarSuscripcion(String suscripcionId) {
        Suscripcion suscripcion = obtenerSuscripcionOLanzarExcepcion(suscripcionId);
        suscripcion.setEstado(EstadoSuscripcion.INACTIVA);
        suscripcionRepository.guardar(suscripcion);
    }

    /**
     * HU-3: Consulta el estado actual de una suscripción.
     *
     * @param suscripcionId ID de la suscripción a consultar.
     * @return El estado de la suscripción (ACTIVA o INACTIVA).
     * @throws SuscripcionNoEncontradaException si no existe la suscripción.
     */
    public EstadoSuscripcion consultarEstado(String suscripcionId) {
        return obtenerSuscripcionOLanzarExcepcion(suscripcionId).getEstado();
    }

    /**
     * HU-9: Retorna la lista de suscripciones activas en QuickBite.
     *
     * @return Lista de suscripciones con estado ACTIVA.
     */
    public List<Suscripcion> obtenerSuscripcionesActivas() {
        return suscripcionRepository.obtenerTodas().stream()
                .filter(Suscripcion::estaActiva)
                .collect(Collectors.toList());
    }

    /**
     * HU-9: Retorna la lista de suscripciones inactivas en QuickBite.
     *
     * @return Lista de suscripciones con estado INACTIVA.
     */
    public List<Suscripcion> obtenerSuscripcionesInactivas() {
        return suscripcionRepository.obtenerTodas().stream()
                .filter(s -> !s.estaActiva())
                .collect(Collectors.toList());
    }

    // ── Método auxiliar privado ───────────────────────────────────────────────

    private Suscripcion obtenerSuscripcionOLanzarExcepcion(String suscripcionId) {
        return suscripcionRepository.buscarPorId(suscripcionId)
                .orElseThrow(() -> new SuscripcionNoEncontradaException(suscripcionId));
    }
}