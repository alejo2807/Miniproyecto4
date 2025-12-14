package com.example.myfirstnavalbattle.controller;

/**
 * Controller for the Character Selection screen.
 * Allows the user to browse and select a character for the game.
 * 
 * @author David - Brandon
 */

import com.example.myfirstnavalbattle.model.Characters;
import com.example.myfirstnavalbattle.model.SelectCharacter;
import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

public class CharacterController {

    private static final List<Characters> characters = SelectCharacter.loadCharacters();
    private Characters actualCharacter;
    private Characters selectedCharacter;

    @FXML
    private Label nameLabel;
    @FXML
    private ImageView characterImage;
    @FXML
    private Button selectButton;

    private final Button defaultButton = new Button();

    public CharacterController() {
    }

    @FXML
    private void initialize() {
        defaultButton.setStyle(selectButton.getStyle());

        if (SelectCharacter.getSelectedCharacter() != null) {
            selectedCharacter = SelectCharacter.getSelectedCharacter();
        }

        updateCharacterView(0);

    }

    @FXML
    private void handleLeftButton() {
        int index = characters.indexOf(actualCharacter);
        if (index == 0) {
            index = characters.size() - 1;
        } else {
            index--;
        }

        updateCharacterView(index);
    }

    @FXML
    private void handleRightButton() {
        int index = characters.indexOf(actualCharacter);
        if (index == 4) {
            index = 0;
        } else {
            index++;
        }
        updateCharacterView(index);
    }

    @FXML
    private void updateCharacterView(int index) {
        actualCharacter = characters.get(index);
        nameLabel.setText(actualCharacter.getName());
        characterImage.setImage(actualCharacter.getImage());
        if (actualCharacter == selectedCharacter) {
            selectButton.setText("¡Seleccionado!");
            selectButton.setStyle("-fx-background-color: white;" + "-fx-text-fill: black");
        } else {
            selectButton.setText("Seleccionar");
            selectButton.setStyle(defaultButton.getStyle());
        }

    }

    @FXML
    private void handleBackButton() throws IOException {
        SceneManager.switchTo("HomeScene");
    }

    @FXML
    private void handleSelectCharacter() {
        selectedCharacter = actualCharacter;
        SelectCharacter.selectCharacter(actualCharacter);
        selectButton.setText("¡Seleccionado!");
        selectButton.setStyle("-fx-background-color: white;" + "-fx-text-fill: black");
    }

}
