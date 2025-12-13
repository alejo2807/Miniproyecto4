package com.example.myfirstnavalbattle.persistence;

import com.example.myfirstnavalbattle.model.dto.GameState;
import java.io.*;
import java.util.HashMap;

/**
 * Handles binary serialization of GameState to a single unified file.
 * Uses a HashMap<String, GameState> to store saves for all users.
 */
public class GameSaver {

    private static final String DATA_FOLDER = "data";
    private static final String UNIFIED_SAVE_FILE = DATA_FOLDER + File.separator + "saved_games.ser";

    /**
     * Saves the current game state to the unified binary file
     * 
     * @param state      The game state DTO
     * @param playerName The name of the player (key for the map)
     * @return true if successful
     */
    public static boolean saveGame(GameState state, String playerName) {
        ensureDataFolder();
        HashMap<String, GameState> allSaves = loadAllSaves();
        allSaves.put(playerName, state);
        return writeAllSaves(allSaves);
    }

    /**
     * Loads a game state from the unified binary file
     * 
     * @param playerName The name of the player to load
     * @return The GameState DTO or null if not found/error
     */
    public static GameState loadGame(String playerName) {
        HashMap<String, GameState> allSaves = loadAllSaves();
        return allSaves.get(playerName);
    }

    /**
     * Checks if a saved game exists for the player
     */
    public static boolean hasSavedGame(String playerName) {
        HashMap<String, GameState> allSaves = loadAllSaves();
        return allSaves.containsKey(playerName);
    }

    /**
     * Deletes a saved game for the player (e.g., when game is finished)
     * 
     * @param playerName The name of the player
     * @return true if successful
     */
    public static boolean deleteGame(String playerName) {
        HashMap<String, GameState> allSaves = loadAllSaves();
        if (allSaves.containsKey(playerName)) {
            allSaves.remove(playerName);
            return writeAllSaves(allSaves);
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, GameState> loadAllSaves() {
        File file = new File(UNIFIED_SAVE_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (HashMap<String, GameState>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading saved games: " + e.getMessage());
            // If the file is corrupted or class changed, return empty to avoid crash loop
            return new HashMap<>();
        }
    }

    private static boolean writeAllSaves(HashMap<String, GameState> allSaves) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(UNIFIED_SAVE_FILE))) {
            oos.writeObject(allSaves);
            System.out.println("Game saved successfully to " + UNIFIED_SAVE_FILE);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving unified games file: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static void ensureDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
