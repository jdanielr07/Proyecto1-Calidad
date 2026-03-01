package com.quickbite.repository;

import com.quickbite.model.MovimientoPuntos;
import java.util.List;

public interface PuntosRepository {
    void guardar(MovimientoPuntos movimiento);
    List<MovimientoPuntos> obtenerPorSuscripcionId(String suscripcionId);
}
