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

/**
 * Pruebas unitarias para PuntosService — QuickBite.
 * Cubre: HU-4, HU-5, HU-8, HU-10
 * Patrón: AAA (Arrange - Act - Assert)
 */
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

        // Suscripción base disponible para la mayoría de tests
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
    }

    // =========================================================
    // HU-4: Acumular puntos por pedido
    // =========================================================

    @Test(description = "HU4 - Éxito: Acumular puntos por pedido en suscripción activa")
    public void acumularPuntos_cuandoSuscripcionActiva_debeIncrementarPuntosDeLealtad() {
        // Arrange
        int puntosIniciales = suscripcionRepositoryMock.buscarPorId("SUB-001").get().getPuntosDeLealtad();

        // Act
        MovimientoPuntos resultado = puntosService.acumularPuntos("SUB-001", 50, "Pedido #1 - Pizza Margherita");

        // Assert
        Assert.assertNotNull(resultado, "El movimiento no debe ser nulo");
        Assert.assertEquals(resultado.getTipo(), TipoMovimiento.ACUMULACION, "Debe ser tipo ACUMULACION");
        Assert.assertEquals(resultado.getCantidad(), 50, "La cantidad debe ser 50 puntos");

        int puntosFinales = suscripcionRepositoryMock.buscarPorId("SUB-001").get().getPuntosDeLealtad();
        Assert.assertEquals(puntosFinales, puntosIniciales + 50, "Los puntos de lealtad deben incrementarse");
    }

    @Test(description = "HU4 - Fallo: Acumular puntos en suscripción inactiva debe lanzar excepción",
          expectedExceptions = SuscripcionInactivaException.class)
    public void acumularPuntos_cuandoSuscripcionInactiva_debeLanzarExcepcion() {
        // Arrange
        suscripcionService.desactivarSuscripcion("SUB-001");

        // Act (debe lanzar excepción)
        puntosService.acumularPuntos("SUB-001", 50, "Pedido #2");

        // Assert - manejado por expectedExceptions
    }

    @Test(description = "HU4 - Fallo: Acumular cantidad negativa debe lanzar excepción",
          expectedExceptions = IllegalArgumentException.class)
    public void acumularPuntos_cuandoCantidadNegativa_debeLanzarExcepcion() {
        // Arrange
        int cantidadInvalida = -10;

        // Act (debe lanzar excepción)
        puntosService.acumularPuntos("SUB-001", cantidadInvalida, "Intento inválido");

        // Assert - manejado por expectedExceptions
    }

    @Test(description = "HU4 - Fallo: Acumular puntos en suscripción inexistente debe lanzar excepción",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void acumularPuntos_cuandoSuscripcionNoExiste_debeLanzarExcepcion() {
        // Arrange - ID inexistente

        // Act (debe lanzar excepción)
        puntosService.acumularPuntos("SUB-INEXISTENTE", 50, "Pedido");

        // Assert - manejado por expectedExceptions
    }

    // =========================================================
    // HU-5: Canjear puntos por recompensas
    // =========================================================

    @Test(description = "HU5 - Éxito: Canjear recompensa con puntos suficientes debe descontar puntos")
    public void canjearRecompensa_cuandoPuntosSuficientes_debeDescontarPuntosDeLealtad() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 300, "Pedidos de la semana");
        Recompensa recompensa = new Recompensa("R-001", "Delivery gratis", "Envío sin costo en tu próximo pedido", 200);

        // Act
        MovimientoPuntos resultado = puntosService.canjearRecompensa("SUB-001", recompensa);

        // Assert
        Assert.assertNotNull(resultado, "El movimiento no debe ser nulo");
        Assert.assertEquals(resultado.getTipo(), TipoMovimiento.REDENCION, "Debe ser tipo REDENCION");
        Assert.assertEquals(resultado.getCantidad(), 200, "Deben descontarse 200 puntos");

        int puntosRestantes = suscripcionRepositoryMock.buscarPorId("SUB-001").get().getPuntosDeLealtad();
        Assert.assertEquals(puntosRestantes, 100, "Deben quedar 100 puntos de lealtad");
    }

    @Test(description = "HU5 - Fallo: Canjear recompensa con puntos insuficientes debe lanzar excepción",
          expectedExceptions = PuntosInsuficientesException.class)
    public void canjearRecompensa_cuandoPuntosInsuficientes_debeLanzarExcepcion() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 50, "Un solo pedido");
        Recompensa recompensa = new Recompensa("R-001", "Delivery gratis", "Envío gratis", 200);

        // Act (debe lanzar excepción)
        puntosService.canjearRecompensa("SUB-001", recompensa);

        // Assert - manejado por expectedExceptions
    }

    @Test(description = "HU5 - Fallo: Canjear recompensa no disponible debe lanzar excepción",
          expectedExceptions = IllegalArgumentException.class)
    public void canjearRecompensa_cuandoRecompensaNoDisponible_debeLanzarExcepcion() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 500, "Pedidos");
        Recompensa recompensaAgotada = new Recompensa("R-002", "Postre gratis", "Un postre a tu elección", 100);
        recompensaAgotada.setDisponible(false);

        // Act (debe lanzar excepción)
        puntosService.canjearRecompensa("SUB-001", recompensaAgotada);

        // Assert - manejado por expectedExceptions
    }

    @Test(description = "HU5 - Fallo: Canjear recompensa en suscripción inactiva debe lanzar excepción",
          expectedExceptions = SuscripcionInactivaException.class)
    public void canjearRecompensa_cuandoSuscripcionInactiva_debeLanzarExcepcion() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 500, "Pedidos previos");
        suscripcionService.desactivarSuscripcion("SUB-001");
        Recompensa recompensa = new Recompensa("R-001", "Delivery gratis", "Envío gratis", 200);

        // Act (debe lanzar excepción)
        puntosService.canjearRecompensa("SUB-001", recompensa);

        // Assert - manejado por expectedExceptions
    }

    // =========================================================
    // HU-8: Consultar historial de movimientos
    // =========================================================

    @Test(description = "HU8 - Éxito: Historial refleja todos los movimientos realizados")
    public void consultarHistorial_cuandoExistenMovimientos_debeRetornarTodos() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 50, "Pedido #1 - Sushi");
        puntosService.acumularPuntos("SUB-001", 80, "Pedido #2 - Hamburguesa");
        Recompensa r = new Recompensa("R-001", "Bebida gratis", "Bebida a tu elección", 30);
        puntosService.canjearRecompensa("SUB-001", r);

        // Act
        List<MovimientoPuntos> historial = puntosService.consultarHistorial("SUB-001");

        // Assert
        Assert.assertNotNull(historial, "El historial no debe ser nulo");
        Assert.assertEquals(historial.size(), 3, "Deben existir 3 movimientos en el historial");
    }

    @Test(description = "HU8 - Éxito: Historial vacío cuando no hay movimientos")
    public void consultarHistorial_cuandoSinMovimientos_debeRetornarListaVacia() {
        // Arrange - suscripción sin movimientos

        // Act
        List<MovimientoPuntos> historial = puntosService.consultarHistorial("SUB-001");

        // Assert
        Assert.assertNotNull(historial, "El historial no debe ser nulo");
        Assert.assertTrue(historial.isEmpty(), "El historial debe estar vacío");
    }

    @Test(description = "HU8 - Fallo: Historial de suscripción inexistente debe lanzar excepción",
          expectedExceptions = SuscripcionNoEncontradaException.class)
    public void consultarHistorial_cuandoSuscripcionNoExiste_debeLanzarExcepcion() {
        // Arrange - ID inexistente

        // Act (debe lanzar excepción)
        puntosService.consultarHistorial("SUB-INEXISTENTE");

        // Assert - manejado por expectedExceptions
    }

    // =========================================================
    // HU-10: Reporte de puntos acumulados y canjeados
    // =========================================================

    @Test(description = "HU10 - Éxito: Total acumulado suma correctamente todos los pedidos")
    public void obtenerTotalAcumulado_cuandoVariosPedidos_debeRetornarSumaCorrecta() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 50,  "Pedido #1 - Tacos");
        puntosService.acumularPuntos("SUB-001", 80,  "Pedido #2 - Ramen");
        puntosService.acumularPuntos("SUB-001", 120, "Pedido #3 - Parrilla");

        // Act
        int totalAcumulado = puntosService.obtenerTotalAcumulado("SUB-001");

        // Assert
        Assert.assertEquals(totalAcumulado, 250, "El total acumulado debe ser 250 puntos");
    }

    @Test(description = "HU10 - Éxito: Total canjeado suma correctamente todos los canjes")
    public void obtenerTotalCanjeado_cuandoVariosCanjes_debeRetornarSumaCorrecta() {
        // Arrange
        puntosService.acumularPuntos("SUB-001", 500, "Pedidos del mes");
        Recompensa r1 = new Recompensa("R-001", "Delivery gratis",  "Envío gratis",         100);
        Recompensa r2 = new Recompensa("R-002", "Descuento 20%",    "20% en próximo pedido", 150);
        puntosService.canjearRecompensa("SUB-001", r1);
        puntosService.canjearRecompensa("SUB-001", r2);

        // Act
        int totalCanjeado = puntosService.obtenerTotalCanjeado("SUB-001");

        // Assert
        Assert.assertEquals(totalCanjeado, 250, "El total canjeado debe ser 250 puntos");
    }

    @Test(description = "HU10 - Fallo: Total acumulado sin movimientos debe retornar cero")
    public void obtenerTotalAcumulado_cuandoSinMovimientos_debeRetornarCero() {
        // Arrange - sin pedidos

        // Act
        int totalAcumulado = puntosService.obtenerTotalAcumulado("SUB-001");

        // Assert
        Assert.assertEquals(totalAcumulado, 0, "El total acumulado debe ser 0 cuando no hay pedidos");
    }
}