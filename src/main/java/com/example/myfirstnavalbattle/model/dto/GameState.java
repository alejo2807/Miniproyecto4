package com.example.myfirstnavalbattle.model.dto;

import java.io.Serializable;

/**
 * Data Transfer Object for the entire Game State
 * Use this to save/load matches
 */
public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private BoardState playerBoard;
    private BoardState iaBoard;
    private boolean isPlayerTurn;
    private int turnCounter;

    // We also save stats so they are accurate to the moment of saving
    private int shotsFired;
    private int hits;
    private int misses;
    private int shipsSunk;

    public GameState(String playerName, BoardState playerBoard, BoardState iaBoard,
            boolean isPlayerTurn, int turnCounter) {
        this.playerName = playerName;
        this.playerBoard = playerBoard;
        this.iaBoard = iaBoard;
        this.isPlayerTurn = isPlayerTurn;
        this.turnCounter = turnCounter;
    }

    // Getters and Setters
    public String getPlayerName() {
        return playerName;
    }

    public BoardState getPlayerBoard() {
        return playerBoard;
    }

    public BoardState getIaBoard() {
        return iaBoard;
    }

    public boolean isPlayerTurn() {
        return isPlayerTurn;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public void setGameStats(int shots, int hits, int misses, int sunk) {
        this.shotsFired = shots;
        this.hits = hits;
        this.misses = misses;
        this.shipsSunk = sunk;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public int getShipsSunk() {
        return shipsSunk;
    }
}
