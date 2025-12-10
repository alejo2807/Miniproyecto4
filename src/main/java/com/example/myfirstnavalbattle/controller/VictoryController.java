package com.example.myfirstnavalbattle.controller;

import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Controller for the Victory Scene
 * Handles the ESC key press to return to the Home Scene
 * 
 * @author 4rias01
 */
public class VictoryController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public void initialize() {
        // Add listener for ESC key
        addKeyListener();
    }

    /**
     * Adds a key listener to detect ESC press and return to home
     */
    private void addKeyListener() {
        anchorPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        try {
                            SceneManager.switchTo("HomeScene");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        event.consume();
                    }
                });
            }
        });
    }

    /**
     * Handles the button to return to home
     */
    @FXML
    private void handleBackToHome() {
        try {
            SceneManager.switchTo("HomeScene");
        } catch (IOException e) {
            System.err.println("Error returning to home: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
