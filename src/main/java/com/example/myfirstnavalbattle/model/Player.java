package com.example.myfirstnavalbattle.model;

// imports
import com.example.myfirstnavalbattle.controller.setupStage.Cell;
import com.example.myfirstnavalbattle.controller.setupStage.Ship;
import java.util.ArrayList;

/**
 * class Player
 * represents a player in the game of BattleShip
 *
 * @author 4rias01
 */
public class Player {
    private final Board board;
    private final String playerName;
    private final Characters character;
    private boolean hasPlayed;
    private boolean hasLost;

    /**
     * Constructor for the Player Class
     *
     * @param playerName the name of the user
     * @param setupCells the cells from the SetupController Class
     * @param setupShips the ships from the SetupController Class
     * @param character the global character selected
     */
    public Player (String playerName, Cell[][] setupCells, ArrayList<Ship> setupShips, Characters character) {
        this.board = new Board(setupCells, setupShips);
        this.playerName = playerName;
        this.character = character;
        hasPlayed = false;
        hasLost = false;
    }

    /**
     * Another constructor of the Player Class
     * Represents the IA
     */
    public Player () {
        board = new Board();
        character = new Characters("DEFAULT");
        playerName = "IA";
        hasPlayed = false;
        hasLost = false;
    }

    /**
     * Makes a shot at the board in the specific coordinates.
     * Update the hasLost boolean based on whether any ships alive.
     * Returns the {@link ModelCell.Status} indicating the result of the shot.
     *
     * @param row the first coordinate of the shot
     * @param col the second coordinate of the shot
     * @return the status of the cell after the shot
     */
    public ModelCell.Status shoot(int row, int col) {
        ModelCell.Status status = board.shoot(row, col);
        if (status == ModelCell.Status.MISS){
            hasPlayed = true;
        }
        else if (status == ModelCell.Status.KILLED){
            if (!board.stillShipsAlive()){
                hasLost = true;
            }
        }
        return status;
    }

    public Board getBoard() { return board; }
    public String getPlayerName() { return playerName; }
    public Characters getCharacter() { return character; }
    public boolean isHasPlayed() { return hasPlayed; }
    public boolean isHasLost() { return hasLost; }

    public void setHasPlayed(boolean hasPlayed) { this.hasPlayed = hasPlayed; }
}
