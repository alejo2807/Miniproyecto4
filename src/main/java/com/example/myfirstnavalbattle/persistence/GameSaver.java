package com.example.myfirstnavalbattle.persistence;

import com.example.myfirstnavalbattle.model.dto.GameState;
import java.io.*;

/**
 * Handles binary serialization of GameState to .ser files
 */
public class GameSaver {

    private static final String DATA_FOLDER = "data";

    /**
     * Saves the current game state to a binary file
     * 
     * @param state      The game state DTO
     * @param playerName The name of the player (used for filename)
     * @return true if successful
     */
    public static boolean saveGame(GameState state, String playerName) {
        String filename = DATA_FOLDER + File.separator + playerName + ".ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(state);
            System.out.println("Game saved successfully to " + filename);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads a game state from a binary file
     * 
     * @param playerName The name of the player to load
     * @return The GameState DTO or null if not found/error
     */
    public static GameState loadGame(String playerName) {
        String filename = DATA_FOLDER + File.separator + playerName + ".ser";
        File file = new File(filename);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if a saved game exists for the player
     */
    public static boolean hasSavedGame(String playerName) {
        String filename = DATA_FOLDER + File.separator + playerName + ".ser";
        return new File(filename).exists();
    }
}
