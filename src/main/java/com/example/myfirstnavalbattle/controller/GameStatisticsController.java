package com.example.myfirstnavalbattle.controller;

import com.example.myfirstnavalbattle.model.GameStatistics;
import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controller for the Game Statistics Scene
 * Displays player statistics and handles navigation back to home
 * 
 * @author David - Brandon
 */
public class GameStatisticsController {

    @FXML
    private Label labelStats;

    // Shared game statistics instance (Singleton pattern)
    private static GameStatistics gameStats = GameStatistics.getInstance();

    @FXML
    public void initialize() {
        // Update label with the summary of statistics
        if (labelStats != null) {
            labelStats.setText(gameStats.getSummary());
        }
    }

    /**
     * Gets the shared game statistics instance
     */
    public static GameStatistics getGameStatistics() {
        return gameStats;
    }

    /**
     * Handles the back button to return to home scene
     */
    @FXML
    private void handleBack() {
        try {
            SceneManager.switchTo("HomeScene");
        } catch (IOException e) {
            System.err.println("Error returning to home: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
