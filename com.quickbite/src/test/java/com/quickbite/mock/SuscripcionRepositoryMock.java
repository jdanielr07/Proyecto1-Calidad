package com.quickbite.mock;

import com.quickbite.model.Suscripcion;
import com.quickbite.repository.SuscripcionRepository;

import java.util.*;

/**
 * Mock manual de SuscripcionRepository para pruebas unitarias de QuickBite.
 * Simula una base de datos en memoria usando un HashMap.
 */
public class SuscripcionRepositoryMock implements SuscripcionRepository {

    private final Map<String, Suscripcion> almacenamiento = new HashMap<>();

    @Override
    public void guardar(Suscripcion suscripcion) {
        almacenamiento.put(suscripcion.getId(), suscripcion);
    }

    @Override
    public Optional<Suscripcion> buscarPorId(String id) {
        return Optional.ofNullable(almacenamiento.get(id));
    }

    @Override
    public List<Suscripcion> obtenerTodas() {
        return new ArrayList<>(almacenamiento.values());
    }

    @Override
    public boolean existePorId(String id) {
        return almacenamiento.containsKey(id);
    }

    /** Limpia todos los datos almacenados (se usa en @BeforeMethod). */
    public void limpiar() {
        almacenamiento.clear();
    }
}