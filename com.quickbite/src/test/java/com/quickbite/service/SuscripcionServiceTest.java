package com.quickbite.service;

import com.quickbite.exception.SuscripcionNoEncontradaException;
import com.quickbite.mock.SuscripcionRepositoryMock;
import com.quickbite.model.EstadoSuscripcion;
import com.quickbite.model.Suscripcion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.List;

public class SuscripcionServiceTest {

    private SuscripcionService suscripcionService;
    private SuscripcionRepositoryMock repositoryMock;

    @BeforeMethod
    public void setUp() {
        repositoryMock = new SuscripcionRepositoryMock();
        suscripcionService = new SuscripcionService(repositoryMock);
    }

    // HU-1
    @Test(description = "QB-01 | HU-1 - Exito: Registrar suscripcion con datos validos debe retornar suscripcion ACTIVA")
    public void registrarSuscripcion_cuandoDatosValidos_debeRetornarSuscripcionActiva() {
        // Arrange
        String id = "SUB-001"; String clienteId = "CLI-001";
        // Act
        Suscripcion resultado = suscripcionService.registrarSuscripcion(id, clienteId);
        // Assert
        Assert.assertNotNull(resultado);
        Assert.assertEquals(resultado.getId(), id);
        Assert.assertEquals(resultado.getEstado(), EstadoSuscripcion.ACTIVA);
        Assert.assertEquals(resultado.getPuntosDeLealtad(), 0);
    }

    @Test(description = "QB-02 | HU-1 - Fallo: Registrar suscripcion con ID duplicado debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void registrarSuscripcion_cuandoIdDuplicado_debeLanzarExcepcion() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        // Act
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-002");
    }

    @Test(description = "QB-03 | HU-1 - Fallo: Registrar suscripcion con ID nulo debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void registrarSuscripcion_cuandoIdNulo_debeLanzarExcepcion() {
        suscripcionService.registrarSuscripcion(null, "CLI-001");
    }

    @Test(description = "QB-04 | HU-1 - Fallo: Registrar suscripcion con clienteId vacio debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void registrarSuscripcion_cuandoClienteIdVacio_debeLanzarExcepcion() {
        suscripcionService.registrarSuscripcion("SUB-001", "   ");
    }

    // HU-2
    @Test(description = "QB-05 | HU-2 - Exito: Activar suscripcion inactiva debe cambiar estado a ACTIVA")
    public void activarSuscripcion_cuandoSuscripcionInactiva_debeQuedarActiva() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        suscripcionService.desactivarSuscripcion("SUB-001");
        // Act
        suscripcionService.activarSuscripcion("SUB-001");
        // Assert
        Assert.assertEquals(suscripcionService.consultarEstado("SUB-001"), EstadoSuscripcion.ACTIVA);
    }

    @Test(description = "QB-06 | HU-2 - Fallo: Activar suscripcion inexistente debe lanzar excepcion",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void activarSuscripcion_cuandoNoExiste_debeLanzarExcepcion() {
        suscripcionService.activarSuscripcion("SUB-INEXISTENTE");
    }

    @Test(description = "QB-07 | HU-2 - Exito: Desactivar suscripcion activa debe cambiar estado a INACTIVA")
    public void desactivarSuscripcion_cuandoSuscripcionActiva_debeQuedarInactiva() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        // Act
        suscripcionService.desactivarSuscripcion("SUB-001");
        // Assert
        Assert.assertEquals(suscripcionService.consultarEstado("SUB-001"), EstadoSuscripcion.INACTIVA);
    }

    @Test(description = "QB-08 | HU-2 - Fallo: Desactivar suscripcion inexistente debe lanzar excepcion",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void desactivarSuscripcion_cuandoNoExiste_debeLanzarExcepcion() {
        suscripcionService.desactivarSuscripcion("SUB-INEXISTENTE");
    }

    // HU-3
    @Test(description = "QB-09 | HU-3 - Exito: Consultar estado de suscripcion activa debe retornar ACTIVA")
    public void consultarEstado_cuandoSuscripcionActiva_debeRetornarActiva() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        // Act & Assert
        Assert.assertEquals(suscripcionService.consultarEstado("SUB-001"), EstadoSuscripcion.ACTIVA);
    }

    @Test(description = "QB-10 | HU-3 - Fallo: Consultar estado de suscripcion inexistente debe lanzar excepcion",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void consultarEstado_cuandoSuscripcionNoExiste_debeLanzarExcepcion() {
        suscripcionService.consultarEstado("SUB-INEXISTENTE");
    }

    // HU-9
    @Test(description = "QB-33 | HU-9 - Exito: Reporte de activas filtra correctamente")
    public void obtenerSuscripcionesActivas_cuandoExistenVarias_debeRetornarSoloActivas() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        suscripcionService.registrarSuscripcion("SUB-002", "CLI-002");
        suscripcionService.registrarSuscripcion("SUB-003", "CLI-003");
        suscripcionService.desactivarSuscripcion("SUB-003");
        // Act & Assert
        Assert.assertEquals(suscripcionService.obtenerSuscripcionesActivas().size(), 2);
    }

    @Test(description = "QB-34 | HU-9 - Exito: Reporte de inactivas filtra correctamente")
    public void obtenerSuscripcionesInactivas_cuandoExistenVarias_debeRetornarSoloInactivas() {
        // Arrange
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        suscripcionService.registrarSuscripcion("SUB-002", "CLI-002");
        suscripcionService.registrarSuscripcion("SUB-003", "CLI-003");
        suscripcionService.desactivarSuscripcion("SUB-002");
        suscripcionService.desactivarSuscripcion("SUB-003");
        // Act & Assert
        Assert.assertEquals(suscripcionService.obtenerSuscripcionesInactivas().size(), 2);
    }

    @Test(description = "QB-35 | HU-9 - Fallo: Reporte de activas con repositorio vacio debe retornar lista vacia")
    public void obtenerSuscripcionesActivas_cuandoRepositorioVacio_debeRetornarListaVacia() {
        // Act
        List<Suscripcion> activas = suscripcionService.obtenerSuscripcionesActivas();
        // Assert
        Assert.assertNotNull(activas);
        Assert.assertTrue(activas.isEmpty());
    }
}