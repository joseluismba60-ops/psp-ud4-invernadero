package fasedemo;

import modelo.*;

public class Fase4_Vigilante {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== FASE 4: VIGILANTE DE ESTADOS ===");

        Temperatura temp = new Temperatura();
        Humedad hum = new Humedad();
        Luz luz = new Luz();
        ProgramadorRiego riego = new ProgramadorRiego();

        Thread tTemp = new Thread(temp, "sensor-Temperatura");
        Thread tHum = new Thread(hum, "sensor-Humedad");
        Thread tLuz = new Thread(luz, "sensor-Luz");

        // Hilo vigilante en bucle infinito
        Thread vigilante = new Thread(() -> {
            while (true) {
                System.out.printf("[VIGILANTE] Temp: %s | Hum: %s | Luz: %s | Riego: %s%n",
                        tTemp.getState(), tHum.getState(), tLuz.getState(), riego.getState());
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "vigilante");

        // CLAVE: Marcarlo como demonio antes de arrancarlo
        vigilante.setDaemon(true);

        // Estados ANTES de arrancar (deben salir NEW)
        System.out.println("--- ESTADOS ANTES DE START ---");
        System.out.printf("Temp: %s | Hum: %s | Luz: %s | Riego: %s%n%n",
                tTemp.getState(), tHum.getState(), tLuz.getState(), riego.getState());

        // Arrancar hilos principales y vigilante
        tTemp.start();
        tHum.start();
        tLuz.start();
        riego.start();
        vigilante.start();

        // Esperar a los dispositivos principales
        tTemp.join();
        tHum.join();
        tLuz.join();
        riego.join();

        // Estados DESPUÉS de terminar (deben salir TERMINATED)
        System.out.println("\n--- ESTADOS DESPUÉS DE JOIN ---");
        System.out.printf("Temp: %s | Hum: %s | Luz: %s | Riego: %s%n",
                tTemp.getState(), tHum.getState(), tLuz.getState(), riego.getState());

        System.out.println("Main finalizado. El vigilante demonio se destruye automáticamente.");
    }
}