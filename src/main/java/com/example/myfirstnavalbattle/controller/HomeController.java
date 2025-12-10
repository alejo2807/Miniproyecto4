package com.example.myfirstnavalbattle.controller;

import com.example.myfirstnavalbattle.model.Characters;
import com.example.myfirstnavalbattle.model.SelectCharacter;
import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.control.Button;
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
    private void handleContinue() {
        System.out.println("Continue");
    }

    @FXML
    private void handleCharacter() throws IOException {
        SceneManager.switchTo("CharacterScene");
    }

    @FXML
    private void handleSettings() throws IOException {
        SceneManager.switchTo("GameStatisticsScene");
    }
<<<<<<< HEAD

    @FXML
    private void handleChangeProfile() throws IOException {
        // SceneManager.switchTo("ProfileScene");
    }
=======
>>>>>>> origin/main
}
