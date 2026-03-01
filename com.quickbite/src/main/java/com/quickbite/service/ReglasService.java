package com.quickbite.service;

import com.quickbite.model.ReglaAcumulacion;
import com.quickbite.model.ReglaRedencion;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que gestiona las reglas de acumulación y canje de puntos de lealtad en QuickBite.
 *
 * HU-6: Definir reglas de acumulación (cuántos puntos se otorgan por pedido)
 * HU-7: Definir reglas de redención (mínimo de puntos para poder canjear recompensas)
 */
public class ReglasService {

    private final List<ReglaAcumulacion> reglasAcumulacion = new ArrayList<>();
    private final List<ReglaRedencion> reglasRedencion = new ArrayList<>();

    /**
     * HU-6: Define una nueva regla de acumulación de puntos para QuickBite.
     * Ejemplo: otorgar 10 puntos por cada pedido completado.
     *
     * @param id              Identificador único de la regla.
     * @param descripcion     Descripción de la regla.
     * @param puntosPorPedido Puntos que se otorgan por cada pedido realizado.
     * @return La regla de acumulación creada.
     * @throws IllegalArgumentException si los parámetros son inválidos o el ID está duplicado.
     */
    public ReglaAcumulacion definirReglaAcumulacion(String id, String descripcion, int puntosPorPedido) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de la regla no puede ser nulo o vacío.");
        }
        if (puntosPorPedido <= 0) {
            throw new IllegalArgumentException("Los puntos por pedido deben ser mayores a 0.");
        }
        if (existeReglaAcumulacion(id)) {
            throw new IllegalArgumentException("Ya existe una regla de acumulación con el ID: " + id);
        }

        ReglaAcumulacion regla = new ReglaAcumulacion(id, descripcion, puntosPorPedido);
        reglasAcumulacion.add(regla);
        return regla;
    }

    /**
     * HU-7: Define una nueva regla de redención de puntos para QuickBite.
     * Ejemplo: el cliente necesita al menos 100 puntos para poder canjear una recompensa.
     *
     * @param id                     Identificador único de la regla.
     * @param descripcion            Descripción de la regla.
     * @param puntosMinimosRedencion Mínimo de puntos necesarios para poder canjear.
     * @return La regla de redención creada.
     * @throws IllegalArgumentException si los parámetros son inválidos o el ID está duplicado.
     */
    public ReglaRedencion definirReglaRedencion(String id, String descripcion, int puntosMinimosRedencion) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID de la regla no puede ser nulo o vacío.");
        }
        if (puntosMinimosRedencion <= 0) {
            throw new IllegalArgumentException("Los puntos mínimos para redención deben ser mayores a 0.");
        }
        if (existeReglaRedencion(id)) {
            throw new IllegalArgumentException("Ya existe una regla de redención con el ID: " + id);
        }

        ReglaRedencion regla = new ReglaRedencion(id, descripcion, puntosMinimosRedencion);
        reglasRedencion.add(regla);
        return regla;
    }

    /**
     * Retorna todas las reglas de acumulación registradas en QuickBite.
     */
    public List<ReglaAcumulacion> obtenerReglasAcumulacion() {
        return new ArrayList<>(reglasAcumulacion);
    }

    /**
     * Retorna todas las reglas de redención registradas en QuickBite.
     */
    public List<ReglaRedencion> obtenerReglasRedencion() {
        return new ArrayList<>(reglasRedencion);
    }

    /**
     * Retorna la regla de acumulación activa (la primera que esté activa).
     */
    public Optional<ReglaAcumulacion> obtenerReglaAcumulacionActiva() {
        return reglasAcumulacion.stream()
                .filter(ReglaAcumulacion::isActiva)
                .findFirst();
    }

    /**
     * Retorna la regla de redención activa (la primera que esté activa).
     */
    public Optional<ReglaRedencion> obtenerReglaRedencionActiva() {
        return reglasRedencion.stream()
                .filter(ReglaRedencion::isActiva)
                .findFirst();
    }

    // ── Métodos auxiliares ────────────────────────────────────────────────────

    private boolean existeReglaAcumulacion(String id) {
        return reglasAcumulacion.stream().anyMatch(r -> r.getId().equals(id));
    }

    private boolean existeReglaRedencion(String id) {
        return reglasRedencion.stream().anyMatch(r -> r.getId().equals(id));
    }
}