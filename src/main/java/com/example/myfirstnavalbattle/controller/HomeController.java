package com.example.myfirstnavalbattle.controller;

/**
 * Controller for the Home screen.
 * Handles navigation to setup, settings, and profile selection.
 * 
 * @author David - Brandon
 */

import com.example.myfirstnavalbattle.model.Characters;
import com.example.myfirstnavalbattle.model.SelectCharacter;
import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.control.Button;
import com.example.myfirstnavalbattle.model.GameStatistics;
import com.example.myfirstnavalbattle.model.Player;
import com.example.myfirstnavalbattle.model.dto.GameState;
import com.example.myfirstnavalbattle.persistence.GameSaver;
import com.example.myfirstnavalbattle.model.Board;
import javafx.scene.control.Alert;
import java.io.IOException;

public class HomeController {
    @FXML
    private Circle circleCharacter;
    Characters selectedCharacter;
    @FXML
    private Button settingsButton;

    @FXML
    private void initialize() {
        selectedCharacter = SelectCharacter.getSelectedCharacter();

        Image image = selectedCharacter.getImage();
        circleCharacter.setFill(new ImagePattern(image));
    }

    @FXML
    private void handlePlay() throws IOException {
        SceneManager.switchTo("SetupScene");
    }

    @FXML
    private void handleContinue() throws IOException {
        String currentPlayer = GameStatistics.getInstance().getCurrentProfileName();
        if (GameSaver.hasSavedGame(currentPlayer)) {
            GameState state = GameSaver.loadGame(currentPlayer);
            if (state != null) {
                // Reconstruct Players
                Board pBoard = new Board(state.getPlayerBoard());
                Board iaBoard = new Board(state.getIaBoard());

                Player p1 = new Player(state.getPlayerName(), pBoard);
                Player ia = new Player(iaBoard); // Uses the constructor we added, assumes IA name

                // Restore turn/played state logic if needed, but handled by
                // turnCounter/nextTurn usually?
                // GameState has 'isPlayerTurn'. We might need to set it in GameController?
                // GameController uses local 'TurnInfo' and 'turnCounter'.
                // We should pass 'turnCounter' too? GameController static? No.
                // For now, simple restoration.

                GameController.setPlayerOne(p1);
                GameController.setPlayerIA(ia);

                // Set pending state to restore stats in GameController
                GameController.setPendingLoadState(state);

                SceneManager.switchTo("GameScene");

                SceneManager.switchTo("GameScene");
            } else {
                showError("Error", "No se pudo cargar la partida guardada.");
            }
        } else {
            showError("Información", "No tienes partidas guardadas para el perfil: " + currentPlayer);
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

    @FXML
    private void handleCharacter() throws IOException {
        SceneManager.switchTo("CharacterScene");
    }

    @FXML
    private void handleSettings() throws IOException {
        SceneManager.switchTo("GameStatisticsScene");
    }

    @FXML
    private void handleChangeProfile() throws IOException {
        SceneManager.switchTo("AccountSelection");
    }
}
