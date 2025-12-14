package com.example.myfirstnavalbattle.model;

/**
 * Interface to observe important game events.
 * Implements the Observer pattern to decouple game logic
 * from notification logic.
 * 
 * DEMONSTRATION: Use of INTERFACES to define contracts
 * 
 * @author David - Brandon
 */
public interface GameEventObserver {

    /**
     * Called when a ship is destroyed
     * 
     * @param shipSize          size of the destroyed ship
     * @param destroyedByPlayer true if destroyed by player, false if by AI
     */
    void onShipDestroyed(int shipSize, boolean destroyedByPlayer);

    /**
     * Called when a shot hits but doesn't destroy (yet)
     * 
     * @param row      hit row
     * @param col      hit col
     * @param byPlayer true if shot by player
     */
    void onHit(int row, int col, boolean byPlayer);

    /**
     * Called when a shot misses
     * 
     * @param row      miss row
     * @param col      miss col
     * @param byPlayer true if shot by player
     */
    void onMiss(int row, int col, boolean byPlayer);

    /**
     * Called when the game ends
     * 
     * @param playerWon true if player won, false if AI won
     */
    void onGameEnd(boolean playerWon);
}
