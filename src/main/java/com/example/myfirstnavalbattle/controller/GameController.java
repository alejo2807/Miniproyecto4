package com.example.myfirstnavalbattle.controller;

import com.example.myfirstnavalbattle.controller.setupStage.Ship;
import com.example.myfirstnavalbattle.model.Board;
import com.example.myfirstnavalbattle.model.GameStatistics;
import com.example.myfirstnavalbattle.model.ModelCell;
import com.example.myfirstnavalbattle.model.Player;
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
<<<<<<< HEAD
=======
import javafx.animation.PauseTransition;
>>>>>>> origin/main
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

    private static Player playerOne;
    private static Player playerIA;
    private Board playerOneBoard;
    private Board playerIABoard;

    private ArrayList<Ship> playerShips = null;
    private ArrayList<Ship> iaShips = null;
    private StackPane[][] stackPanesOfPlayer = null;
    private StackPane[][] stackPanesOfIA = null;

    private ArrayList<StackPane> stackPanesPlayerAlive;
    private ArrayList<ImageView> iaShipsImageView = null;

    // Cola de objetivos prioritarios para la IA (estructura de datos: Queue)
    private Queue<int[]> objetivosIA;

    // Estadísticas del jugador (estructura de datos: HashMap)
    private GameStatistics playerStats;

    // Stack para naves destruidas por el jugador (estructura de datos: Stack)
    private Stack<Ship> navesDestruidas;

    // Timer para ocultar la notificación de barco hundido
    private PauseTransition hideNotification;

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
        objetivosIA = new LinkedList<>(); // Inicializar cola de objetivos
<<<<<<< HEAD
        playerStats = GameStatisticsController.getGameStatistics(); // Obtener instancia compartida de estadísticas
=======
        playerStats = GameStatistics.getInstance(); // Obtener instancia singleton
>>>>>>> origin/main
        navesDestruidas = new Stack<>(); // Inicializar stack de naves destruidas

        initGridPane(gridPanePlayer, margins, size, 45);
        initGridPane(gridPaneIA, margins, size, 45);
        initShips();
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

        // Registrar estadísticas del jugador humano (no de la IA)
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
                randomShoot();
            }
        } else if (status == ModelCell.Status.KILLED) {
            Ship targetShip = board.getShip(row, col);
            int[] targetCoords = (int[]) targetShip.getUserData();
            int shipRow = targetCoords[0];
            int shipCol = targetCoords[1];

            if (!playerIsIA) {
                setImageVisibility(shipRow, shipCol);
                navesDestruidas.push(targetShip); // Agregar barco hundido al Stack
<<<<<<< HEAD
                showShipDestroyedNotification(targetShip); // Mostrar notificación
=======
                showShipDestroyedNotification(targetShip); // Mostrar notificación temporal
>>>>>>> origin/main
            }

            setStackPaneState(player, shipRow, shipCol, targetShip.getSize(), targetShip.isVertical());
            if (player.isHasLost()) {
                finishGame();
                if (playerIsIA)
                    System.out.println("playerOne has lost");
                else
                    System.out.println("playerIA has lost");
            } else if (playerIsIA) {
                randomShoot();
            }
        }
    }

    /**
     * Muestra una notificación temporal cuando el jugador hunde un barco enemigo.
     * La notificación aparece durante 3 segundos y luego desaparece
     * automáticamente.
     * Si se hunde otro barco antes de que termine el timer, se cancela y muestra el
     * nuevo.
     * 
     * @param ship El barco que fue hundido
     */
    private void showShipDestroyedNotification(Ship ship) {
        // Cancelar el timer anterior si existe
        if (hideNotification != null) {
            hideNotification.stop();
        }

        // Actualizar el texto del label con el barco hundido
        int shipSize = ship.getSize();
        labelShipDestroyed.setText("¡Has hundido un Barco tamaño " + shipSize + "!");

        // Hacer visible el label
        labelShipDestroyed.setVisible(true);

        // Crear nuevo timer de 3 segundos para ocultar la notificación
        hideNotification = new PauseTransition(Duration.seconds(3));
        hideNotification.setOnFinished(event -> {
            labelShipDestroyed.setVisible(false);
        });

        // Iniciar el timer
        hideNotification.play();
    }

    private void randomShoot() {
        StackPane stackPane = null;

        // Primero intentar usar objetivos de la cola (IA inteligente)
        while (!objetivosIA.isEmpty() && stackPane == null) {
            int[] objetivo = objetivosIA.poll();
            int targetRow = objetivo[0];
            int targetCol = objetivo[1];

            if (esObjetivoValido(targetRow, targetCol)) {
                stackPane = stackPanesOfPlayer[targetRow][targetCol];
            }
        }

        // Si no hay objetivos válidos en la cola, disparo aleatorio
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
     * Muestra una notificación temporal cuando el jugador hunde un barco enemigo.
     * La notificación aparece durante 3 segundos y luego desaparece
     * automáticamente.
     * Si se hunde otro barco antes de que termine el timer, se cancela y muestra el
     * nuevo.
     * 
     * @param ship El barco que fue hundido
     */
    private void showShipDestroyedNotification(Ship ship) {
        // Cancelar el timer anterior si existe
        if (hideNotification != null) {
            hideNotification.stop();
        }

        // Actualizar el texto del label con el barco hundido
        int shipSize = ship.getSize();
        labelShipDestroyed.setText("¡Has hundido un Barco tamaño " + shipSize + "!");

        // Hacer visible el label
        labelShipDestroyed.setVisible(true);

        // Crear nuevo timer de 3 segundos para ocultar la notificación
        hideNotification = new PauseTransition(Duration.seconds(3));
        hideNotification.setOnFinished(event -> {
            labelShipDestroyed.setVisible(false);
        });

        // Iniciar el timer
        hideNotification.play();
    }

    /**
     * Agrega las celdas adyacentes (arriba, abajo, izquierda, derecha) a la cola de
     * objetivos.
     * Esto hace que la IA sea más inteligente al "cazar" naves después de un
     * acierto.
     */
    private void agregarObjetivosAdyacentes(int row, int col) {
        int[][] direcciones = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }; // arriba, abajo, izq, der
        for (int[] dir : direcciones) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];
            if (esObjetivoValido(newRow, newCol)) {
                objetivosIA.offer(new int[] { newRow, newCol });
            }
        }
    }

    /**
     * Verifica si una celda es un objetivo válido para la IA.
     * Debe estar dentro del tablero y no haber sido disparada aún.
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
            randomShoot();
        } else {
            playerOne.setHasPlayed(false);
            playerIA.setHasPlayed(true);
        }
    }

    private void setStackPaneState(Player player, int row, int col, int size, boolean vertical) {
        int init = vertical ? row : col;

        for (int target = init; target < init + size; target++) {

            StackPane stackPane;
            if (vertical) {
                stackPane = getStackPane(player, target, col); // iteras el row
            } else {
                stackPane = getStackPane(player, row, target); // iteras el col
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

        // Mostrar naves destruidas y estadísticas
        mostrarNavesDestruidas();
        System.out.println("\n=== ESTADÍSTICAS DEL JUGADOR ===");
        System.out.println(playerStats.getSummary());
        System.out.println("================================\n");

        // Mostrar ventana de victoria o derrota
        try {
            if (playerIA.isHasLost()) {
                // El jugador humano ganó
<<<<<<< HEAD
                System.out.println("[GAME] Jugador GANÓ - Cambiando a VictoryScene");
                SceneManager.switchTo("VictoryScene");
            } else if (playerOne.isHasLost()) {
                // El jugador humano perdió
                System.out.println("[GAME] Jugador PERDIÓ - Cambiando a LostScene");
=======
                SceneManager.switchTo("VictoryScene");
            } else if (playerOne.isHasLost()) {
                // El jugador humano perdió
>>>>>>> origin/main
                SceneManager.switchTo("LostScene");
            }
        } catch (IOException e) {
            System.err.println("Error al cargar la escena de fin de juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra la lista de barcos hundidos por el jugador en orden LIFO.
     * Demuestra el uso de la estructura de datos Stack.
     */
    private void mostrarNavesDestruidas() {
        System.out.println("\n=== Naves Destruidas (Orden de Hundimiento - Mas reciente al mas antiguo) ===");

        if (navesDestruidas.isEmpty()) {
            System.out.println("No hundiste ninguna nave.");
        } else {
            Stack<Ship> temp = new Stack<>();
            int position = 1;

            // Mostrar del más reciente al más antiguo (LIFO)
            while (!navesDestruidas.isEmpty()) {
                Ship ship = navesDestruidas.pop();
                temp.push(ship);
                System.out.printf("%d. Nave de tamaño %d%n", position++, ship.getSize());
            }

            // Restaurar el stack para mantener el estado
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
        SceneManager.switchTo("HomeScene");
    }
}
