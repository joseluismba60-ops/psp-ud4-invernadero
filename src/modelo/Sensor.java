package modelo;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public abstract class Sensor implements Runnable {
    protected String nombre;
    protected String unidad;
    protected int periodoMs;
    protected int maxLecturas;
    protected double umbralMin;
    protected double umbralMax;

    // Estadísticas
    protected int lecturasRealizadas = 0;
    protected double sumaLecturas = 0;
    protected double minLectura = Double.MAX_VALUE;
    protected double maxLectura = Double.MIN_VALUE;
    protected int numAlertas = 0;

    public Sensor(String nombre, String unidad, int periodoMs, int maxLecturas, double umbralMin, double umbralMax) {
        this.nombre = nombre;
        this.unidad = unidad;
        this.periodoMs = periodoMs;
        this.maxLecturas = maxLecturas;
        this.umbralMin = umbralMin;
        this.umbralMax = umbralMax;
    }

    public abstract double medir();

    @Override
    public void run() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
        for (int i = 0; i < maxLecturas; i++) {
            double valor = medir();
            lecturasRealizadas++;
            sumaLecturas += valor;
            if (valor < minLectura) minLectura = valor;
            if (valor > maxLectura) maxLectura = valor;

            boolean alerta = (valor < umbralMin || valor > umbralMax);
            if (alerta) numAlertas++;

            String hora = LocalTime.now().format(fmt);
            String hilo = Thread.currentThread().getName();
            String msgAlerta = alerta ? " ⚠️ ALERTA" : "";

            System.out.printf("[%s] [%s] %s: %.2f %s%s%n",
                    hora, hilo, nombre, valor, unidad, msgAlerta);

            try {
                Thread.sleep(periodoMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public String informe() {
        if (lecturasRealizadas == 0) {
            return String.format("[%s] Sin lecturas registradas.", nombre);
        }
        double media = sumaLecturas / lecturasRealizadas;
        return String.format("[%s] Lecturas: %d | Media: %.2f %s | Mín: %.2f %s | Máx: %.2f %s | Alertas: %d",
                nombre, lecturasRealizadas, media, unidad, minLectura, unidad, maxLectura, unidad, numAlertas);
    }
}