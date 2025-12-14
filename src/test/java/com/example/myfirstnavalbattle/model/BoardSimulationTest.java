package com.example.myfirstnavalbattle.model;

import com.example.myfirstnavalbattle.controller.SetupController;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardSimulationTest {

    /**
     * Test 1: Verify that the board initializes with the correct number of ships.
     * The default constructor creates a random board with 10 ships.
     */
    @Test
    public void testBoardInitialization() {
        Board board = new Board();
        // SetupController.GRID_SIZE is 10, check bounds if needed, but mainly ships.
        // initRandomBoard adds 10 ships (4x1, 3x2, 2x3, 1x4 = 10 ships total)
        assertEquals(10, board.getShips().size(), "Board should be initialized with 10 ships.");
        assertTrue(board.stillShipsAlive(), "Ships should be alive initially.");
    }

    /**
     * Test 2: Verify that shooting at an empty cell returns MISS.
     * Since the board is random, we search for an empty cell.
     */
    @Test
    public void testShootMiss() {
        Board board = new Board();
        boolean foundEmpty = false;

        for (int row = 0; row < SetupController.GRID_SIZE; row++) {
            for (int col = 0; col < SetupController.GRID_SIZE; col++) {
                if (board.getCell(row, col).getStatus() == ModelCell.Status.EMPTY) {
                    ModelCell.Status result = board.shoot(row, col);
                    assertEquals(ModelCell.Status.MISS, result, "Shooting at empty cell should return MISS");
                    assertEquals(ModelCell.Status.MISS, board.getCell(row, col).getStatus(),
                            "Cell status should be updated to MISS");
                    foundEmpty = true;
                    break;
                }
            }
            if (foundEmpty)
                break;
        }

        assertTrue(foundEmpty, "Should have found an empty cell to test MISS condition.");
    }

    /**
     * Test 3: Verify that shooting at a ship cell returns HIT.
     * Since the board is random, we search for a cell with a SHIP.
     */
    @Test
    public void testShootHit() {
        Board board = new Board();
        boolean foundShip = false;

        for (int row = 0; row < SetupController.GRID_SIZE; row++) {
            for (int col = 0; col < SetupController.GRID_SIZE; col++) {
                if (board.getCell(row, col).getStatus() == ModelCell.Status.SHIP) {
                    ModelCell.Status result = board.shoot(row, col);
                    // It could be HIT or KILLED depending on ship size (size 1 is killed instantly)
                    assertTrue(result == ModelCell.Status.HIT || result == ModelCell.Status.KILLED,
                            "Shooting at property should return HIT or KILLED");
                    foundShip = true;
                    break;
                }
            }
            if (foundShip)
                break;
        }

        assertTrue(foundShip, "Should have found a ship cell to test HIT condition.");
    }
}
