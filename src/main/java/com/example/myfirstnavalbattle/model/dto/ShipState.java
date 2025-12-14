package com.example.myfirstnavalbattle.model.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for Ship state
 * Allows saving ship information without JavaFX dependencies
 * 
 * @author David - Brandon
 */
public class ShipState implements Serializable {
    private static final long serialVersionUID = 1L;

    private int x;
    private int y;
    private int size;
    private boolean vertical;
    private int health; // 0 means sunk
    private boolean isSunk;

    public ShipState(int x, int y, int size, boolean vertical, int health) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.vertical = vertical;
        this.health = health;
        this.isSunk = (health <= 0);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    public boolean isVertical() {
        return vertical;
    }

    public int getHealth() {
        return health;
    }

    public boolean isSunk() {
        return isSunk;
    }

    public void setHealth(int health) {
        this.health = health;
        this.isSunk = (health <= 0);
    }

    @Override
    public String toString() {
        return "ShipState{" +
                "size=" + size +
                ", health=" + health +
                ", sunk=" + isSunk +
                '}';
    }
}
