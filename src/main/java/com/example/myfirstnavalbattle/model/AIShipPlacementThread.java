package com.example.myfirstnavalbattle.model;

/**
 * Hilo para posicionar los barcos de la IA en segundo plano.
 * Mientras el jugador posiciona sus barcos manualmente, este hilo
 * genera el tablero de la IA de manera concurrente.
 * 
 * Demuestra: Concurrencia, sincronización con volatile, y uso de Thread
 * 
 * @author 4rias01
 */
public class AIShipPlacementThread extends Thread {

    private Board aiBoard;
    private volatile boolean isComplete = false;
    private volatile boolean shouldStart = false;
    private final Object lock = new Object();

    /**
     * Constructor del hilo de posicionamiento de IA
     */
    public AIShipPlacementThread() {
        super("AI-Ship-Placement-Thread");
        System.out.println("[HILO IA] Thread creado y listo para iniciar");
    }

    @Override
    public void run() {
        System.out.println("[HILO IA] Thread ejecutándose, esperando señal para comenzar...");

        // Esperar señal para comenzar
        synchronized (lock) {
            while (!shouldStart) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    System.err.println("[HILO IA] Interrumpido mientras esperaba");
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        System.out.println("[HILO IA] Señal recibida! Comenzando posicionamiento de barcos...");

        try {
            // Simular un pequeño delay para hacer visible la concurrencia
            Thread.sleep(500);

            // Crear y generar el tablero de la IA
            aiBoard = new Board();

            System.out.println("[HILO IA] ✓ Tablero de IA generado exitosamente");

            // Marcar como completado
            synchronized (lock) {
                isComplete = true;
                lock.notifyAll();
            }

            System.out.println("[HILO IA] Posicionamiento completado!");

        } catch (InterruptedException e) {
            System.err.println("[HILO IA] Interrumpido durante posicionamiento");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.err.println("[HILO IA] Error durante posicionamiento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Señala al hilo que puede comenzar el posicionamiento
     */
    public void signalStart() {
        synchronized (lock) {
            shouldStart = true;
            lock.notifyAll();
        }
        System.out.println("[HILO PRINCIPAL] Señal enviada al hilo IA para comenzar");
    }

    /**
     * Espera a que el hilo complete el posicionamiento
     * 
     * @throws InterruptedException si el hilo es interrumpido mientras espera
     */
    public void waitForCompletion() throws InterruptedException {
        synchronized (lock) {
            while (!isComplete) {
                System.out.println("[HILO PRINCIPAL] Esperando a que IA complete posicionamiento...");
                lock.wait();
            }
        }
        System.out.println("[HILO PRINCIPAL] IA ha completado el posicionamiento");
    }

    /**
     * Obtiene el tablero generado por la IA
     * 
     * @return Board generado, o null si aún no está completo
     */
    public Board getAIBoard() {
        return aiBoard;
    }

    /**
     * Verifica si el posicionamiento está completo
     * 
     * @return true si está completo, false en caso contrario
     */
    public boolean isComplete() {
        return isComplete;
    }
}
