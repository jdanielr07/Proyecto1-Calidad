package com.quickbite;

import com.quickbite.model.*;
import com.quickbite.repository.SuscripcionRepository;
import com.quickbite.repository.PuntosRepository;
import com.quickbite.service.*;
import com.quickbite.exception.*;

import java.util.*;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static SuscripcionService suscripcionService;
    private static PuntosService puntosService;
    private static ReglasService reglasService;

    public static void main(String[] args) {
        // Repositorios en memoria (mismos mocks del proyecto)
        SuscripcionRepository suscripcionRepo = new com.quickbite.mock.SuscripcionRepositoryMock();
        PuntosRepository puntosRepo = new com.quickbite.mock.PuntosRepositoryMock();

        suscripcionService = new SuscripcionService(suscripcionRepo);
        puntosService      = new PuntosService(suscripcionRepo, puntosRepo);
        reglasService      = new ReglasService();

        // Datos de ejemplo precargados
        suscripcionService.registrarSuscripcion("SUB-001", "CLI-001");
        suscripcionService.registrarSuscripcion("SUB-002", "CLI-002");
        puntosService.acumularPuntos("SUB-001", 150, "Pedido inicial de bienvenida");
        reglasService.definirReglaAcumulacion("RA-001", "10 puntos por cada pedido", 10);
        reglasService.definirReglaRedencion("RR-001", "Minimo 100 puntos para canjear", 100);

        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║     QuickBite — Programa de Lealtad      ║");
        System.out.println("╚══════════════════════════════════════════╝");
        System.out.println("  Datos de prueba precargados:");
        System.out.println("  • SUB-001 (CLI-001) — 150 puntos");
        System.out.println("  • SUB-002 (CLI-002) — 0 puntos");

        int opcion = -1;
        while (opcion != 0) {
            mostrarMenu();
            opcion = leerInt("Seleccione una opcion: ");
            System.out.println();
            try {
                switch (opcion) {
                    case 1:  registrarSuscripcion();     break;
                    case 2:  cambiarEstado();            break;
                    case 3:  consultarEstado();          break;
                    case 4:  acumularPuntos();           break;
                    case 5:  canjearRecompensa();        break;
                    case 6:  verHistorial();             break;
                    case 7:  reporteSuscripciones();     break;
                    case 8:  reportePuntos();            break;
                    case 9:  definirReglaAcumulacion();  break;
                    case 10: definirReglaRedencion();    break;
                    case 0:
                        System.out.println("¡Hasta luego!");
                        break;
                    default:
                        System.out.println("⚠ Opcion invalida. Intente de nuevo.");
                }
            } catch (SuscripcionNoEncontradaException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } catch (SuscripcionInactivaException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } catch (PuntosInsuficientesException e) {
                System.out.println("❌ Error: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Error: " + e.getMessage());
            }
            if (opcion != 0) pausar();
        }
    }

    // ── MENU ──────────────────────────────────────────────────────────────────

    private static void mostrarMenu() {
        System.out.println("\n┌──────────────────────────────────────────┐");
        System.out.println("│               MENU PRINCIPAL             │");
        System.out.println("├──────────────────────────────────────────┤");
        System.out.println("│  1. Registrar suscripcion                │");
        System.out.println("│  2. Activar / Desactivar suscripcion     │");
        System.out.println("│  3. Consultar estado de suscripcion      │");
        System.out.println("│  4. Acumular puntos por pedido           │");
        System.out.println("│  5. Canjear recompensa                   │");
        System.out.println("│  6. Ver historial de movimientos         │");
        System.out.println("│  7. Reporte de suscripciones             │");
        System.out.println("│  8. Reporte de puntos                    │");
        System.out.println("│  9. Definir regla de acumulacion         │");
        System.out.println("│ 10. Definir regla de redencion           │");
        System.out.println("│  0. Salir                                │");
        System.out.println("└──────────────────────────────────────────┘");
    }

    // ── OPCIONES ──────────────────────────────────────────────────────────────

    private static void registrarSuscripcion() {
        System.out.println("─── Registrar Suscripcion ───");
        String id       = leerTexto("ID de suscripcion (ej: SUB-003): ");
        String clienteId = leerTexto("ID de cliente    (ej: CLI-003): ");
        Suscripcion s = suscripcionService.registrarSuscripcion(id, clienteId);
        System.out.println("✅ Suscripcion registrada correctamente.");
        imprimirSuscripcion(s);
    }

    private static void cambiarEstado() {
        System.out.println("─── Activar / Desactivar Suscripcion ───");
        String id = leerTexto("ID de suscripcion: ");
        EstadoSuscripcion estadoActual = suscripcionService.consultarEstado(id);
        System.out.println("   Estado actual: " + estadoActual);
        System.out.println("   1. Activar");
        System.out.println("   2. Desactivar");
        int op = leerInt("Seleccione: ");
        if (op == 1) {
            suscripcionService.activarSuscripcion(id);
            System.out.println("✅ Suscripcion activada.");
        } else if (op == 2) {
            suscripcionService.desactivarSuscripcion(id);
            System.out.println("✅ Suscripcion desactivada.");
        } else {
            System.out.println("⚠ Opcion invalida.");
        }
    }

    private static void consultarEstado() {
        System.out.println("─── Consultar Estado ───");
        String id = leerTexto("ID de suscripcion: ");
        EstadoSuscripcion estado = suscripcionService.consultarEstado(id);
        System.out.println("   Estado: " + estado);
    }

    private static void acumularPuntos() {
        System.out.println("─── Acumular Puntos por Pedido ───");
        String id      = leerTexto("ID de suscripcion: ");
        int    puntos  = leerInt("Puntos a acumular: ");
        String detalle = leerTexto("Detalle del pedido: ");
        MovimientoPuntos mov = puntosService.acumularPuntos(id, puntos, detalle);
        System.out.println("✅ Puntos acumulados correctamente.");
        imprimirMovimiento(mov);
        int total = puntosService.obtenerTotalAcumulado(id);
        System.out.println("   Total acumulado: " + total + " puntos");
    }

    private static void canjearRecompensa() {
        System.out.println("─── Canjear Recompensa ───");
        String id = leerTexto("ID de suscripcion: ");
        System.out.println("   Recompensas disponibles:");
        System.out.println("   1. Delivery gratis      (200 puntos)");
        System.out.println("   2. Descuento 20%        (150 puntos)");
        System.out.println("   3. Postre gratis        (100 puntos)");
        System.out.println("   4. Bebida gratis        ( 80 puntos)");
        int op = leerInt("Seleccione recompensa: ");
        Recompensa recompensa;
        switch (op) {
            case 1: recompensa = new Recompensa("R-001", "Delivery gratis",  "Envio sin costo en tu proximo pedido", 200); break;
            case 2: recompensa = new Recompensa("R-002", "Descuento 20%",    "20% de descuento en tu proximo pedido", 150); break;
            case 3: recompensa = new Recompensa("R-003", "Postre gratis",    "Un postre a tu eleccion", 100); break;
            case 4: recompensa = new Recompensa("R-004", "Bebida gratis",    "Una bebida a tu eleccion", 80); break;
            default: System.out.println("⚠ Opcion invalida."); return;
        }
        MovimientoPuntos mov = puntosService.canjearRecompensa(id, recompensa);
        System.out.println("✅ Recompensa canjeada: " + recompensa.getNombre());
        imprimirMovimiento(mov);
        int restantes = puntosService.obtenerTotalAcumulado(id) - puntosService.obtenerTotalCanjeado(id);
        System.out.println("   Puntos restantes: " + restantes);
    }

    private static void verHistorial() {
        System.out.println("─── Historial de Movimientos ───");
        String id = leerTexto("ID de suscripcion: ");
        List<MovimientoPuntos> historial = puntosService.consultarHistorial(id);
        if (historial.isEmpty()) {
            System.out.println("   No hay movimientos registrados.");
            return;
        }
        System.out.println("   " + historial.size() + " movimiento(s) encontrado(s):\n");
        System.out.printf("   %-10s %-12s %-8s %s%n", "ID", "TIPO", "PUNTOS", "DETALLE");
        System.out.println("   " + "─".repeat(55));
        for (MovimientoPuntos m : historial) {
            System.out.printf("   %-10s %-12s %-8d %s%n",
                m.getId(), m.getTipo(), m.getCantidad(), m.getDescripcion());
        }
    }

    private static void reporteSuscripciones() {
        System.out.println("─── Reporte de Suscripciones ───");
        List<Suscripcion> activas   = suscripcionService.obtenerSuscripcionesActivas();
        List<Suscripcion> inactivas = suscripcionService.obtenerSuscripcionesInactivas();
        System.out.println("\n  ACTIVAS (" + activas.size() + "):");
        if (activas.isEmpty()) System.out.println("   (ninguna)");
        for (Suscripcion s : activas) imprimirSuscripcion(s);
        System.out.println("\n  INACTIVAS (" + inactivas.size() + "):");
        if (inactivas.isEmpty()) System.out.println("   (ninguna)");
        for (Suscripcion s : inactivas) imprimirSuscripcion(s);
    }

    private static void reportePuntos() {
        System.out.println("─── Reporte de Puntos ───");
        String id = leerTexto("ID de suscripcion: ");
        int acumulado = puntosService.obtenerTotalAcumulado(id);
        int canjeado  = puntosService.obtenerTotalCanjeado(id);
        int saldo     = acumulado - canjeado;
        System.out.println("\n   Total acumulado : " + acumulado + " puntos");
        System.out.println("   Total canjeado  : " + canjeado  + " puntos");
        System.out.println("   Saldo actual    : " + saldo     + " puntos");
    }

    private static void definirReglaAcumulacion() {
        System.out.println("─── Definir Regla de Acumulacion ───");
        String id          = leerTexto("ID de regla (ej: RA-002): ");
        String descripcion = leerTexto("Descripcion: ");
        int    puntos      = leerInt("Puntos por pedido: ");
        ReglaAcumulacion regla = reglasService.definirReglaAcumulacion(id, descripcion, puntos);
        System.out.println("✅ Regla creada: " + regla.getId() + " — " + regla.getDescripcion());
    }

    private static void definirReglaRedencion() {
        System.out.println("─── Definir Regla de Redencion ───");
        String id          = leerTexto("ID de regla (ej: RR-002): ");
        String descripcion = leerTexto("Descripcion: ");
        int    puntosMin   = leerInt("Puntos minimos para canjear: ");
        ReglaRedencion regla = reglasService.definirReglaRedencion(id, descripcion, puntosMin);
        System.out.println("✅ Regla creada: " + regla.getId() + " — " + regla.getDescripcion());
    }

    // ── HELPERS ───────────────────────────────────────────────────────────────

    private static void imprimirSuscripcion(Suscripcion s) {
        System.out.println("   ┌─ ID: " + s.getId());
        System.out.println("   │  Cliente : " + s.getClienteId());
        System.out.println("   │  Estado  : " + s.getEstado());
        System.out.println("   └─ Puntos  : " + s.getPuntosDeLealtad());
    }

    private static void imprimirMovimiento(MovimientoPuntos m) {
        System.out.println("   ┌─ ID: " + m.getId());
        System.out.println("   │  Tipo    : " + m.getTipo());
        System.out.println("   │  Puntos  : " + m.getCantidad());
        System.out.println("   └─ Detalle : " + m.getDescripcion());
    }

    private static String leerTexto(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int leerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("   ⚠ Ingrese un numero valido.");
            }
        }
    }

    private static void pausar() {
        System.out.print("\nPresione Enter para continuar...");
        scanner.nextLine();
    }
}