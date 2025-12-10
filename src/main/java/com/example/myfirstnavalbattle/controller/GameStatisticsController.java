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
 * @author 4rias01
 */
public class GameStatisticsController {

    @FXML
    private Label labelStats;

    // TODO: Create a static instance that can be accessed from anywhere
    private static GameStatistics gameStats = new GameStatistics();

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
