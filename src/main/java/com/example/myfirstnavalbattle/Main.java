package com.example.myfirstnavalbattle;

/**
 * Main application class.
 * Starts the JavaFX application and sets up the initial scene.
 * 
 * @author David - Brandon
 */

import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage mainStage) throws IOException {
        loadFonts();

        SceneManager.setStage(mainStage);
        SceneManager.switchTo("HomeScene");

        mainStage.setTitle("Battleship War Z");
        mainStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/com/example/myfirstnavalbattle/images/sceneImages/favicon.jpg"))));
        mainStage.show();
    }

    private void loadFonts() {
        InputStream fontSansBold = getClass().getResourceAsStream(
                "/com/example/myfirstnavalbattle/Fonts/OpenSans-Bold.ttf");
        InputStream fontSansRegular = getClass().getResourceAsStream(
                "/com/example/myfirstnavalbattle/Fonts/OpenSans-Regular.ttf");

        Font.loadFont(fontSansBold, 12);
        Font.loadFont(fontSansRegular, 12);
    }

    public static void main(String[] args) {
        launch();
    }
}
