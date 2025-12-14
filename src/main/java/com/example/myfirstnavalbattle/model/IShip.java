package com.example.myfirstnavalbattle.model;

import javafx.scene.image.Image;

/**
 * Interface for Ship objects.
 * Defines the contract that all ships must follow.
 * Demonstrates SOLID principles (Interface Segregation, Open/Closed).
 * 
 * @author David - Brandon
 */
public interface IShip {

    /**
     * @return the size of the ship (e.g., 4 cells)
     */
    int getSize();

    /**
     * @return true if the ship is placed vertically, false if horizontal
     */
    boolean isVertical();

    /**
     * @return the visual representation of the ship
     */
    Image getImage();

    /**
     * @return user data associated with the ship (e.g., coordinates)
     */
    Object getUserData();
}
