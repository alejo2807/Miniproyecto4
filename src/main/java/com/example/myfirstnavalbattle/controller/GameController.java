package com.example.myfirstnavalbattle.controller;

/**
 * Controller for the main Game screen.
 * Manages the game logic, board interactions, AI behavior, and game state.
 * 
 * @author David - Brandon
 */

import com.example.myfirstnavalbattle.controller.setupStage.Ship;
import com.example.myfirstnavalbattle.model.Board;
import com.example.myfirstnavalbattle.model.GameStatistics;
import com.example.myfirstnavalbattle.model.StatisticsDisplayAdapter;
import com.example.myfirstnavalbattle.model.ModelCell;
import com.example.myfirstnavalbattle.model.Player;
import com.example.myfirstnavalbattle.model.dto.BoardState;
import com.example.myfirstnavalbattle.model.dto.GameState;
import com.example.myfirstnavalbattle.model.dto.ShipState;
import com.example.myfirstnavalbattle.persistence.GameSaver;
import com.example.myfirstnavalbattle.persistence.ProfileManager;
import com.example.myfirstnavalbattle.view.SceneManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class GameController {
    @FXML
    AnchorPane anchorPane;
    @FXML
    private GridPane gridPanePlayer;
    @FXML
    private GridPane gridPaneIA;
    @FXML
    Circle playerOneCharacter;
    @FXML
    Circle playerTwoCharacter;
    @FXML
    Label labelName;
    @FXML
    private Label labelShipDestroyed;
    @FXML
    private Label labelTimer;

    private static Player playerOne;
    private static Player playerIA;
    private Board playerOneBoard;
    private Board playerIABoard;

    // Pending state to load game (setted by HomeController)
    private static GameState pendingLoadState = null;

    public static void setPendingLoadState(GameState state) {
        pendingLoadState = state;
    }

    private ArrayList<Ship> playerShips = null;
    private ArrayList<Ship> iaShips = null;
    private StackPane[][] stackPanesOfPlayer = null;
    private StackPane[][] stackPanesOfIA = null;

    private ArrayList<StackPane> stackPanesPlayerAlive;
    private ArrayList<ImageView> iaShipsImageView = null;

    // Queue of priority targets for AI (Data Structure: Queue)
    private Queue<int[]> objetivosIA;

    // Player statistics (Data Structure: HashMap)
    private GameStatistics playerStats;

    // Stack for destroyed ships by the player (Data Structure: Stack)
    private Stack<Ship> navesDestruidas;

    // Timer to hide the ship sunk notification
    private PauseTransition hideNotification;

    // Timer thread
    private com.example.myfirstnavalbattle.model.GameTimerThread gameTimer;

    /**
     * Inner class to encapsulate current turn information.
     * 
     * DEMONSTRATION: Use of INNER CLASS
     * 
     * An inner class has access to the members of the outer class
     * and is used to group related logic that only makes sense
     * within the context of GameController.
     * 
     * @author David - Brandon
     */
    private class TurnInfo {
        private final boolean isPlayerTurn;
        private final int turnNumber;
        private final long timestamp;
        private final String playerName;

        /**
         * Turn info constructor.
         * 
         * @param isPlayerTurn true if it is the player's turn
         * @param turnNumber   current turn number
         */
        public TurnInfo(boolean isPlayerTurn, int turnNumber) {
            this.isPlayerTurn = isPlayerTurn;
            this.turnNumber = turnNumber;
            this.timestamp = System.currentTimeMillis();
            // Acceso a miembros de la clase externa (GameController)
            this.playerName = isPlayerTurn ? playerOne.getPlayerName() : "CPU";
        }

        /**
         * Generates a turn summary.
         * 
         * @return String with turn information
         */
        public String getSummary() {
            String playerType = isPlayerTurn ? "Jugador" : "IA";
            return String.format("[Turno %d] %s (%s) - Timestamp: %d",
                    turnNumber, playerType, playerName, timestamp);
        }

        /**
         * Checks if it is the player's turn.
         * 
         * @return true if it is the player's turn
         */
        public boolean isPlayerTurn() {
            return isPlayerTurn;
        }

        /**
         * Gets the turn number.
         * 
         * @return turn number
         */
        public int getTurnNumber() {
            return turnNumber;
        }

        /**
         * Gets the turn timestamp.
         * 
         * @return timestamp in milliseconds
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Gets the current player name.
         * 
         * @return player name
         */
        public String getPlayerName() {
            return playerName;
        }
    }

    // Turn counter for the inner class
    private int turnCounter = 0;

    public GameController() {
    }

    @FXML
    public void initialize() {
        addListenerToScene(anchorPane);
        int size = SetupController.GRID_SIZE;
        int margins = 450;

        playerOneBoard = playerOne.getBoard();
        playerIABoard = playerIA.getBoard();

        Image imageCharacterOne = playerOne.getCharacter().getImage();
        Image imageCharacterTwo = playerIA.getCharacter().getImage();
        playerOneCharacter.setFill(new ImagePattern(imageCharacterOne));
        playerTwoCharacter.setFill(new ImagePattern(imageCharacterTwo));
        labelName.setText(playerOne.getPlayerName());

        stackPanesOfIA = new StackPane[size][size];
        stackPanesOfPlayer = new StackPane[size][size];
        stackPanesPlayerAlive = new ArrayList<>();

        playerShips = playerOneBoard.getShips();
        iaShips = playerIABoard.getShips();
        iaShipsImageView = new ArrayList<>();
        // Initialize queue of targets
        objetivosIA = new LinkedList<>();
        playerStats = GameStatisticsController.getGameStatistics(); // Get shared statistics instance
        // playerStats = GameStatisticsController.getGameStatistics(); // Get shared
        // statistics instance (Duplicated line removed)

        // Load statistics if there is a pending game
        if (pendingLoadState != null) {
            System.out.println("[GAME] Resuming game - Restoring statistics...");
            playerStats.setStats(
                    pendingLoadState.getShotsFired(),
                    pendingLoadState.getHits(),
                    pendingLoadState.getMisses(),
                    pendingLoadState.getShipsSunk());
            // Restore turn if necessary
            turnCounter = pendingLoadState.getTurnCounter();

            // Clear pending state for future games
            pendingLoadState = null;
        } else {
            playerStats.reset(); // Reset for new game
        }

        navesDestruidas = new Stack<>(); // Initialize destroyed ships stack

        initGridPane(gridPanePlayer, margins, size, 45);
        initGridPane(gridPaneIA, margins, size, 45);
        initShips();
        restoreVisuals();

        // Start timer
        if (labelTimer != null) {
            gameTimer = new com.example.myfirstnavalbattle.model.GameTimerThread(labelTimer);
            gameTimer.start();
        }
    }

    private void restoreVisuals() {
        restoreGridVisuals(playerOneBoard, stackPanesOfPlayer);
        restoreGridVisuals(playerIABoard, stackPanesOfIA);

        // FIX: Restore visibility of destroyed enemy ships
        for (Ship ship : iaShips) {
            if (!isShipAlive(ship, playerIABoard)) {
                int[] coords = (int[]) ship.getUserData();
                setImageVisibility(coords[0], coords[1]);
            }
        }
    }

    private void restoreGridVisuals(Board board, StackPane[][] panes) {
        int size = SetupController.GRID_SIZE;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                ModelCell cell = board.getCell(i, j);
                // Evitar null pointer si panes no inicializados (aunque initialize lo hace
                // antes)
                if (panes[i][j] == null)
                    continue;

                // Aplicar estilos segun estado
                if (cell.getStatus() == ModelCell.Status.MISS) {
                    panes[i][j].getStyleClass().add("water");
                } else if (cell.getStatus() == ModelCell.Status.HIT) {
                    panes[i][j].getStyleClass().add("hit");
                } else if (cell.getStatus() == ModelCell.Status.KILLED) {
                    panes[i][j].getStyleClass().add("hit");
                    panes[i][j].getStyleClass().add("killed");
                }
                // If HIT/KILLED, disable interaction?
                if (cell.getStatus() != ModelCell.Status.EMPTY && cell.getStatus() != ModelCell.Status.SHIP) {
                    // If already shot, disable if it's enemy board (IA)
                    // Or player board?
                    // Original logic: stackPane.setDisable(true) on click
                    // We must replicate it
                    if (panes == stackPanesOfIA) {
                        // Only disable if we already shot (MISS/HIT/KILLED)
                        panes[i][j].setDisable(true);
                    }
                }
            }
        }
    }

    private void initGridPane(GridPane gridPane, int margins, int size, int stackSize) {
        gridPane.setPrefSize(margins, margins);
        gridPane.setMaxSize(margins, margins);
        gridPane.setMinSize(margins, margins);

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                StackPane stackPane = new StackPane();
                stackPane.setPrefSize(stackSize, stackSize);
                stackPane.getStyleClass().add("StackPane");

                GridPane.setRowIndex(stackPane, row);
                GridPane.setColumnIndex(stackPane, col);
                gridPane.getChildren().add(stackPane);

                stackPane.setUserData(new int[] { row, col });

                if (gridPane == gridPaneIA) {
                    stackPanesOfIA[row][col] = stackPane;
                    stackPaneListener(stackPane);
                } else {
                    stackPanesOfPlayer[row][col] = stackPane;
                    stackPanesPlayerAlive.add(stackPane);
                }
            }
        }
    }

    private void initShips() {
        for (int index = 0; index < iaShips.size(); index++) {
            Ship playerShip = playerShips.get(index);
            Ship iaShip = iaShips.get(index);

            ImageView playerShipImage = new ImageView(playerShip.getImage());
            ImageView iaShipImage = new ImageView(iaShip.getImage());
            iaShipsImageView.add(iaShipImage);

            putShipImage(playerShip, gridPanePlayer, playerShipImage);
            putShipImage(iaShip, gridPaneIA, iaShipImage);

        }
        setIAView(false);
    }

    private void putShipImage(Ship ship, GridPane gridPane, ImageView shipImage) {
        shipImage.setMouseTransparent(true);
        int[] coords = (int[]) ship.getUserData();
        int row = coords[0];
        int col = coords[1];

        boolean vertical = ship.isVertical();
        int size = ship.getSize();

        int width = 43;
        int height = (45 * size) - 10;

        gridPane.add(shipImage, col, row);
        if (vertical) {
            shipImage.setFitHeight(height);
            shipImage.setFitWidth(width);
            GridPane.setColumnSpan(shipImage, 1);
            GridPane.setRowSpan(shipImage, size);
        } else {
            shipImage.setFitHeight(width);
            shipImage.setFitWidth(height);
            GridPane.setColumnSpan(shipImage, size);
            GridPane.setRowSpan(shipImage, 1);
        }
        shipImage.setUserData(coords);
    }

    private void stackPaneListener(StackPane stackPane) {
        stackPane.setOnMouseClicked(mouseEvent -> {
            if (!playerOne.isHasPlayed()) {
                stackPane.setDisable(true);
                int[] coords = (int[]) stackPane.getUserData();
                shootInGame(playerIA, playerIABoard, coords[0], coords[1], stackPane);
            }
        });
    }

    private void shootInGame(Player player, Board board, int row, int col, StackPane stackPane) {
        ModelCell.Status status = player.shoot(row, col);
        boolean playerIsIA = (player == playerOne);

        // Register statistics for human player (not AI)
        if (!playerIsIA) {
            playerStats.incrementStat("disparosTotales");
            if (status == ModelCell.Status.MISS) {
                playerStats.incrementStat("fallos");
            } else {
                playerStats.incrementStat("aciertos");
                if (status == ModelCell.Status.KILLED) {
                    playerStats.incrementStat("barcosHundidos");
                }
            }
        }

        if (status == ModelCell.Status.MISS) {
            stackPane.getStyleClass().add("water");
            nextTurn();
        } else if (status == ModelCell.Status.HIT) {
            stackPane.getStyleClass().add("hit");
            if (playerIsIA) {
                agregarObjetivosAdyacentes(row, col); // Agregar celdas vecinas a la cola
                // FIX: Add delay before next shot
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> randomShoot());
                pause.play();
            }
        } else if (status == ModelCell.Status.KILLED) {
            Ship targetShip = board.getShip(row, col);
            int[] targetCoords = (int[]) targetShip.getUserData();
            int shipRow = targetCoords[0];
            int shipCol = targetCoords[1];

            if (!playerIsIA) {
                setImageVisibility(shipRow, shipCol);
                navesDestruidas.push(targetShip); // Add sunken ship to Stack
                showShipDestroyedNotification(targetShip); // Show notification
            }

            setStackPaneState(player, shipRow, shipCol, targetShip.getSize(), targetShip.isVertical());
            if (player.isHasLost()) {
                finishGame();
                if (playerIsIA)
                    System.out.println("playerOne has lost");
                else
                    System.out.println("playerIA has lost");
            } else if (playerIsIA) {
                // FIX: Add delay before next shot
                PauseTransition pause = new PauseTransition(Duration.seconds(2));
                pause.setOnFinished(e -> randomShoot());
                pause.play();
            }
        }
    }

    private void randomShoot() {
        StackPane stackPane = null;

        // First try to use targets from queue (Smart AI)
        while (!objetivosIA.isEmpty() && stackPane == null) {
            int[] objetivo = objetivosIA.poll();
            int targetRow = objetivo[0];
            int targetCol = objetivo[1];

            if (esObjetivoValido(targetRow, targetCol)) {
                stackPane = stackPanesOfPlayer[targetRow][targetCol];
            }
        }

        // If no valid targets in queue, random shot
        if (stackPane == null && !stackPanesPlayerAlive.isEmpty()) {
            Collections.shuffle(stackPanesPlayerAlive);
            stackPane = stackPanesPlayerAlive.get(0);
        }

        if (stackPane != null && !playerOne.isHasLost()) {
            stackPanesPlayerAlive.remove(stackPane);
            stackPane.setStyle("-fx-border-color: red;");
            int[] coords = (int[]) stackPane.getUserData();
            int row = coords[0];
            int col = coords[1];

            shootInGame(playerOne, playerOneBoard, row, col, stackPane);
        }
    }

    /**
     * Shows a temporary notification when the player sinks an enemy ship.
     * The notification appears for 3 seconds and then disappears automatically.
     * If another ship is sunk before the timer ends, it is cancelled and the new
     * one is shown.
     * 
     * @param ship The ship that was sunk
     */
    private void showShipDestroyedNotification(Ship ship) {
        // Cancel previous timer if exists
        if (hideNotification != null) {
            hideNotification.stop();
        }

        // Update label text with sunk ship
        int shipSize = ship.getSize();
        labelShipDestroyed.setText("¡Has hundido un Barco tamaño " + shipSize + "!");

        // Make label visible
        labelShipDestroyed.setVisible(true);

        // Create new 3-second timer to hide notification
        hideNotification = new PauseTransition(Duration.seconds(3));
        hideNotification.setOnFinished(event -> {
            labelShipDestroyed.setVisible(false);
        });

        // Start timer
        hideNotification.play();
    }

    /**
     * Adds adjacent cells (up, down, left, right) to the queue of targets.
     * This makes the AI smarter at "hunting" ships after a hit.
     */
    private void agregarObjetivosAdyacentes(int row, int col) {
        int[][] direcciones = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }; // up, down, left, right
        for (int[] dir : direcciones) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (esObjetivoValido(newRow, newCol)) {
                objetivosIA.offer(new int[] { newRow, newCol });
            }
        }
    }

    /**
     * Verifies if a cell is a valid target for the AI.
     * Must be within the board and not have been shot yet.
     */
    private boolean esObjetivoValido(int row, int col) {
        if (row < 0 || row >= SetupController.GRID_SIZE || col < 0 || col >= SetupController.GRID_SIZE) {
            return false;
        }
        StackPane stackPane = stackPanesOfPlayer[row][col];
        return stackPanesPlayerAlive.contains(stackPane);
    }

    private void nextTurn() {
        if (!playerOne.isHasPlayed()) {
            playerOne.setHasPlayed(true);
            playerIA.setHasPlayed(false);

            TurnInfo turnInfo = new TurnInfo(false, ++turnCounter);
            System.out.println(turnInfo.getSummary());

            // FIX: Add delay before first AI shot of the turn
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> randomShoot());
            pause.play();
        } else {
            playerOne.setHasPlayed(false);
            playerIA.setHasPlayed(true);

            // Use TurnInfo inner class
            TurnInfo turnInfo = new TurnInfo(true, ++turnCounter);
            System.out.println(turnInfo.getSummary());
        }
    }

    private void setStackPaneState(Player player, int row, int col, int size, boolean vertical) {
        int init = vertical ? row : col;

        for (int target = init; target < init + size; target++) {

            StackPane stackPane;
            if (vertical) {
                stackPane = getStackPane(player, target, col); // iterate row
            } else {
                stackPane = getStackPane(player, row, target); // iterate col
            }
            assert stackPane != null;
            stackPane.getStyleClass().add("killed");
        }
    }

    private StackPane getStackPane(Player player, int row, int col) {
        if (player == playerIA) {
            return stackPanesOfIA[row][col];
        } else {
            return stackPanesOfPlayer[row][col];
        }
    }

    private void setIAView(boolean show) {
        for (ImageView imageView : iaShipsImageView) {
            imageView.setVisible(show);
        }
    }

    private void setImageVisibility(int row, int col) {
        int[] coords = new int[] { row, col };
        for (ImageView imageView : iaShipsImageView) {

            int[] imageCoords = (int[]) imageView.getUserData();
            if (Arrays.equals(coords, imageCoords)) {
                imageView.setVisible(true);
                iaShipsImageView.remove(imageView);
                break;
            }
        }
    }

    private void addListenerToScene(AnchorPane pane) {
        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (event.isControlDown() && event.getCode() == KeyCode.S && !iaShipsImageView.isEmpty()) {
                        setIAView(!iaShipsImageView.get(0).isVisible());
                        event.consume();
                    }
                });
            }
        });
    }

    private void finishGame() {
        for (StackPane[] stackPaneArray : stackPanesOfIA) {
            for (StackPane stackPane : stackPaneArray) {
                stackPane.setDisable(true);
            }
        }

        // Show destroyed ships and statistics
        mostrarNavesDestruidas();

        // USE OF ADAPTER: Convert statistics to different formats
        System.out.println("\n=== USE OF STATISTICS DISPLAY ADAPTER ===");
        StatisticsDisplayAdapter adapter = new StatisticsDisplayAdapter(playerStats);

        // Show in console table format
        System.out.println(adapter.toConsoleTable());

        // Show in JSON format (useful for APIs)
        System.out.println("JSON Format:");
        System.out.println(adapter.toJSON());

        // Show compact format
        System.out.println("\nCompact Format:");
        System.out.println(adapter.toCompactDisplay());

        System.out.println("\n=== END OF ADAPTER USAGE ===\n");

        // Show victory or defeat window
        try {
            // Update global statistics
            playerStats.incrementTotalGamesPlayed();

            if (playerIA.isHasLost()) {
                // Human player won
                playerStats.incrementTotalGamesWon();
                System.out.println("[GAME] Player WON - Switching to VictoryScene");
                SceneManager.switchTo("VictoryScene");
            } else if (playerOne.isHasLost()) {
                // Human player lost
                playerStats.incrementTotalGamesLost();
                System.out.println("[GAME] Player LOST - Switching to LostScene");
                SceneManager.switchTo("LostScene");
            }

            // Delete saved game to avoid "Ghost Games" (already finished games that can be
            // loaded)
            GameSaver.deleteGame(playerStats.getCurrentProfileName());

            // Stop timer
            if (gameTimer != null) {
                gameTimer.stopTimer();
            }

            // GUARDAR DATOS EN CSV
            saveProfileStats();

        } catch (IOException e) {
            System.err.println("Error loading end game scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveProfileStats() {
        ProfileManager manager = new ProfileManager();
        String name = playerStats.getCurrentProfileName();

        // Use official global counters from GameStatistics
        int played = playerStats.getTotalGamesPlayed();
        int won = playerStats.getTotalGamesWon();
        int lost = playerStats.getTotalGamesLost();

        System.out.println("[SAVE] Saving profile: " + name + " | Played=" + played);

        manager.saveOrUpdateProfile(name, played, won, lost, generateShipStatusSummary());
    }

    private String generateShipStatusSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        // Iterate player ships
        for (Ship ship : playerShips) {
            String status = "Intact";
            // Calculate health. Ship doesn't have 'health' directly visible here easily,
            // but we can check cells or state
            // Or better, check if 'KILLED' or if any part is 'HIT'

            // Alternative method: verify model
            boolean isSunk = !isShipAlive(ship, playerOneBoard);
            if (isSunk) {
                status = "Sunk";
            } else if (isShipHit(ship, playerOneBoard)) {
                status = "Hit";
            }

            sb.append("Size").append(ship.getSize()).append(":").append(status).append("|");
        }
        if (sb.length() > 1)
            sb.setLength(sb.length() - 1); // Remove last |
        sb.append("]");
        return sb.toString();
    }

    // Helper to verify state from board
    private boolean isShipAlive(Ship ship, Board board) {
        // Reuse existing logic in Board, or replicate
        // Board has isShipAlive but it's private.
        // We access through ModelCell
        int[] coords = (int[]) ship.getUserData();
        return checkShipHealth(board, coords[0], coords[1], ship.getSize(), ship.isVertical()) > 0;
    }

    private boolean isShipHit(Ship ship, Board board) {
        int[] coords = (int[]) ship.getUserData();
        int health = checkShipHealth(board, coords[0], coords[1], ship.getSize(), ship.isVertical());
        return health < ship.getSize(); // If health < size, it was hit
    }

    private int checkShipHealth(Board board, int row, int col, int size, boolean vertical) {
        int health = 0;
        int init = vertical ? row : col;
        for (int i = 0; i < size; i++) {
            int r = vertical ? init + i : row;
            int c = vertical ? col : init + i;
            ModelCell cell = board.getCell(r, c);
            if (cell.getStatus() == ModelCell.Status.SHIP) {
                health++;
            }
        }
        return health;
    }

    private void saveGameState() {
        // Create DTOs
        BoardState pBoard = createBoardState(playerOneBoard);
        BoardState iaBoard = createBoardState(playerIABoard);

        GameState state = new GameState(
                playerStats.getCurrentProfileName(),
                pBoard,
                iaBoard,
                playerOne.isHasPlayed(), // If playerOne 'isHasPlayed', it means they ALREADY played their turn, it's
                                         // AI's turn?
                // Check nextTurn logic: if isHasPlayed false -> setTrue -> IA turn.
                // If we save, we save the current state.
                turnCounter);

        GameSaver.saveGame(state, playerStats.getCurrentProfileName());
    }

    private BoardState createBoardState(Board board) {
        // We need size. Assess 10
        BoardState bs = new BoardState(10);

        // Copy cells
        ModelCell.Status[][] statuses = new ModelCell.Status[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                statuses[i][j] = board.getCell(i, j).getStatus();
            }
        }
        bs.setCellStatuses(statuses);

        // Copy ships
        for (Ship s : board.getShips()) {
            int[] coords = (int[]) s.getUserData();
            // Calculate current health for state
            int health = checkShipHealth(board, coords[0], coords[1], s.getSize(), s.isVertical());

            bs.addShip(new ShipState(
                    coords[0], coords[1],
                    s.getSize(),
                    s.isVertical(),
                    health));
        }
        return bs;
    }

    /**
     * Shows the list of ships sunk by the player in LIFO order.
     * Demonstrates the use of the Stack data structure.
     */
    private void mostrarNavesDestruidas() {
        System.out.println("\n=== Destroyed Ships (Sunk Order - Recent to Oldest) ===");

        if (navesDestruidas.isEmpty()) {
            System.out.println("No ships sunk.");
        } else {
            Stack<Ship> temp = new Stack<>();
            int position = 1;

            // Show from most recent to oldest (LIFO)
            while (!navesDestruidas.isEmpty()) {
                Ship ship = navesDestruidas.pop();
                temp.push(ship);
                System.out.printf("%d. Ship size %d%n", position++, ship.getSize());
            }

            // Restore stack to maintain state
            while (!temp.isEmpty()) {
                navesDestruidas.push(temp.pop());
            }
        }

        System.out.println("=============================================\n");
    }

    public static void setPlayerOne(Player player) {
        playerOne = player;
    }

    public static void setPlayerIA(Player player) {
        playerIA = player;
    }

    @FXML
    private void handleBackButton() throws IOException {
        saveGameState(); // Save game state (position, etc)

        if (gameTimer != null) {
            gameTimer.stopTimer();
        }

        SceneManager.switchTo("HomeScene");
    }
}
