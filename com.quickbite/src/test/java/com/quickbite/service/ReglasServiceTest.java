package com.quickbite.service;

import com.quickbite.model.ReglaAcumulacion;
import com.quickbite.model.ReglaRedencion;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.List;
import java.util.Optional;

public class ReglasServiceTest {

    private ReglasService reglasService;

    @BeforeMethod
    public void setUp() {
        reglasService = new ReglasService();
    }

    // HU-6
    @Test(description = "QB-19 | HU-6 - Exito: Definir regla de acumulacion valida debe crearla activa")
    public void definirReglaAcumulacion_cuandoDatosValidos_debeCrearReglaActiva() {
        // Arrange & Act
        ReglaAcumulacion resultado = reglasService.definirReglaAcumulacion("RA-001", "10 puntos por pedido", 10);
        // Assert
        Assert.assertNotNull(resultado);
        Assert.assertEquals(resultado.getId(), "RA-001");
        Assert.assertTrue(resultado.isActiva());
    }

    @Test(description = "QB-20 | HU-6 - Fallo: Definir regla con puntos cero debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaAcumulacion_cuandoPuntosCero_debeLanzarExcepcion() {
        reglasService.definirReglaAcumulacion("RA-001", "Invalida", 0);
    }

    @Test(description = "QB-21 | HU-6 - Fallo: Definir regla de acumulacion con ID duplicado debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaAcumulacion_cuandoIdDuplicado_debeLanzarExcepcion() {
        // Arrange
        reglasService.definirReglaAcumulacion("RA-001", "Estandar", 10);
        // Act
        reglasService.definirReglaAcumulacion("RA-001", "Duplicada", 20);
    }

    @Test(description = "QB-22 | HU-6 - Exito: Lista de reglas de acumulacion contiene la regla registrada")
    public void obtenerReglasAcumulacion_cuandoExisteUnaRegla_debeRetornarListaConUnElemento() {
        // Arrange
        reglasService.definirReglaAcumulacion("RA-001", "10 puntos por pedido", 10);
        // Act
        List<ReglaAcumulacion> reglas = reglasService.obtenerReglasAcumulacion();
        // Assert
        Assert.assertEquals(reglas.size(), 1);
        Assert.assertEquals(reglas.get(0).getId(), "RA-001");
    }

    @Test(description = "QB-23 | HU-6 - Exito: Regla de acumulacion activa es recuperada correctamente")
    public void obtenerReglaAcumulacionActiva_cuandoExisteReglaActiva_debeRetornarla() {
        // Arrange
        reglasService.definirReglaAcumulacion("RA-001", "10 puntos por pedido", 10);
        // Act & Assert
        Assert.assertTrue(reglasService.obtenerReglaAcumulacionActiva().isPresent());
    }

    @Test(description = "QB-24 | HU-6 - Fallo: Sin reglas de acumulacion retorna Optional vacio")
    public void obtenerReglaAcumulacionActiva_cuandoNoHayReglas_debeRetornarOptionalVacio() {
        Assert.assertFalse(reglasService.obtenerReglaAcumulacionActiva().isPresent());
    }

    // HU-7
    @Test(description = "QB-25 | HU-7 - Exito: Definir regla de redencion valida debe crearla activa")
    public void definirReglaRedencion_cuandoDatosValidos_debeCrearReglaActiva() {
        // Arrange & Act
        ReglaRedencion resultado = reglasService.definirReglaRedencion("RR-001", "Minimo 100 puntos", 100);
        // Assert
        Assert.assertNotNull(resultado);
        Assert.assertEquals(resultado.getId(), "RR-001");
        Assert.assertTrue(resultado.isActiva());
    }

    @Test(description = "QB-26 | HU-7 - Fallo: Definir regla de redencion con puntos negativos debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaRedencion_cuandoPuntosNegativos_debeLanzarExcepcion() {
        reglasService.definirReglaRedencion("RR-001", "Invalida", -50);
    }

    @Test(description = "QB-27 | HU-7 - Fallo: Definir regla de redencion con ID duplicado debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaRedencion_cuandoIdDuplicado_debeLanzarExcepcion() {
        // Arrange
        reglasService.definirReglaRedencion("RR-001", "Estandar", 100);
        // Act
        reglasService.definirReglaRedencion("RR-001", "Duplicada", 200);
    }

    @Test(description = "QB-31 | HU-7 - Exito: Regla de redencion activa es recuperada correctamente")
    public void obtenerReglaRedencionActiva_cuandoExisteReglaActiva_debeRetornarla() {
        // Arrange
        reglasService.definirReglaRedencion("RR-001", "Minimo 100 puntos", 100);
        // Act & Assert
        Assert.assertTrue(reglasService.obtenerReglaRedencionActiva().isPresent());
    }

    @Test(description = "QB-32 | HU-7 - Fallo: Sin reglas de redencion retorna Optional vacio")
    public void obtenerReglaRedencionActiva_cuandoNoHayReglas_debeRetornarOptionalVacio() {
        Assert.assertFalse(reglasService.obtenerReglaRedencionActiva().isPresent());
    }
}