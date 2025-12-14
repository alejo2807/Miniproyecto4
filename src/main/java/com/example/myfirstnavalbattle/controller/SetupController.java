package com.example.myfirstnavalbattle.controller;

/**
 * Controller for the Ship Setup screen.
 * Handles ship placement, rotation, and starting the game vs AI.
 * 
 * @author David - Brandon
 */

import com.example.myfirstnavalbattle.controller.setupStage.Cell;
import com.example.myfirstnavalbattle.controller.setupStage.Ship;
import com.example.myfirstnavalbattle.model.AIShipPlacementThread;
import com.example.myfirstnavalbattle.model.Characters;
import com.example.myfirstnavalbattle.model.Player;
import com.example.myfirstnavalbattle.model.SelectCharacter;
import com.example.myfirstnavalbattle.view.AnimationsManager;
import com.example.myfirstnavalbattle.view.SceneManager;
import com.example.myfirstnavalbattle.model.GameStatistics;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class SetupController {
    private Cell[][] cells = null;
    private ArrayList<Ship> ships = null;
    private Characters actualCharacter;
    private final int CELL_SIZE = 50;
    public final static int GRID_SIZE = 10;

    private Ship currentShip;
    private boolean currentShipIsVertical;
    private int currentShipRow;
    private int currentShipCol;
    private int currentShipSize;

    // Thread for background AI ship placement
    private AIShipPlacementThread aiPlacementThread;

    @FXML
    private GridPane gridpane;
    @FXML
    private HBox hBox;
    @FXML
    private Button readyButton;
    @FXML
    private ImageView characterImage;
    @FXML
    private TextField userNameTextField;
    @FXML
    private Rectangle rectangle;

    @FXML
    public void initialize() {
        ships = new ArrayList<>();
        cells = new Cell[GRID_SIZE][GRID_SIZE];

        initGridPane();
        initShips();
        initUserInfo();

        gridpane.setPrefSize(500, 500);
        gridpane.setMaxSize(500, 500);
        gridpane.setMinSize(500, 500);

        // Create and start AI placement thread
        aiPlacementThread = new AIShipPlacementThread();
        aiPlacementThread.start();
        System.out.println("[SETUP] AI thread started in background");
    }

    private void shipOnDragListener(Ship ship) {
        ship.setOnDragDetected(event -> {
            Dragboard db = ship.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("ship");

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            Image snapshot = getShipSnapshot(ship);
            content.putImage(snapshot);

            if (ship.isVertical()) {
                db.setDragView(snapshot, snapshot.getWidth() / 2, 25);
            } else {
                db.setDragView(snapshot, 25, snapshot.getHeight() / 2);
            }

            db.setContent(content);
            currentShip = ship;
            setCurrenShipAttributes();

            event.consume();
        });
    }

    private void cellOnDragOver(Cell cell) {
        cell.setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            if (currentShip != null) {
                if (canBePlaced(cell.getRow(), cell.getCol())) {
                    setCellState(cell.getRow(), cell.getCol(), Cell.Status.OVER, null);
                }
            }
            event.consume();
        });
        cell.setOnDragExited(event -> {
            if (currentShip != null && cell.getStatus() == Cell.Status.OVER) {
                setCellState(cell.getRow(), cell.getCol(), Cell.Status.EMPTY, null);
            }
        });
    }

    private void cellOnDragDropped(Cell cell) {
        cell.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (Objects.equals(db.getString(), "ship")) {
                dropShipInCell(cell);
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
            activateUserInfo();
        });
    }

    private void dropShipInCell(Cell cell) {

        int targetRow = cell.getRow();
        int targetCol = cell.getCol();

        boolean canBePlace = canBePlaced(targetRow, targetCol);

        if (canBePlace && currentShip.getParent() instanceof GridPane) {
            setCurrentShipLocation();
            setCellState(currentShipRow, currentShipCol, Cell.Status.EMPTY, null);

            currentShip.setUserData(new int[] { targetRow, targetCol });
            setCurrentShipLocation();

            setCellState(currentShipRow, currentShipCol, Cell.Status.SHIP, currentShip);
            placeShipInGridPane();
        } else if (canBePlace) {
            currentShip.setUserData(new int[] { targetRow, targetCol });
            setCurrentShipLocation();

            setCellState(currentShipRow, currentShipCol, Cell.Status.SHIP, currentShip);
            placeShipInGridPane();
        }

    }

    private boolean canBePlaced(int row, int col) {
        int init = currentShipIsVertical ? row : col;
        for (int target = init; target < init + currentShipSize; target++) {

            Cell cell;
            if (currentShipIsVertical) {
                cell = getCell(target, col);
            } else {
                cell = getCell(row, target);
            }

            if (cell == null) {
                return false;
            }
            if (cell.getStatus() == Cell.Status.SHIP) {
                return false;
            }
        }
        return true;
    }

    private void setCellState(int row, int col, Cell.Status status, Ship ship) {
        int init = currentShipIsVertical ? row : col; // variable that will iterate the for loop.
        // If vertical, iterate row and col remains fixed
        // if horizontal, row remains fixed and iterate col

        for (int target = init; target < init + currentShipSize; target++) {

            Cell cell;
            if (currentShipIsVertical) {
                cell = getCell(target, col); // iterate row
            } else {
                cell = getCell(row, target); // iterate col
            }
            assert cell != null;
            cell.setStatus(status);
            cell.setShip(ship);
        }
    }

    private void placeShipInGridPane() {
        if (currentShip.getParent() != null) {
            ((Pane) currentShip.getParent()).getChildren().remove(currentShip);
        }

        if (currentShipIsVertical) {
            gridpane.add(currentShip, currentShipCol, currentShipRow);
            GridPane.setRowSpan(currentShip, currentShipSize);
            GridPane.setColumnSpan(currentShip, 1);
        } else {
            gridpane.add(currentShip, currentShipCol, currentShipRow);
            GridPane.setRowSpan(currentShip, 1);
            GridPane.setColumnSpan(currentShip, currentShipSize);
        }
    }

    private void clickOnShipListener(Ship ship) {
        ship.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                if (!(ship.getParent() instanceof GridPane)) {
                    ship.rotateShip();
                }
            }
            if (event.getClickCount() == 2 && ship.getParent() instanceof GridPane) {
                currentShip = ship;
                setCurrenShipAttributes();
                setCurrentShipLocation();

                setCellState(currentShipRow, currentShipCol, Cell.Status.EMPTY, null);
                ((Pane) ship.getParent()).getChildren().remove(ship);

                if (!currentShipIsVertical) {
                    ship.rotateShip();
                }
                hBox.getChildren().add(ship);
                activateUserInfo();
            }
        });
    }

    private void initGridPane() {

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                Cell cell = new Cell(row, col);
                cell.setPrefSize(CELL_SIZE, CELL_SIZE);

                cells[row][col] = cell;

                cellOnDragOver(cell);
                cellOnDragDropped(cell);

                GridPane.setRowIndex(cell, row);
                GridPane.setColumnIndex(cell, col);
                gridpane.getChildren().add(cell);
            }
        }
    }

    private void initShips() {
        makeAndAddShip(4);
        makeAndAddShip(3);
        makeAndAddShip(3);
        makeAndAddShip(2);
        makeAndAddShip(2);
        makeAndAddShip(2);
        makeAndAddShip(1);
        makeAndAddShip(1);
        makeAndAddShip(1);
        makeAndAddShip(1);
    }

    private void initUserInfo() {
        actualCharacter = SelectCharacter.getSelectedCharacter();
        Image image = actualCharacter.getImage();

        characterImage.setImage(image);
        characterImage.setFitHeight(300);
        characterImage.setFitWidth(300);
        characterImage.setVisible(false);

        readyButton.setDisable(true);
        readyButton.setVisible(false);

        userNameTextField.setDisable(true);
        userNameTextField.setVisible(false);
        initTextFieldListener(userNameTextField);
    }

    private void initTextFieldListener(TextField textField) {
        textField.textProperty().addListener((obs, oldText, newText) -> {
            String sinNumeros = newText.replaceAll("[0-9]", "");
            String textoLimpio = sinNumeros.trim();

            if (textoLimpio.length() > 15) {
                textoLimpio = textoLimpio.substring(0, 15);
            }

            if (!textoLimpio.equals(newText)) {
                userNameTextField.setText(textoLimpio);
            }

            readyButton.setDisable(textoLimpio.length() <= 3);
        });
    }

    private void activateUserInfo() {
        if (hBox.getChildren().isEmpty()) {
            readyButton.setVisible(true);
            characterImage.setVisible(true);
            userNameTextField.setVisible(true);
            userNameTextField.setDisable(false);

            rectangle.setX(-25);
            rectangle.setY(-100);
            rectangle.setWidth(510);
            rectangle.setHeight(600);

            // Auto-detect User
            String currentProfile = GameStatistics.getInstance().getCurrentProfileName();
            if (!currentProfile.equals("Guest")) {
                userNameTextField.setText(currentProfile);
                userNameTextField.setDisable(true); // Lock it
                readyButton.setDisable(false); // Enable Ready immediately
            }
        } else {
            characterImage.setVisible(false);
            readyButton.setVisible(false);
            readyButton.setDisable(true);
            userNameTextField.setDisable(true);
            userNameTextField.setVisible(false);
            userNameTextField.setText("");

            rectangle.setVisible(true);
            rectangle.setX(0);
            rectangle.setY(0);
            rectangle.setWidth(510);
            rectangle.setHeight(270);
        }
    }

    private void makeAndAddShip(int size) {
        Ship ship = new Ship(size);
        AnimationsManager.applyCursorEvents(ship);

        shipOnDragListener(ship);
        clickOnShipListener(ship);
        hBox.getChildren().add(ship);
        ships.add(ship);
    }

    public Image getShipSnapshot(Ship ship) {
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

        return ship.snapshot(params, null);
    }

    private Cell getCell(int row, int col) {
        if (row >= GRID_SIZE || col >= GRID_SIZE) {
            return null;
        }
        return cells[row][col];
    }

    private void setCurrenShipAttributes() {
        currentShipSize = currentShip.getSize();
        currentShipIsVertical = currentShip.isVertical();
    }

    private void setCurrentShipLocation() {

        int[] coords = (int[]) currentShip.getUserData();
        currentShipRow = coords[0];
        currentShipCol = coords[1];

    }

    @FXML
    private void handleBackButton() throws IOException {
        SceneManager.switchTo("HomeScene");
    }

    @FXML
    private void handleReadyButton() throws IOException {
        try {
            System.out.println("[SETUP] Player pressed Ready. Synchronizing with AI thread...");

            // Signal AI thread to start (if not already)
            aiPlacementThread.signalStart();

            // Wait for AI thread completion
            aiPlacementThread.waitForCompletion();

            System.out.println("[SETUP] Synchronization complete. Starting game...");

            // Create players
            String playerName = userNameTextField.getText();
            Player playerOne = new Player(playerName, cells, ships, actualCharacter);

            // Use board generated by AI thread
            Player playerIA = new Player(aiPlacementThread.getAIBoard());

            GameController.setPlayerOne(playerOne);
            GameController.setPlayerIA(playerIA);

            System.out.println("[SETUP] ✓ Both players ready. Switching to GameScene");
            SceneManager.switchTo("GameScene");

        } catch (InterruptedException e) {
            System.err.println("[SETUP] Error: Thread interrupted during synchronization");
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
