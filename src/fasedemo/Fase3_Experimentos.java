package fasedemo;

import modelo.*;

public class Fase3_Experimentos {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== FASE 3: TRES EXPERIMENTOS ===");

        // EXPERIMENTO A: Imprimir informe sin esperar vs esperando
        System.out.println("\n--- EXPERIMENTO A: Informe sin join() vs con join() ---");
        Temperatura tempA = new Temperatura();
        Thread tTempA = new Thread(tempA, "sensor-ExpA");
        tTempA.start();

        // Impresión inmediata sin esperar a que el hilo trabaje
        System.out.println("Informe INMEDIATO (sin join): " + tempA.informe());

        tTempA.join(); // Esperamos a que finalice
        System.out.println("Informe TRAS JOIN: " + tempA.informe());

        // EXPERIMENTO B: Llamar a riego.run() directamente en vez de start()
        System.out.println("\n--- EXPERIMENTO B: riego.run() vs riego.start() ---");
        ProgramadorRiego riegoB = new ProgramadorRiego(3);
        long inicioB = System.currentTimeMillis();

        riegoB.run(); // Se llama directamente a run() en lugar de start()

        long finB = System.currentTimeMillis();
        System.out.printf("Tiempo consumido por run(): %d ms%n", (finB - inicioB));
        System.out.println("Estado final del objeto riegoB: " + riegoB.getState());

        // EXPERIMENTO C: Intentar reorientar/reiniciar un hilo ya terminado
        System.out.println("\n--- EXPERIMENTO C: Recomenzar un hilo terminado ---");
        ProgramadorRiego riegoC = new ProgramadorRiego(2);
        riegoC.start();
        riegoC.join(); // Esperamos a que termine del todo

        try {
            System.out.println("Intentando llamar a start() por segunda vez sobre el mismo objeto...");
            riegoC.start();
        } catch (IllegalThreadStateException e) {
            System.out.println("Excepción capturada correctamente: " + e.getClass().getSimpleName());
            System.out.println("Mensaje de la excepción: " + e.getMessage());
        }
    }
}