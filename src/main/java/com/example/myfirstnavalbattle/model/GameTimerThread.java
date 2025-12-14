package com.example.myfirstnavalbattle.model;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Thread to track game time in real time.
 * Updates a UI Label every second showing elapsed time
 * in MM:SS format.
 * 
 * Demonstrates: Thread-safe UI updates with Platform.runLater(),
 * thread lifecycle control.
 * 
 * @author David - Brandon
 */
public class GameTimerThread extends Thread {

    private volatile boolean running = true;
    private long startTime;
    private Label timerLabel;

    /**
     * Game Timer Thread Constructor
     * 
     * @param timerLabel JavaFX Label where time will be shown
     */
    public GameTimerThread(Label timerLabel) {
        super("Game-Timer-Thread");
        this.timerLabel = timerLabel;
        this.startTime = System.currentTimeMillis();
        System.out.println("[TIMER] Thread created");
    }

    @Override
    public void run() {
        System.out.println("[TIMER] Starting count...");

        while (running) {
            try {
                // Calculate elapsed time
                long elapsedMillis = System.currentTimeMillis() - startTime;
                long seconds = (elapsedMillis / 1000) % 60;
                long minutes = (elapsedMillis / 1000) / 60;

                // Format time
                final String timeString = String.format("%02d:%02d", minutes, seconds);

                // Update UI thread-safely
                Platform.runLater(() -> {
                    if (timerLabel != null) {
                        timerLabel.setText(timeString);
                    }
                });

                // Wait 1 second before next update
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                System.out.println("[TIMER] Thread interrupted");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[TIMER] Error: " + e.getMessage());
            }
        }

        System.out.println("[TIMER] Thread stopped");
    }

    /**
     * Stops the timer safely
     */
    public void stopTimer() {
        running = false;
        this.interrupt(); // Interrupt sleep to stop immediately
        System.out.println("[TIMER] Stop signal sent");
    }

    /**
     * Gets elapsed time in milliseconds
     * 
     * @return elapsed time since start
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * Gets elapsed time formatted as String
     * 
     * @return time in MM:SS format
     */
    public String getFormattedTime() {
        long elapsedMillis = getElapsedTime();
        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
