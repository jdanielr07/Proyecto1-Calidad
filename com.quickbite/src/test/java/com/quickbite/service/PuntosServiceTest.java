package com.quickbite.service;

import com.quickbite.exception.SuscripcionInactivaException;
import com.quickbite.exception.SuscripcionNoEncontradaException;
import com.quickbite.exception.PuntosInsuficientesException;
import com.quickbite.mock.SuscripcionRepositoryMock;
import com.quickbite.mock.PuntosRepositoryMock;
import com.quickbite.model.MovimientoPuntos;
import com.quickbite.model.Recompensa;
import com.quickbite.model.TipoMovimiento;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.util.List;

public class PuntosServiceTest {

    private PuntosService puntosService;
    private SuscripcionService suscripcionService;
    private SuscripcionRepositoryMock suscripcionRepositoryMock;
    private PuntosRepositoryMock puntosRepositoryMock;

    @BeforeMethod
    public void setUp() {
        suscripcionRepositoryMock = new SuscripcionRepositoryMock();
        puntosRepositoryMock = new PuntosRepositoryMock();
        suscripcionService = new SuscripcionService(suscripcionRepositoryMock);
        puntosService = new PuntosService(suscripcionRepositoryMock, puntosRepositoryMock);
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
    }

    // HU-4
    @Test(description = "QB-11 | HU-4 - Exito: Acumular puntos por pedido en suscripcion activa")
    public void acumularPuntos_cuandoSuscripcionActiva_debeIncrementarPuntosDeLealtad() {
        // Arrange
        int puntosIniciales = suscripcionRepositoryMock.buscarPorId("SUB-001").get().getPuntosDeLealtad();
        // Act
        MovimientoPuntos resultado = puntosService.acumularPuntos("SUB-001", 50, "Pedido #1 - Pizza");
        // Assert
        Assert.assertNotNull(resultado);
        Assert.assertEquals(resultado.getTipo(), TipoMovimiento.ACUMULACION);
        Assert.assertEquals(resultado.getCantidad(), 50);
        Assert.assertEquals(suscripcionRepositoryMock.buscarPorId("SUB-001").get().getPuntosDeLealtad(), puntosIniciales + 50);
    }

    @Test(description = "QB-12 | HU-4 - Fallo: Acumular puntos en suscripcion inactiva debe lanzar excepcion",
          expectedExceptions = SuscripcionInactivaException.class)
    public void acumularPuntos_cuandoSuscripcionInactiva_debeLanzarExcepcion() {
        // Arrange
        suscripcionService.desactivarSuscripcion("SUB-001");
        // Act
        puntosService.acumularPuntos("SUB-001", 50, "Pedido");
    }

    @Test(description = "QB-13 | HU-4 - Fallo: Acumular cantidad negativa debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void acumularPuntos_cuandoCantidadNegativa_debeLanzarExcepcion() {
        puntosService.acumularPuntos("SUB-001", -10, "Invalido");
    }

    @Test(description = "QB-14 | HU-4 - Fallo: Acumular puntos en suscripcion inexistente debe lanzar excepcion",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void acumularPuntos_cuandoSuscripcionNoExiste_debeLanzarExcepcion() {
        puntosService.acumularPuntos("SUB-INEXISTENTE", 50, "Pedido");
    }

    // HU-5
    @Test(description = "QB-15 | HU-5 - Exito: Canjear recompensa con puntos suficientes debe descontar puntos")
    public void canjearRecompensa_cuandoPuntosSuficientes_debeDescontarPuntosDeLealtad() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 300, "Pedidos de la semana");
        Recompensa recompensa = new Recompensa("R-001", "Delivery gratis", "Envio sin costo", 200);
        // Act
        MovimientoPuntos resultado = puntosService.canjearRecompensa("SUB-001", recompensa);
        // Assert
        Assert.assertNotNull(resultado);
        Assert.assertEquals(resultado.getTipo(), TipoMovimiento.REDENCION);
        Assert.assertEquals(resultado.getCantidad(), 200);
        Assert.assertEquals(suscripcionRepositoryMock.buscarPorId("SUB-001").get().getPuntosDeLealtad(), 100);
    }

    @Test(description = "QB-16 | HU-5 - Fallo: Canjear recompensa con puntos insuficientes debe lanzar excepcion",
          expectedExceptions = PuntosInsuficientesException.class)
    public void canjearRecompensa_cuandoPuntosInsuficientes_debeLanzarExcepcion() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 50, "Un solo pedido");
        Recompensa recompensa = new Recompensa("R-001", "Delivery gratis", "Envio gratis", 200);
        // Act
        puntosService.canjearRecompensa("SUB-001", recompensa);
    }

    @Test(description = "QB-17 | HU-5 - Fallo: Canjear recompensa no disponible debe lanzar excepcion",
          expectedExceptions = IllegalArgumentException.class)
    public void canjearRecompensa_cuandoRecompensaNoDisponible_debeLanzarExcepcion() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 500, "Pedidos");
        Recompensa recompensaAgotada = new Recompensa("R-002", "Postre gratis", "Un postre", 100);
        recompensaAgotada.setDisponible(false);
        // Act
        puntosService.canjearRecompensa("SUB-001", recompensaAgotada);
    }

    @Test(description = "QB-18 | HU-5 - Fallo: Canjear recompensa en suscripcion inactiva debe lanzar excepcion",
          expectedExceptions = SuscripcionInactivaException.class)
    public void canjearRecompensa_cuandoSuscripcionInactiva_debeLanzarExcepcion() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 500, "Pedidos previos");
        suscripcionService.desactivarSuscripcion("SUB-001");
        Recompensa recompensa = new Recompensa("R-001", "Delivery gratis", "Envio gratis", 200);
        // Act
        puntosService.canjearRecompensa("SUB-001", recompensa);
    }

    // HU-8
    @Test(description = "QB-28 | HU-8 - Exito: Historial refleja todos los movimientos realizados")
    public void consultarHistorial_cuandoExistenMovimientos_debeRetornarTodos() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 50, "Pedido #1 - Sushi");
        puntosService.acumularPuntos("SUB-001", 80, "Pedido #2 - Hamburguesa");
        Recompensa r = new Recompensa("R-001", "Bebida gratis", "Bebida a tu eleccion", 30);
        puntosService.canjearRecompensa("SUB-001", r);
        // Act
        List<MovimientoPuntos> historial = puntosService.consultarHistorial("SUB-001");
        // Assert
        Assert.assertNotNull(historial);
        Assert.assertEquals(historial.size(), 3);
    }

    @Test(description = "QB-29 | HU-8 - Exito: Historial vacio cuando no hay movimientos")
    public void consultarHistorial_cuandoSinMovimientos_debeRetornarListaVacia() {
        // Act
        List<MovimientoPuntos> historial = puntosService.consultarHistorial("SUB-001");
        // Assert
        Assert.assertNotNull(historial);
        Assert.assertTrue(historial.isEmpty());
    }

    @Test(description = "QB-30 | HU-8 - Fallo: Historial de suscripcion inexistente debe lanzar excepcion",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void consultarHistorial_cuandoSuscripcionNoExiste_debeLanzarExcepcion() {
        puntosService.consultarHistorial("SUB-INEXISTENTE");
    }

    // HU-10
    @Test(description = "QB-36 | HU-10 - Exito: Total acumulado suma correctamente todos los pedidos")
    public void obtenerTotalAcumulado_cuandoVariosPedidos_debeRetornarSumaCorrecta() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 50,  "Pedido #1 - Tacos");
        puntosService.acumularPuntos("SUB-001", 80,  "Pedido #2 - Ramen");
        puntosService.acumularPuntos("SUB-001", 120, "Pedido #3 - Parrilla");
        // Act & Assert
        Assert.assertEquals(puntosService.obtenerTotalAcumulado("SUB-001"), 250);
    }

    @Test(description = "QB-37 | HU-10 - Exito: Total canjeado suma correctamente todos los canjes")
    public void obtenerTotalCanjeado_cuandoVariosCanjes_debeRetornarSumaCorrecta() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 500, "Pedidos del mes");
        puntosService.canjearRecompensa("SUB-001", new Recompensa("R-001", "Delivery gratis", "Envio gratis", 100));
        puntosService.canjearRecompensa("SUB-001", new Recompensa("R-002", "Descuento 20%", "20% en proximo pedido", 150));
        // Act & Assert
        Assert.assertEquals(puntosService.obtenerTotalCanjeado("SUB-001"), 250);
    }

    @Test(description = "QB-38 | HU-10 - Fallo: Total acumulado sin movimientos debe retornar cero")
    public void obtenerTotalAcumulado_cuandoSinMovimientos_debeRetornarCero() {
        Assert.assertEquals(puntosService.obtenerTotalAcumulado("SUB-001"), 0);
    }
}