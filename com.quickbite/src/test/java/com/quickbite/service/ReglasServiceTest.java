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

    @Test(description = "HU6 - Exito: Definir regla de acumulacion valida")
    public void definirReglaAcumulacion_cuandoDatosValidos_debeCrearReglaActiva() {
        ReglaAcumulacion resultado = reglasService.definirReglaAcumulacion("RA-001", "10 puntos por pedido", 10);
        Assert.assertNotNull(resultado);
        Assert.assertEquals(resultado.getId(), "RA-001");
        Assert.assertTrue(resultado.isActiva());
    }

    @Test(description = "HU6 - Fallo: Puntos cero lanza excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaAcumulacion_cuandoPuntosCero_debeLanzarExcepcion() {
        reglasService.definirReglaAcumulacion("RA-001", "Invalida", 0);
    }

    @Test(description = "HU6 - Fallo: ID duplicado lanza excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaAcumulacion_cuandoIdDuplicado_debeLanzarExcepcion() {
        reglasService.definirReglaAcumulacion("RA-001", "Estandar", 10);
        reglasService.definirReglaAcumulacion("RA-001", "Duplicada", 20);
    }

    @Test(description = "HU6 - Exito: Lista de reglas contiene la regla registrada")
    public void obtenerReglasAcumulacion_cuandoExisteUnaRegla_debeRetornarListaConUnElemento() {
        reglasService.definirReglaAcumulacion("RA-001", "10 puntos por pedido", 10);
        List<ReglaAcumulacion> reglas = reglasService.obtenerReglasAcumulacion();
        Assert.assertEquals(reglas.size(), 1);
        Assert.assertEquals(reglas.get(0).getId(), "RA-001");
    }

    @Test(description = "HU6 - Exito: Regla activa es recuperada correctamente")
    public void obtenerReglaAcumulacionActiva_cuandoExisteReglaActiva_debeRetornarla() {
        reglasService.definirReglaAcumulacion("RA-001", "10 puntos por pedido", 10);
        Optional<ReglaAcumulacion> reglaActiva = reglasService.obtenerReglaAcumulacionActiva();
        Assert.assertTrue(reglaActiva.isPresent());
    }

    @Test(description = "HU6 - Fallo: Sin reglas retorna Optional vacio")
    public void obtenerReglaAcumulacionActiva_cuandoNoHayReglas_debeRetornarOptionalVacio() {
        Optional<ReglaAcumulacion> reglaActiva = reglasService.obtenerReglaAcumulacionActiva();
        Assert.assertFalse(reglaActiva.isPresent());
    }

    @Test(description = "HU7 - Exito: Definir regla de redencion valida")
    public void definirReglaRedencion_cuandoDatosValidos_debeCrearReglaActiva() {
        ReglaRedencion resultado = reglasService.definirReglaRedencion("RR-001", "Minimo 100 puntos", 100);
        Assert.assertNotNull(resultado);
        Assert.assertEquals(resultado.getId(), "RR-001");
        Assert.assertTrue(resultado.isActiva());
    }

    @Test(description = "HU7 - Fallo: Puntos negativos lanza excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaRedencion_cuandoPuntosNegativos_debeLanzarExcepcion() {
        reglasService.definirReglaRedencion("RR-001", "Invalida", -50);
    }

    @Test(description = "HU7 - Fallo: ID duplicado lanza excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void definirReglaRedencion_cuandoIdDuplicado_debeLanzarExcepcion() {
        reglasService.definirReglaRedencion("RR-001", "Estandar", 100);
        reglasService.definirReglaRedencion("RR-001", "Duplicada", 200);
    }

    @Test(description = "HU7 - Exito: Regla de redencion activa es recuperada")
    public void obtenerReglaRedencionActiva_cuandoExisteReglaActiva_debeRetornarla() {
        reglasService.definirReglaRedencion("RR-001", "Minimo 100 puntos", 100);
        Optional<ReglaRedencion> reglaActiva = reglasService.obtenerReglaRedencionActiva();
        Assert.assertTrue(reglaActiva.isPresent());
    }

    @Test(description = "HU7 - Fallo: Sin reglas retorna Optional vacio")
    public void obtenerReglaRedencionActiva_cuandoNoHayReglas_debeRetornarOptionalVacio() {
        Optional<ReglaRedencion> reglaActiva = reglasService.obtenerReglaRedencionActiva();
        Assert.assertFalse(reglaActiva.isPresent());
    }
}
