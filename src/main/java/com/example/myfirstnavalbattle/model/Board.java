package com.example.myfirstnavalbattle.model;

import com.example.myfirstnavalbattle.controller.SetupController;
import com.example.myfirstnavalbattle.controller.setupStage.Cell;
import com.example.myfirstnavalbattle.controller.setupStage.Ship;

import java.util.ArrayList;

public class Board {

    ModelCell[][] cells;
    ArrayList<Ship> ships;

    int size;

    public Board() {
        size = SetupController.GRID_SIZE;
        cells = new ModelCell[size][size];
        ships = new ArrayList<>();

        initRandomBoard(size);
    }

    public Board(Cell[][] setupCells, ArrayList<Ship> setupShips) {
        size = SetupController.GRID_SIZE;

        cells = new ModelCell[size][size];
        ships = new ArrayList<>();

        initBoard(setupCells, setupShips);
    }

    private void initBoard(Cell[][] setupCellsArray, ArrayList<Ship> setupShips) {
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                ModelCell modelCell = new ModelCell(setupCellsArray[row][col]);
                cells[row][col] = modelCell;
            }
        }
        initPlayerShip(setupShips);
    }

    private void initRandomBoard(int sizeOfGrid) {
        ships.add(new Ship(1));
        ships.add(new Ship(1));
        ships.add(new Ship(1));
        ships.add(new Ship(1));
        ships.add(new Ship(2));
        ships.add(new Ship(2));
        ships.add(new Ship(2));
        ships.add(new Ship(3));
        ships.add(new Ship(3));
        ships.add(new Ship(4));

        for (int row = 0; row < sizeOfGrid; row++) {
            for (int col = 0; col < sizeOfGrid; col++) {
                ModelCell iaCell = new ModelCell();
                cells[row][col] = iaCell;
            }
        }
        generateRandomBoard();
    }

    private void generateRandomBoard() {
        for (Ship ship : ships) {
            int size = ship.getSize();

            while (true) {
                boolean vertical = Math.random() < 0.5;
                int row = (int)(Math.random() * 10);
                int col = (int)(Math.random() * 10);
                if (canBePlaceRandom(row, col, vertical, size)) {
                    if (ship.isVertical() != vertical) {
                        ship.rotateShip();
                    }
                    ship.setUserData( new int[] {row, col} );
                    setModelCellsState(ship, ModelCell.Status.SHIP);
                    break;
                }
            }
        }
    }

    private boolean canBePlaceRandom(int row, int col, boolean vertical, int size) {
        int init = vertical ? row : col;
        for (int target = init; target < init + size; target++) {

            ModelCell cell;
            if (vertical) {
                cell = getCell(target,col);
            }
            else {
                cell = getCell(row,target);
            }

            if (cell == null) { return false; }
            if (cell.getStatus() == ModelCell.Status.SHIP) { return false; }
        }
        return true;
    }

    private void setModelCellsState(Ship targetShip, ModelCell.Status status) {
        // Si es vertical, itera el row y col permanece fijo
        // si es horizontal, el row permanece fijo y itera el col
        int[] coords = (int[]) targetShip.getUserData();
        int shipRow = coords[0];
        int shipCol = coords[1];
        boolean vertical = targetShip.isVertical();
        int size = targetShip.getSize();


        int init = vertical? shipRow : shipCol; // variable que ira iterando el for.

        for (int target = init; target < init + size; target++) {

            ModelCell cell;
            if (vertical) {
                cell = getCell(target, shipCol); //iteras el row
            }
            else{
                cell = getCell(shipRow, target); //iteras el col
            }
            assert cell != null;
            cell.setStatus(status);
            cell.setShip(targetShip);
        }
    }

    private void initPlayerShip(ArrayList<Ship> setupShips) {
        ships.addAll(setupShips);
    }

    public ModelCell.Status shoot(int row, int col) {
        ModelCell cell = getCell(row, col);

        if (cell.getStatus() == ModelCell.Status.EMPTY) {
            cell.setStatus(ModelCell.Status.MISS);
            return ModelCell.Status.MISS;
        }
        else{
            cell.setStatus(ModelCell.Status.HIT);
            Ship targetShip = cell.getShip();

            if(isShipAlive(targetShip)) {
                return ModelCell.Status.HIT;
            }
            else{
                setModelCellsState(targetShip, ModelCell.Status.KILLED);
                return ModelCell.Status.KILLED;
            }
        }
    }

    private boolean isShipAlive(Ship targetShip) {
        int[] coords = (int[]) targetShip.getUserData();
        int shipRow = coords[0];
        int shipCol = coords[1];
        boolean vertical = targetShip.isVertical();
        int size = targetShip.getSize();

        int init = vertical? shipRow : shipCol;

        for (int target = init; target < init + size; target++) {

            ModelCell cell;
            if (vertical) {
                cell = getCell(target, shipCol);
            }
            else{
                cell = getCell(shipRow, target);
            }
            assert cell != null;

            if (cell.getStatus() == ModelCell.Status.SHIP) {
                return true;
            }
        }
        return false;
    }

    public boolean stillShipsAlive(){
        for (Ship ship : ships) {
            if(isShipAlive(ship)){
                return true;
            }
        }
        return false;
    }

    public Ship getShip(int row, int col) {
        return getCell(row, col).getShip();
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public ModelCell getCell(int row, int col) {
        if (row >= size || col >= size) {
            return null;
        }
        return cells[row][col];
    }
}
