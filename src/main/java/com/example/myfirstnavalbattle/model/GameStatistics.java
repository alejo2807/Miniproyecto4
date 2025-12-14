package com.example.myfirstnavalbattle.model;

import java.util.HashMap;

/**
 * Class to track player statistics using HashMap.
 * This data structure allows storing and querying game metrics
 * efficiently using keys (String) and values (Integer).
 *
 * @author David - Brandon
 */
public class GameStatistics {
    // Unique Instance (Singleton) to share statistics between scenes
    private static GameStatistics instance;

    // Data Structure: HashMap to store player metrics
    private HashMap<String, Integer> stats;

    // Current loaded profile
    private String currentProfileName = "Guest";

    public void setCurrentProfileName(String name) {
        this.currentProfileName = name;
    }

    public String getCurrentProfileName() {
        return currentProfileName;
    }

    /**
     * Private constructor for Singleton pattern
     */
    private GameStatistics() {
        stats = new HashMap<>();
        initializeStats();
    }

    /**
     * Gets the unique instance of GameStatistics (Singleton)
     * 
     * @return The shared GameStatistics instance
     */
    public static GameStatistics getInstance() {
        if (instance == null) {
            instance = new GameStatistics();
        }
        return instance;
    }

    /**
     * Resets global instance (useful to start new game)
     */
    public static void resetInstance() {
        instance = new GameStatistics();
    }

    /**
     * Initializes all statistics to 0
     */
    private void initializeStats() {
        stats.put("disparosTotales", 0);
        stats.put("aciertos", 0);
        stats.put("fallos", 0);
        stats.put("barcosHundidos", 0);
    }

    /**
     * Increments a specific statistic by 1
     *
     * @param statName name of the statistic to increment
     */
    public void incrementStat(String statName) {
        incrementStat(statName, 1);
    }

    /**
     * Increments a specific statistic by the given amount
     *
     * @param statName name of the statistic to increment
     * @param amount   amount to increment
     */
    public void incrementStat(String statName, int amount) {
        if (stats.containsKey(statName)) {
            stats.put(statName, stats.get(statName) + amount);
        }
    }

    /**
     * Sets statistics manually (useful for loading saved game)
     * 
     * @param shots  total shots
     * @param hits   hits
     * @param misses misses
     * @param sunk   sunken ships
     */
    public void setStats(int shots, int hits, int misses, int sunk) {
        stats.put("disparosTotales", shots);
        stats.put("aciertos", hits);
        stats.put("fallos", misses);
        stats.put("barcosHundidos", sunk);
    }

    /**
     * Gets the value of a specific statistic
     *
     * @param statName name of the statistic
     * @return value of the statistic, or 0 if it doesn't exist
     */
    public int getStat(String statName) {
        return stats.getOrDefault(statName, 0);
    }

    /**
     * Gets all statistics as HashMap
     *
     * @return HashMap with all statistics
     */
    public HashMap<String, Integer> getAllStats() {
        return new HashMap<>(stats);
    }

    /**
     * Resets all statistics to 0
     */
    public void reset() {
        initializeStats();
    }

    /**
     * Calculates player precision as percentage
     *
     * @return precision in percentage (0-100)
     */
    public double getPrecision() {
        int total = getStat("disparosTotales");
        if (total == 0)
            return 0.0;
        return (getStat("aciertos") * 100.0) / total;
    }

    /**
     * Generates a summary of statistics in String format
     *
     * @return statistics summary
     */
    // Global Statistics (Persistent for the session)
    private static int totalGamesPlayed = 0;
    private static int totalGamesWon = 0;
    private static int totalGamesLost = 0;

    /**
     * Increments total games played counter
     */
    public void incrementTotalGamesPlayed() {
        totalGamesPlayed++;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public int getTotalGamesWon() {
        return totalGamesWon;
    }

    public int getTotalGamesLost() {
        return totalGamesLost;
    }

    /**
     * Increments total games won counter
     */
    public void incrementTotalGamesWon() {
        totalGamesWon++;
    }

    /**
     * Sets total games played counter
     * 
     * @param count amount
     */
    public void setTotalGamesPlayed(int count) {
        totalGamesPlayed = count;
    }

    /**
     * Sets total games won counter
     * 
     * @param count amount
     */
    public void setTotalGamesWon(int count) {
        totalGamesWon = count;
    }

    /**
     * Sets total games lost counter
     * 
     * @param count amount
     */
    public void setTotalGamesLost(int count) {
        totalGamesLost = count;
    }

    /**
     * Increments total games lost counter
     */
    public void incrementTotalGamesLost() {
        totalGamesLost++;
    }

    /**
     * Generates a summary of statistics in String format
     * Includes global statistics and current game stats
     *
     * @return statistics summary
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== GLOBAL STATISTICS ===\n");
        sb.append("Games Played: ").append(totalGamesPlayed).append("\n");
        sb.append("Games Won: ").append(totalGamesWon).append("\n");
        sb.append("Games Lost: ").append(totalGamesLost).append("\n\n");

        sb.append("=== LAST GAME ===\n");
        sb.append("Total Shots: ").append(getStat("disparosTotales")).append("\n");
        sb.append("Hits: ").append(getStat("aciertos")).append("\n");
        sb.append("Misses: ").append(getStat("fallos")).append("\n");
        sb.append("Ships Sunk: ").append(getStat("barcosHundidos")).append("\n");
        sb.append(String.format("Precision: %.2f%%", getPrecision()));
        return sb.toString();
    }
}
