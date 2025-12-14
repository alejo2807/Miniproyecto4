package com.example.myfirstnavalbattle.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the persistence of player profiles using a CSV file.
 * File format: Name;GamesPlayed;GamesWon;GamesLost;ShipStatus
 * 
 * @author David - Brandon
 */
public class ProfileManager {

    private static final String DATA_FOLDER = "data";
    private static final String PROFILES_FILE = DATA_FOLDER + File.separator + "all_players.csv";

    /**
     * Inner class to represent a profile row
     */
    public static class Profile {
        private String name;
        private int played;
        private int won;
        private int lost;
        private String shipStatus; // Readable summary

        public Profile(String name, int played, int won, int lost, String shipStatus) {
            this.name = name;
            this.played = played;
            this.won = won;
            this.lost = lost;
            this.shipStatus = shipStatus;
        }

        public String getName() {
            return name;
        }

        public int getPlayed() {
            return played;
        }

        public int getWon() {
            return won;
        }

        public int getLost() {
            return lost;
        }

        public String getShipStatus() {
            return shipStatus;
        }

        public void updateStats(int played, int won, int lost, String shipStatus) {
            this.played = played;
            this.won = won;
            this.lost = lost;
            this.shipStatus = shipStatus;
        }

        @Override
        public String toString() {
            return name + ";" + played + ";" + won + ";" + lost + ";" + shipStatus;
        }
    }

    public ProfileManager() {
        createDataFolder();
    }

    private void createDataFolder() {
        File folder = new File(DATA_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public List<Profile> loadProfiles() {
        List<Profile> profiles = new ArrayList<>();
        File file = new File(PROFILES_FILE);

        if (!file.exists()) {
            return profiles;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    String name = parts[0];
                    int played = Integer.parseInt(parts[1]);
                    int won = Integer.parseInt(parts[2]);
                    int lost = Integer.parseInt(parts[3]);
                    String status = (parts.length > 4) ? parts[4] : "N/A";
                    profiles.add(new Profile(name, played, won, lost, status));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading profiles: " + e.getMessage());
        }
        return profiles;
    }

    public void saveOrUpdateProfile(String name, int played, int won, int lost, String shipStatus) {
        List<Profile> profiles = loadProfiles();
        boolean found = false;

        for (Profile p : profiles) {
            if (p.getName().equalsIgnoreCase(name)) {
                p.updateStats(played, won, lost, shipStatus);
                found = true;
                break;
            }
        }

        if (!found) {
            profiles.add(new Profile(name, played, won, lost, shipStatus));
        }

        writeProfiles(profiles);
    }

    public void deleteProfile(String name) {
        List<Profile> profiles = loadProfiles();
        profiles.removeIf(p -> p.getName().equalsIgnoreCase(name));
        writeProfiles(profiles);
    }

    private void writeProfiles(List<Profile> profiles) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PROFILES_FILE))) {
            for (Profile p : profiles) {
                bw.write(p.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving profiles: " + e.getMessage());
        }
    }

    /**
     * Gets or creates a profile
     */
    public Profile getProfile(String name) {
        List<Profile> profiles = loadProfiles();
        for (Profile p : profiles) {
            if (p.getName().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return new Profile(name, 0, 0, 0, "No Data");
    }
}
