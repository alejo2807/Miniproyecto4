package com.example.myfirstnavalbattle.model;

import com.example.myfirstnavalbattle.controller.setupStage.Cell;
import com.example.myfirstnavalbattle.controller.setupStage.Ship;

/**
 * Represents a logical cell in the board's model.
 * Contains the status of the cell and any ship present.
 * 
 * @author David - Brandon
 */
public class ModelCell {

    public enum Status {
        EMPTY,
        SHIP,
        MISS,
        HIT,
        KILLED
    }

    private Status status;

    private Ship ship;

    public ModelCell(Cell cell) {
        this.ship = cell.getShip();

        this.status = switch (cell.getStatus()) {
            case EMPTY -> Status.EMPTY;
            case SHIP -> Status.SHIP;
            default -> null;
        };
    }

    public ModelCell() {
        ship = null;
        status = Status.EMPTY;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public Status getStatus() {
        return status;
    }
}
