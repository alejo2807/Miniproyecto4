package com.example.myfirstnavalbattle.model;

import javafx.scene.image.Image;

import java.util.Objects;

public class Characters {

    private final String name;
    private final Image image;


    public Characters(String name) {
        this.name = "Capitán "+ name;
        this.image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/myfirstnavalbattle/Images/captains/CAPTAIN" + name.toUpperCase() + ".png")));
    }

    public String getName() {
        return name;
    }
    public Image getImage() {return image;}

}
