package com.example.myfirstnavalbattle.model;

import com.example.myfirstnavalbattle.controller.SetupController;
import com.example.myfirstnavalbattle.controller.setupStage.Cell;
import com.example.myfirstnavalbattle.controller.setupStage.Ship;

import java.util.ArrayList;

/**
 * Represents the game board.
 * Manages cells and ships.
 * 
 * @author David - Brandon
 */
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

    /**
     * Reconstructs Board from saved state
     */
    public Board(com.example.myfirstnavalbattle.model.dto.BoardState state) {
        size = SetupController.GRID_SIZE;
        cells = new ModelCell[size][size];
        ships = new ArrayList<>();

        // Restore ships first to link them to cells
        for (com.example.myfirstnavalbattle.model.dto.ShipState ss : state.getShips()) {
            Ship ship = new Ship(ss.getSize());
            if (ship.isVertical() != ss.isVertical()) {
                ship.rotateShip();
            }
            ship.setUserData(new int[] { ss.getX(), ss.getY() });
            ships.add(ship);
        }

        // Restore cells. Note: We need to link cells to ships if status is
        // SHIP/HIT/KILLED
        // But ModelCell stores the Ship object.
        // So we iterate cells, set status. If status implies a ship, we find which ship
        // covers this cell.

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ModelCell.Status status = state.getCellStatuses()[i][j];
                ModelCell cell = new ModelCell();
                cell.setStatus(status);

                if (status == ModelCell.Status.SHIP || status == ModelCell.Status.HIT
                        || status == ModelCell.Status.KILLED) {
                    for (Ship s : ships) {
                        if (isPointInShip(i, j, s)) {
                            cell.setShip(s);
                            break;
                        }
                    }
                }
                cells[i][j] = cell;
            }
        }
    }

    private boolean isPointInShip(int r, int c, Ship s) {
        int[] coords = (int[]) s.getUserData();
        int sr = coords[0];
        int sc = coords[1];
        if (s.isVertical()) {
            return c == sc && r >= sr && r < sr + s.getSize();
        } else {
            return r == sr && c >= sc && c < sc + s.getSize();
        }
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
            int attempts = 0;

            while (attempts < 100) { // Limit attempts to avoid infinite loops
                boolean vertical = Math.random() < 0.5;

                // FIX: Adjust bounds based on size to avoid OutOfBounds exception
                int maxRow = vertical ? (10 - size) : 9;
                int maxCol = vertical ? 9 : (10 - size);

                // Add 1 to range because Math.random() * N is [0, N)
                int row = (int) (Math.random() * (maxRow + 1));
                int col = (int) (Math.random() * (maxCol + 1));

                if (canBePlaceRandom(row, col, vertical, size)) {
                    if (ship.isVertical() != vertical) {
                        ship.rotateShip();
                    }
                    ship.setUserData(new int[] { row, col });
                    setModelCellsState(ship, ModelCell.Status.SHIP);
                    break;
                }
                attempts++;
            }
        }
    }

    private boolean canBePlaceRandom(int row, int col, boolean vertical, int size) {
        int init = vertical ? row : col;
        for (int target = init; target < init + size; target++) {

            ModelCell cell;
            if (vertical) {
                cell = getCell(target, col);
            } else {
                cell = getCell(row, target);
            }

            if (cell == null) {
                return false;
            }
            if (cell.getStatus() == ModelCell.Status.SHIP) {
                return false;
            }
        }
        return true;
    }

    private void setModelCellsState(Ship targetShip, ModelCell.Status status) {
        // If vertical, iterate row and col remains fixed
        // if horizontal, row remains fixed and iterate col
        int[] coords = (int[]) targetShip.getUserData();
        int shipRow = coords[0];
        int shipCol = coords[1];
        boolean vertical = targetShip.isVertical();
        int size = targetShip.getSize();

        int init = vertical ? shipRow : shipCol; // variable that will iterate the loop.

        for (int target = init; target < init + size; target++) {

            ModelCell cell;
            if (vertical) {
                cell = getCell(target, shipCol); // iterate row
            } else {
                cell = getCell(shipRow, target); // iterate col
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
        } else if (cell.getStatus() == ModelCell.Status.MISS) {
            return ModelCell.Status.MISS;
        } else {
            // It is SHIP, HIT or KILLED
            Ship targetShip = cell.getShip();

            // Safety check: if ship is null (shouldn't happen for SHIP status, but safe for
            // stability)
            if (targetShip == null) {
                return cell.getStatus();
            }

            cell.setStatus(ModelCell.Status.HIT);

            if (isShipAlive(targetShip)) {
                return ModelCell.Status.HIT;
            } else {
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

        int init = vertical ? shipRow : shipCol;

        for (int target = init; target < init + size; target++) {

            ModelCell cell;
            if (vertical) {
                cell = getCell(target, shipCol);
            } else {
                cell = getCell(shipRow, target);
            }
            assert cell != null;

            if (cell.getStatus() == ModelCell.Status.SHIP) {
                return true;
            }
        }
        return false;
    }

    public boolean stillShipsAlive() {
        for (Ship ship : ships) {
            if (isShipAlive(ship)) {
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
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IllegalArgumentException("Coordinates out of bounds: [" + row + "," + col + "]");
        }
        return cells[row][col];
    }
}
