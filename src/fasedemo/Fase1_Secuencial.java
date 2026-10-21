package fasedemo;

import modelo.*;

public class Fase1_Secuencial {
    public static void main(String[] args) {
        System.out.println("=== FASE 1: PROTOTIPO SECUENCIAL ===");
        long inicio = System.currentTimeMillis();

        Temperatura temp = new Temperatura();
        Humedad hum = new Humedad();
        Luz luz = new Luz();
        ProgramadorRiego riego = new ProgramadorRiego();

        // Ejecución secuencial llamando directamente a run() en el hilo main
        temp.run();
        hum.run();
        luz.run();
        riego.run();

        long fin = System.currentTimeMillis();
        long tiempoTotal = fin - inicio;

        System.out.println("\n--- INFORMES DE DISPOSITIVOS ---");
        System.out.println(temp.informe());
        System.out.println(hum.informe());
        System.out.println(luz.informe());
        System.out.println(riego.informe());
        System.out.printf("%nTiempo total de ejecución secuencial: %d ms (~%.2f s)%n",
                tiempoTotal, tiempoTotal / 1000.0);
    }
}
