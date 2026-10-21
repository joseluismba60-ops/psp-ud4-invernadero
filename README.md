# Invernadero con hilos

**Autor:** José Luis Mba  
**Módulo:** Programación de Servicios y Procesos (2.º DAM)  
**Centro:** MEDAC Sevilla Este  

---

## Qué hace
Simulación concurrente en Java para la monitorización automática de un invernadero. El sistema gestiona tres sensores (Temperatura, Humedad y Luz) y un programador de riego, permitiendo comparar la eficiencia entre un flujo secuencial mono-hilo y un diseño multihilo concurrente.

---

## Cómo ejecutarlo
1. Clonar el repositorio: `git clone https://github.com/tu-usuario/psp-ud4-invernadero.git`
2. Abrir el proyecto en IntelliJ IDEA con **JDK 21**.
3. Ejecutar las clases situadas en `src/fasedemo/`:
   * `Fase1_Secuencial.java`
   * `Fase2_Concurrente.java`
   * `Fase3_Experimentos.java`
   * `Fase4_Vigilante.java`

---

## Resultados

| Fase | Tipo de Ejecución | Tiempo Total | Comportamiento del Log |
| :--- | :--- | :--- | :--- |
| **Fase 1** | Secuencial (`run()`) | **~11,28 s** | Orden estricto en bloque. Todo lo ejecuta el hilo `[main]`. |
| **Fase 2** | Concurrente (`start()`) | **~3,55 s** | Salida intercalada con nombres propios (`[sensor-Temperatura]`, `[riego]`, etc.). |

* **Relación de tiempos:**  
  * En la Fase 1 el tiempo equivale a la **suma de los tiempos** de todos los dispositivos ($400\text{ms} \times 5 + 600\text{ms} \times 5 + 500\text{ms} \times 5 + 700\text{ms} \times 5 = 11000\text{ ms}$).
  * En la Fase 2 el tiempo equivale al **máximo de los tiempos** ($\max(2000, 3000, 2500, 3500) = 3500\text{ ms}$), correspondiente a los 5 ciclos del riego.

---

## Análisis

### ¿Por qué compensa usar hilos en el invernadero?
En la versión secuencial, si el sensor de humedad tarda 600 ms en cada toma, el sistema se queda bloqueado esperando y nadie vigila la temperatura ni la luz. Al usar hilos, los sensores miden en paralelo sin interrumpirse entre sí, reduciendo el tiempo total de lectura de 11,28 s a solo 3,55 s y garantizando un control continuo del entorno.

---

### Experimentos A, B y C

* **Experimento A (Lectura del informe sin `join()`):**
  * *Observación:* La primera llamada a `informe()` devuelve `[Temperatura] Sin lecturas registradas`. Tras hacer `join()`, muestra el informe completo con las 5 lecturas.
  * *Explicación:* Como `main` no esperó a la ejecución del nuevo hilo, consultó el informe inmediatamente al arrancar, cuando el bucle del sensor aún no había guardado ninguna medición.

* **Experimento B (Invocación directa de `run()` frente a `start()`):**
  * *Observación:* Los mensajes en consola indican que los ejecuta el hilo `[main]`, tarda algo más de 2 segundos en responder y el estado final del objeto devuelve `NEW`.
  * *Explicación:* Invocamos `run()` como un método normal de clase. No se llegó a solicitar la creación de un nuevo hilo de ejecución al sistema operativo, por lo que el objeto `Thread` nunca pasó del estado inicial `NEW`.

* **Experimento C (Recomenzar un hilo finalizado):**
  * *Observación:* Al llamar a `start()` por segunda vez salta la excepción `java.lang.IllegalThreadStateException`.
  * *Explicación:* Un hilo en Java sigue un ciclo de vida no reentrante. Cuando completa su ejecución entra en el estado `TERMINATED`, desde el cual no se le permite volver a iniciar.

---

### Estados observados y el Vigilante

| Estado observado | Cuándo ocurre | Línea / Acción que lo provoca |
| :--- | :--- | :--- |
| `NEW` | Antes de iniciar la ejecución del hilo. | Instanciación del objeto sin llamar a `start()`. |
| `RUNNABLE` | Durante el procesamiento activo. | Cuando el hilo está ejecutando instrucciones en su `run()`. |
| `TIMED_WAITING` | Durante las pausas entre mediciones. | Invocación de `Thread.sleep(periodoMs)`. |
| `TERMINATED` | Al finalizar todas las mediciones. | Cuando el método `run()` concluye. |

* **¿Por qué termina el programa si el vigilante sigue en su bucle `while(true)`?**  
  Porque el hilo se configuró como demonio con `vigilante.setDaemon(true)`. La JVM apaga automáticamente los hilos demonio tan pronto como todos los hilos de usuario principales (`main`, sensores y riego) han finalizado sus tareas.

---

### Comparativa: `Thread` frente a `Runnable`

| Aspecto | `Thread` (`extends Thread`) | `Runnable` (`implements Runnable`) |
| :--- | :--- | :--- |
| **Concepto** | Representa el hilo de ejecución en sí. | Representa únicamente la tarea a ejecutar. |
| **Herencia** | Impide heredar de otra clase base. | Permite que la clase herede de otra (ej. `Sensor`). |
| **Obtención del nombre** | Directa mediante `getName()`. | Indirecta mediante `Thread.currentThread().getName()`. |
| **Reutilización / Flexibilidad** | Alto acoplamiento entre tarea y soporte. | Alto desacoplamiento, facilitando reutilización. |

* **¿Por qué los sensores SOLO pueden ser `Runnable` en este diseño?**  
  Porque Java no admite herencia múltiple. Al heredar obligatoriamente de la clase abstracta `Sensor`, se imposibilita extender de la clase `Thread`.
* **¿Qué forma elegirías para el riego y por qué?**  
  Implementar `Runnable`. Aunque extender `Thread` resulte rápido para un ejercicio, `Runnable` separa la lógica de negocio de la infraestructura de concurrencia y sigue los principios de diseño orientados a objetos.
