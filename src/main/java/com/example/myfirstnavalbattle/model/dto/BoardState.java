package com.example.myfirstnavalbattle.model.dto;

import com.example.myfirstnavalbattle.model.ModelCell;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Data Transfer Object for Board state
 * Stores the status of each cell and the state of ships
 */
public class BoardState implements Serializable {
    private static final long serialVersionUID = 1L;

    private ModelCell.Status[][] cellStatuses;
    private ArrayList<ShipState> ships;

    public BoardState(int size) {
        cellStatuses = new ModelCell.Status[size][size];
        ships = new ArrayList<>();
    }

    public ModelCell.Status[][] getCellStatuses() {
        return cellStatuses;
    }

    public void setCellStatuses(ModelCell.Status[][] cellStatuses) {
        this.cellStatuses = cellStatuses;
    }

    public ArrayList<ShipState> getShips() {
        return ships;
    }

    public void setShips(ArrayList<ShipState> ships) {
        this.ships = ships;
    }

    public void addShip(ShipState ship) {
        this.ships.add(ship);
    }
}
