package com.example.myfirstnavalbattle.controller;

/**
 * Controller for the Account Selection screen.
 * Handles profile creation, deletion, and selection.
 * 
 * @author David - Brandon
 */

import com.example.myfirstnavalbattle.model.GameStatistics;
import com.example.myfirstnavalbattle.persistence.ProfileManager;
import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AccountSelectionController {

    @FXML
    private ListView<String> profileListView;

    @FXML
    private TextField nameField;

    private ProfileManager profileManager;

    @FXML
    public void initialize() {
        profileManager = new ProfileManager();
        refreshProfileList();
    }

    private void refreshProfileList() {
        List<ProfileManager.Profile> profiles = profileManager.loadProfiles();
        List<String> names = profiles.stream()
                .map(ProfileManager.Profile::getName)
                .collect(Collectors.toList());
        profileListView.setItems(FXCollections.observableArrayList(names));
    }

    @FXML
    private void handleBack() throws IOException {
        SceneManager.switchTo("HomeScene");
    }

    @FXML
    private void handleAddProfile() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showAlert("Error", "El nombre no puede estar vacío");
            return;
        }

        // Check duplication
        if (profileListView.getItems().stream().anyMatch(n -> n.equalsIgnoreCase(name))) {
            showAlert("Error", "El perfil ya existe");
            return;
        }

        // Create new profile with 0 stats
        profileManager.saveOrUpdateProfile(name, 0, 0, 0, "Nuevo");
        nameField.clear();
        refreshProfileList();
    }

    @FXML
    private void handleDeleteProfile() {
        String selected = profileListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Selecciona un perfil para eliminar");
            return;
        }
        profileManager.deleteProfile(selected);
        refreshProfileList();
    }

    @FXML
    private void handleSelectProfile() throws IOException {
        String selected = profileListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Selecciona un perfil");
            return;
        }

        // Set current profile in GameStatistics singleton
        GameStatistics.getInstance().setCurrentProfileName(selected);

        // Sync stats from file to memory
        ProfileManager.Profile p = profileManager.getProfile(selected);
        if (p != null) {
            GameStatistics gs = GameStatistics.getInstance();
            gs.setTotalGamesPlayed(p.getPlayed());
            gs.setTotalGamesWon(p.getWon());
            gs.setTotalGamesLost(p.getLost());
            System.out.println("Estadísticas cargadas para " + selected);
        }

        System.out.println("Perfil seleccionado: " + selected);
        SceneManager.switchTo("HomeScene");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
