package com.example.myfirstnavalbattle.controller;

import com.example.myfirstnavalbattle.model.GameStatistics;
import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controlador para la escena de estadísticas del jugador
 */
public class GameStatisticsController {

    @FXML
    private Label labelStats;

    @FXML
    public void initialize() {
        // Obtener la instancia singleton de GameStatistics
        GameStatistics stats = GameStatistics.getInstance();

        // Actualizar el label con el resumen de estadísticas
        labelStats.setText(stats.getSummary());
    }

    @FXML
    private void handleBack() throws IOException {
        SceneManager.switchTo("HomeScene");
    }
}
