package com.quickbite.service;

import com.quickbite.exception.SuscripcionNoEncontradaException;
import com.quickbite.mock.SuscripcionRepositoryMock;
import com.quickbite.model.EstadoSuscripcion;
import com.quickbite.model.Suscripcion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Pruebas unitarias para SuscripcionService — QuickBite.
 * Cubre: HU-1, HU-2, HU-3, HU-9
 * Patrón: AAA (Arrange - Act - Assert)
 */
public class SuscripcionServiceTest {

    private SuscripcionService suscripcionService;
    private SuscripcionRepositoryMock repositoryMock;

    @BeforeMethod
    public void setUp() {
        repositoryMock = new SuscripcionRepositoryMock();
        suscripcionService = new SuscripcionService(repositoryMock);
    }

    // =========================================================
    // HU-1: Registrar nueva suscripción
    // =========================================================

    @Test(description = "HU1 - Éxito: Registrar suscripción con datos válidos debe retornar suscripción ACTIVA")
    public void registrarSuscripcion_cuandoDatosValidos_debeRetornarSuscripcionActiva() {
        // Arrange
        String id = "SUB-001";
        String clienteId = "CLI-001";

        // Act
        Suscripcion resultado = suscripcionService.registrarSuscripcion(id, clienteId);

        // Assert
        Assert.assertNotNull(resultado, "La suscripción no debe ser nula");
        Assert.assertEquals(resultado.getId(), id, "El ID debe coincidir");
        Assert.assertEquals(resultado.getClienteId(), clienteId, "El clienteId debe coincidir");
        Assert.assertEquals(resultado.getEstado(), EstadoSuscripcion.ACTIVA, "Debe crearse ACTIVA");
        Assert.assertEquals(resultado.getPuntosDeLealtad(), 0, "Debe iniciar con 0 puntos de lealtad");
    }

    @Test(description = "HU1 - Fallo: Registrar suscripción con ID duplicado debe lanzar excepción",
          expectedExceptions = IllegalArgumentException.class)
    public void registrarSuscripcion_cuandoIdDuplicado_debeLanzarExcepcion() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");

        // Act (debe lanzar excepción)
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-002");

        // Assert - manejado por expectedExceptions
    }

    @Test(description = "HU1 - Fallo: Registrar suscripción con ID nulo debe lanzar excepción",
          expectedExceptions = IllegalArgumentException.class)
    public void registrarSuscripcion_cuandoIdNulo_debeLanzarExcepcion() {
        // Arrange
        String idNulo = null;

        // Act (debe lanzar excepción)
        suscripcionService.registrarSuscripcion(idNulo, "CLI-001");

        // Assert - manejado por expectedExceptions
    }

    @Test(description = "HU1 - Fallo: Registrar suscripción con clienteId vacío debe lanzar excepción",
          expectedExceptions = IllegalArgumentException.class)
    public void registrarSuscripcion_cuandoClienteIdVacio_debeLanzarExcepcion() {
        // Arrange
        String clienteIdVacio = "   ";

        // Act (debe lanzar excepción)
        suscripcionService.registrarSuscripcion("SUB-001", clienteIdVacio);

        // Assert - manejado por expectedExceptions
    }

    // =========================================================
    // HU-2: Activar suscripción
    // =========================================================

    @Test(description = "HU2 - Éxito: Activar suscripción inactiva debe cambiar estado a ACTIVA")
    public void activarSuscripcion_cuandoSuscripcionInactiva_debeQuedarActiva() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        suscripcionService.desactivarSuscripcion("SUB-001");

        // Act
        suscripcionService.activarSuscripcion("SUB-001");

        // Assert
        EstadoSuscripcion estado = suscripcionService.consultarEstado("SUB-001");
        Assert.assertEquals(estado, EstadoSuscripcion.ACTIVA, "La suscripción debe quedar ACTIVA");
    }

    @Test(description = "HU2 - Fallo: Activar suscripción inexistente debe lanzar excepción",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void activarSuscripcion_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange - repositorio vacío

        // Act (debe lanzar excepción)
        suscripcionService.activarSuscripcion("SUB-INEXISTENTE");

        // Assert - manejado por expectedExceptions
    }

    // =========================================================
    // HU-2: Desactivar suscripción
    // =========================================================

    @Test(description = "HU2 - Éxito: Desactivar suscripción activa debe cambiar estado a INACTIVA")
    public void desactivarSuscripcion_cuandoSuscripcionActiva_debeQuedarInactiva() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");

        // Act
        suscripcionService.desactivarSuscripcion("SUB-001");

        // Assert
        EstadoSuscripcion estado = suscripcionService.consultarEstado("SUB-001");
        Assert.assertEquals(estado, EstadoSuscripcion.INACTIVA, "La suscripción debe quedar INACTIVA");
    }

    @Test(description = "HU2 - Fallo: Desactivar suscripción inexistente debe lanzar excepción",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void desactivarSuscripcion_cuandoNoExiste_debeLanzarExcepcion() {
        // Arrange - repositorio vacío

        // Act (debe lanzar excepción)
        suscripcionService.desactivarSuscripcion("SUB-INEXISTENTE");

        // Assert - manejado por expectedExceptions
    }

    // =========================================================
    // HU-3: Consultar estado de suscripción
    // =========================================================

    @Test(description = "HU3 - Éxito: Consultar estado de suscripción activa debe retornar ACTIVA")
    public void consultarEstado_cuandoSuscripcionActiva_debeRetornarActiva() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");

        // Act
        EstadoSuscripcion estado = suscripcionService.consultarEstado("SUB-001");

        // Assert
        Assert.assertEquals(estado, EstadoSuscripcion.ACTIVA, "El estado debe ser ACTIVA");
    }

    @Test(description = "HU3 - Fallo: Consultar estado de suscripción inexistente debe lanzar excepción",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void consultarEstado_cuandoSuscripcionNoExiste_debeLanzarExcepcion() {
        // Arrange - repositorio vacío

        // Act (debe lanzar excepción)
        suscripcionService.consultarEstado("SUB-INEXISTENTE");

        // Assert - manejado por expectedExceptions
    }

    // =========================================================
    // HU-9: Reporte de suscripciones activas e inactivas
    // =========================================================

    @Test(description = "HU9 - Éxito: Reporte de activas filtra correctamente")
    public void obtenerSuscripcionesActivas_cuandoExistenVarias_debeRetornarSoloActivas() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001"); // ACTIVA
        suscripcionService.registrarSuscripcion("SUB-002", "CLI-002"); // ACTIVA
        suscripcionService.registrarSuscripcion("SUB-003", "CLI-003"); // será INACTIVA
        suscripcionService.desactivarSuscripcion("SUB-003");

        // Act
        List<Suscripcion> activas = suscripcionService.obtenerSuscripcionesActivas();

        // Assert
        Assert.assertEquals(activas.size(), 2, "Deben existir exactamente 2 suscripciones activas");
    }

    @Test(description = "HU9 - Éxito: Reporte de inactivas filtra correctamente")
    public void obtenerSuscripcionesInactivas_cuandoExistenVarias_debeRetornarSoloInactivas() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001"); // ACTIVA
        suscripcionService.registrarSuscripcion("SUB-002", "CLI-002"); // será INACTIVA
        suscripcionService.registrarSuscripcion("SUB-003", "CLI-003"); // será INACTIVA
        suscripcionService.desactivarSuscripcion("SUB-002");
        suscripcionService.desactivarSuscripcion("SUB-003");

        // Act
        List<Suscripcion> inactivas = suscripcionService.obtenerSuscripcionesInactivas();

        // Assert
        Assert.assertEquals(inactivas.size(), 2, "Deben existir exactamente 2 suscripciones inactivas");
    }

    @Test(description = "HU9 - Fallo: Reporte de activas con repositorio vacío debe retornar lista vacía")
    public void obtenerSuscripcionesActivas_cuandoRepositorioVacio_debeRetornarListaVacia() {
        // Arrange - repositorio vacío

        // Act
        List<Suscripcion> activas = suscripcionService.obtenerSuscripcionesActivas();

        // Assert
        Assert.assertNotNull(activas, "La lista no debe ser nula");
        Assert.assertTrue(activas.isEmpty(), "La lista debe estar vacía");
    }
}