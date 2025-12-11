package com.example.myfirstnavalbattle.model;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Hilo para llevar el tiempo de juego en tiempo real.
 * Actualiza un Label en la UI cada segundo mostrando el tiempo transcurrido
 * en formato MM:SS.
 * 
 * Demuestra: Thread-safe UI updates con Platform.runLater(),
 * control de ciclo de vida de hilos
 * 
 * @author 4rias01
 */
public class GameTimerThread extends Thread {

    private volatile boolean running = true;
    private long startTime;
    private Label timerLabel;

    /**
     * Constructor del hilo temporizador
     * 
     * @param timerLabel Label de JavaFX donde se mostrará el tiempo
     */
    public GameTimerThread(Label timerLabel) {
        super("Game-Timer-Thread");
        this.timerLabel = timerLabel;
        this.startTime = System.currentTimeMillis();
        System.out.println("[TEMPORIZADOR] Thread creado");
    }

    @Override
    public void run() {
        System.out.println("[TEMPORIZADOR] Iniciando conteo...");

        while (running) {
            try {
                // Calcular tiempo transcurrido
                long elapsedMillis = System.currentTimeMillis() - startTime;
                long seconds = (elapsedMillis / 1000) % 60;
                long minutes = (elapsedMillis / 1000) / 60;

                // Formatear tiempo
                final String timeString = String.format("%02d:%02d", minutes, seconds);

                // Actualizar UI de forma thread-safe
                Platform.runLater(() -> {
                    if (timerLabel != null) {
                        timerLabel.setText(timeString);
                    }
                });

                // Esperar 1 segundo antes de la siguiente actualización
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                System.out.println("[TEMPORIZADOR] Thread interrumpido");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[TEMPORIZADOR] Error: " + e.getMessage());
            }
        }

        System.out.println("[TEMPORIZADOR] Thread detenido");
    }

    /**
     * Detiene el temporizador de forma segura
     */
    public void stopTimer() {
        running = false;
        this.interrupt(); // Interrumpir el sleep para detener inmediatamente
        System.out.println("[TEMPORIZADOR] Señal de detención enviada");
    }

    /**
     * Obtiene el tiempo transcurrido en milisegundos
     * 
     * @return tiempo transcurrido desde el inicio
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Obtiene el tiempo transcurrido formateado como String
     * 
     * @return tiempo en formato MM:SS
     */
    public String getFormattedTime() {
        long elapsedMillis = getElapsedTime();
        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
