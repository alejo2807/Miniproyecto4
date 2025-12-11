package com.example.myfirstnavalbattle.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * SceneManager Class
 * Switch the scene/root in the whole application
 *
 * @author GG2R10
 */
public class SceneManager {
    private static Scene mainScene;

    /**
     * Sets up and displays the main stage of the application
     * <p>
     * This method loads the HomeScene FXML file, initializes the main scene,
     * applies global cursor and button animations, and sets the scene to full screen.
     *
     * @param stage the primary {@link Stage} where the scene will be set
     * @throws IOException if the FXML file cannot be loaded
     */
    public static void setStage(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(
                "/com/example/myfirstnavalbattle/scenes/HomeSceneView/HomeScene.fxml"));
        Parent root = loader.load();

        mainScene = new Scene(root);
        AnimationsManager.applyGlobalCursor(mainScene);
        AnimationsManager.applyToAllButtons(root);

        stage.setScene(mainScene);
        stage.setFullScreen(true);
    }

    /**
     * Switches the current scene to the one specified by {@code sceneName}.
     * <p>
     * This method loads the corresponding FXML file located at
     * {@code /scenes/[sceneName]View/[sceneName].fxml}, applies global cursor settings
     * and animations to all buttons, and updates the root of the main scene.
     *
     * @param sceneName the name of the scene to switch to (must match the FXML folder and file name)
     * @throws IOException if the FXML file cannot be found or loaded
     */
    public static void switchTo(String sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(
                "/com/example/myfirstnavalbattle/scenes/"+ sceneName + "View/" + sceneName + ".fxml"));
        Parent newRoot = loader.load();

        AnimationsManager.applyGlobalCursor(mainScene);
        AnimationsManager.applyToAllButtons(newRoot);

        mainScene.setRoot(newRoot);
    }
}
