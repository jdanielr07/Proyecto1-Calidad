package com.quickbite.repository;

import com.quickbite.model.Suscripcion;
import java.util.List;
import java.util.Optional;

public interface SuscripcionRepository {
    void guardar(Suscripcion suscripcion);
    Optional<Suscripcion> buscarPorId(String id);
    List<Suscripcion> obtenerTodas();
    boolean existePorId(String id);
}
