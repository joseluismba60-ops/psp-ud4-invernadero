package modelo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ProgramadorRiego extends Thread {
    private final int totalCiclos;
    private final int periodoMs;
    private int ciclosCompletados = 0;
    private double litrosTotales = 0.0;

    public ProgramadorRiego(int totalCiclos) {
        super("riego"); // Le asignamos el nombre del hilo en el constructor de Thread
        this.totalCiclos = totalCiclos;
        this.periodoMs = 700;
    }

    public ProgramadorRiego() {
        this(5);
    }

    @Override
    public void run() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        for (int i = 0; i < totalCiclos; i++) {
            boolean abierta = (i % 2 == 0);
            ciclosCompletados++;
            if (abierta) {
                litrosTotales += 2.5;
            }

            String hora = LocalTime.now().format(fmt);
            String estadoValvula = abierta ? "ABIERTA (+2.5L)" : "cerrada";

            System.out.printf("[%s] [%s] Válvula de riego: %s (Ciclo %d/%d)%n",
                    hora, getName(), estadoValvula, ciclosCompletados, totalCiclos);

            try {
                Thread.sleep(periodoMs);
            } catch (InterruptedException e) {
                interrupt();
                break;
            }
        }
    }

    public String informe() {
        return String.format("[ProgramadorRiego] Ciclos realizados: %d | Litros regados: %.2f L",
                ciclosCompletados, litrosTotales);
    }
}