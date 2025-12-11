package com.example.myfirstnavalbattle.controller.setupStage;

import javafx.css.PseudoClass;
import javafx.scene.layout.StackPane;

public class Cell extends StackPane {
    public enum Status {
        EMPTY,
        SHIP,
        OVER
    }

    private Ship ship;
    private final int row;
    private final int col;

    private static final PseudoClass EMPTY_PSEUDO = PseudoClass.getPseudoClass("empty");
    private static final PseudoClass SHIP_PSEUDO = PseudoClass.getPseudoClass("ship");
    private static final PseudoClass OVER_PSEUDO = PseudoClass.getPseudoClass("over");

    private Status status;


    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        setStatus(Status.EMPTY);
        getStyleClass().add("cell");
    }


    public void setStatus(Status status) {
        this.status = status;

        pseudoClassStateChanged(EMPTY_PSEUDO, false);
        pseudoClassStateChanged(SHIP_PSEUDO, false);
        pseudoClassStateChanged(OVER_PSEUDO, false);

        // Activar solo la pseudo-clase correspondiente
        switch (status) {
            case EMPTY -> pseudoClassStateChanged(EMPTY_PSEUDO, true);
            case SHIP -> pseudoClassStateChanged(SHIP_PSEUDO, true);
            case OVER -> pseudoClassStateChanged(OVER_PSEUDO, true);
        }
    }
    public Status getStatus() { return status; }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public Ship getShip() { return ship; }
    public int getRow() { return row; }
    public int getCol() { return col; }
}
