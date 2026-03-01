package com.membresias.service;

import com.membresias.exception.MembresiaNoEncontradaException;
import com.membresias.model.EstadoMembresia;
import com.membresias.model.Membresia;
import com.membresias.repository.MembresiaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio que gestiona las operaciones sobre membresías.
 * HU 1 - Registrar membresía
 * HU 2 - Activar / Desactivar membresía
 * HU 3 - Consultar estado de membresía
 * HU 9 - Reporte de membresías activas e inactivas
 */
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;

    public MembresiaService(MembresiaRepository membresiaRepository) {
        this.membresiaRepository = membresiaRepository;
    }

    /**
     * HU 1 - Registra una nueva membresía para un usuario.
     *
     * @param id        Identificador único de la membresía.
     * @param usuarioId Identificador del usuario propietario.
     * @return La membresía recién creada.
     * @throws IllegalArgumentException si el ID o el usuarioId son nulos o vacíos.
     */
    public Membresia registrarMembresia(String id, String usuarioId) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de membresía no puede ser nulo o vacío.");
        }
        if (usuarioId == null || usuarioId.isBlank()) {
            throw new IllegalArgumentException("El ID de usuario no puede ser nulo o vacío.");
        }
        if (membresiaRepository.existePorId(id)) {
            throw new IllegalArgumentException("Ya existe una membresía con el ID: " + id);
        }

        Membresia membresia = new Membresia(id, usuarioId, LocalDate.now());
        membresiaRepository.guardar(membresia);
        return membresia;
    }

    /**
     * HU 2 - Activa una membresía existente.
     *
     * @param membresiaId ID de la membresía a activar.
     * @throws MembresiaNoEncontradaException si no existe la membresía.
     */
    public void activarMembresia(String membresiaId) {
        Membresia membresia = obtenerMembresiaOLanzarExcepcion(membresiaId);
        membresia.setEstado(EstadoMembresia.ACTIVA);
        membresiaRepository.guardar(membresia);
    }

    /**
     * HU 2 - Desactiva una membresía existente.
     *
     * @param membresiaId ID de la membresía a desactivar.
     * @throws MembresiaNoEncontradaException si no existe la membresía.
     */
    public void desactivarMembresia(String membresiaId) {
        Membresia membresia = obtenerMembresiaOLanzarExcepcion(membresiaId);
        membresia.setEstado(EstadoMembresia.INACTIVA);
        membresiaRepository.guardar(membresia);
    }

    /**
     * HU 3 - Consulta el estado actual de una membresía.
     *
     * @param membresiaId ID de la membresía a consultar.
     * @return El estado de la membresía (ACTIVA o INACTIVA).
     * @throws MembresiaNoEncontradaException si no existe la membresía.
     */
    public EstadoMembresia consultarEstado(String membresiaId) {
        Membresia membresia = obtenerMembresiaOLanzarExcepcion(membresiaId);
        return membresia.getEstado();
    }

    /**
     * HU 9 - Retorna la lista de membresías activas.
     *
     * @return Lista de membresías con estado ACTIVA.
     */
    public List<Membresia> obtenerMembresiasActivas() {
        return membresiaRepository.obtenerTodas().stream()
                .filter(Membresia::estaActiva)
                .collect(Collectors.toList());
    }

    /**
     * HU 9 - Retorna la lista de membresías inactivas.
     *
     * @return Lista de membresías con estado INACTIVA.
     */
    public List<Membresia> obtenerMembresiasInactivas() {
        return membresiaRepository.obtenerTodas().stream()
                .filter(m -> !m.estaActiva())
                .collect(Collectors.toList());
    }

    // --- Método auxiliar privado ---

    private Membresia obtenerMembresiaOLanzarExcepcion(String membresiaId) {
        return membresiaRepository.buscarPorId(membresiaId)
                .orElseThrow(() -> new MembresiaNoEncontradaException(membresiaId));
    }
}