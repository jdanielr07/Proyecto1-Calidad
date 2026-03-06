package com.quickbite.mock;

import com.quickbite.model.MovimientoPuntos;
import com.quickbite.repository.PuntosRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock manual de PuntosRepository para pruebas unitarias de QuickBite.
 * Simula una base de datos en memoria usando un ArrayList.
 */
public class PuntosRepositoryMock implements PuntosRepository {

    private final List<MovimientoPuntos> almacenamiento = new ArrayList<>();

    @Override
    public void guardar(MovimientoPuntos movimiento) {
        almacenamiento.add(movimiento);
    }

    @Override
    public List<MovimientoPuntos> obtenerPorSuscripcionId(String suscripcionId) {
        return almacenamiento.stream()
                .filter(m -> m.getSuscripcionId().equals(suscripcionId))
                .collect(Collectors.toList());
    }

    /** Limpia todos los datos almacenados (se usa en @BeforeMethod). */
    public void limpiar() {
        almacenamiento.clear();
    }
}