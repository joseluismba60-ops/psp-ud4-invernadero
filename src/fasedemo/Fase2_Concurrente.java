package fasedemo;

import modelo.*;

public class Fase2_Concurrente {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== FASE 2: INVERNADERO CONCURRENTE ===");
        long inicio = System.currentTimeMillis();

        // 1. Instancias
        Temperatura temp = new Temperatura();
        Humedad hum = new Humedad();
        Luz luz = new Luz();
        ProgramadorRiego riego = new ProgramadorRiego();

        // 2. Envolver los Runnable en hilos Thread con nombres claros
        Thread tTemp = new Thread(temp, "sensor-Temperatura");
        Thread tHum = new Thread(hum, "sensor-Humedad");
        Thread tLuz = new Thread(luz, "sensor-Luz");

        // 3. Arrancar los cuatro hilos en paralelo
        tTemp.start();
        tHum.start();
        tLuz.start();
        riego.start(); // El riego ya es un Thread, no necesita new Thread(...)

        // 4. Sincronización: esperar a que finalicen los 4 hilos antes de sacar informes
        tTemp.join();
        tHum.join();
        tLuz.join();
        riego.join();

        long fin = System.currentTimeMillis();
        long tiempoTotal = fin - inicio;

        System.out.println("\n--- INFORMES DE DISPOSITIVOS ---");
        System.out.println(temp.informe());
        System.out.println(hum.informe());
        System.out.println(luz.informe());
        System.out.println(riego.informe());
        System.out.printf("%nTiempo total de ejecución concurrente: %d ms (~%.2f s)%n",
                tiempoTotal, tiempoTotal / 1000.0);
    }
}