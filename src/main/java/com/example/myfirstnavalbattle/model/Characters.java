package com.example.myfirstnavalbattle.model;

/**
 * Represents a character in the game.
 * Stores the name and image of the character (Captain).
 * 
 * @author David - Brandon
 */

import javafx.scene.image.Image;

import java.util.Objects;

public class Characters {

    private final String name;
    private final Image image;

    public Characters(String name) {
        this.name = "Capitán " + name;
        this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/com/example/myfirstnavalbattle/Images/captains/CAPTAIN" + name.toUpperCase() + ".png")));
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

}
