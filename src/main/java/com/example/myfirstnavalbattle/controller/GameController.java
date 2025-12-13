package com.example.myfirstnavalbattle.controller;

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

    // Estado pendiente para cargar partida (setted by HomeController)
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

    // Cola de objetivos prioritarios para la IA (estructura de datos: Queue)
    private Queue<int[]> objetivosIA;

    // Estadísticas del jugador (estructura de datos: HashMap)
    private GameStatistics playerStats;

    // Stack para naves destruidas por el jugador (estructura de datos: Stack)
    private Stack<Ship> navesDestruidas;

    // Timer para ocultar la notificación de barco hundido
    private PauseTransition hideNotification;

    // Hilo del temporizador
    private com.example.myfirstnavalbattle.model.GameTimerThread gameTimer;

    /**
     * Clase interna para encapsular información del turno actual.
     * 
     * DEMOSTRACIÓN: Uso de CLASE INTERNA (Inner Class)
     * 
     * Una clase interna tiene acceso a los miembros de la clase externa
     * y se usa para agrupar lógica relacionada que solo tiene sentido
     * dentro del contexto de GameController.
     * 
     * @author 4rias01
     */
    private class TurnInfo {
        private final boolean isPlayerTurn;
        private final int turnNumber;
        private final long timestamp;
        private final String playerName;

        /**
         * Constructor de información del turno
         * 
         * @param isPlayerTurn true si es turno del jugador
         * @param turnNumber   número del turno actual
         */
        public TurnInfo(boolean isPlayerTurn, int turnNumber) {
            this.isPlayerTurn = isPlayerTurn;
            this.turnNumber = turnNumber;
            this.timestamp = System.currentTimeMillis();
            // Acceso a miembros de la clase externa (GameController)
            this.playerName = isPlayerTurn ? playerOne.getPlayerName() : "CPU";
        }

        /**
         * Genera un resumen del turno
         * 
         * @return String con información del turno
         */
        public String getSummary() {
            String playerType = isPlayerTurn ? "Jugador" : "IA";
            return String.format("[Turno %d] %s (%s) - Timestamp: %d",
                    turnNumber, playerType, playerName, timestamp);
        }

        /**
         * Verifica si es turno del jugador
         * 
         * @return true si es turno del jugador
         */
        public boolean isPlayerTurn() {
            return isPlayerTurn;
        }

        /**
         * Obtiene el número del turno
         * 
         * @return número del turno
         */
        public int getTurnNumber() {
            return turnNumber;
        }

        /**
         * Obtiene el timestamp del turno
         * 
         * @return timestamp en milisegundos
         */
        public long getTimestamp() {
            return timestamp;
        }

        /**
         * Obtiene el nombre del jugador actual
         * 
         * @return nombre del jugador
         */
        public String getPlayerName() {
            return playerName;
        }
    }

    // Contador de turnos para la clase interna
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
        objetivosIA = new LinkedList<>(); // Inicializar cola de objetivos
        playerStats = GameStatisticsController.getGameStatistics(); // Obtener instancia compartida de estadísticas
        playerStats = GameStatisticsController.getGameStatistics(); // Obtener instancia compartida de estadísticas

        // Cargar estadísticas si hay una partida pendiente
        if (pendingLoadState != null) {
            System.out.println("[GAME] Resumiendo partida - Restaurando estadísticas...");
            playerStats.setStats(
                    pendingLoadState.getShotsFired(),
                    pendingLoadState.getHits(),
                    pendingLoadState.getMisses(),
                    pendingLoadState.getShipsSunk());
            // Restaurar turno si es necesario
            turnCounter = pendingLoadState.getTurnCounter();

            // Limpiar estado pendiente para futuras partidas
            pendingLoadState = null;
        } else {
            playerStats.reset(); // Resetear para partida nueva
        }

        navesDestruidas = new Stack<>(); // Inicializar stack de naves destruidas

        initGridPane(gridPanePlayer, margins, size, 45);
        initGridPane(gridPaneIA, margins, size, 45);
        initShips();
        restoreVisuals();

        // Iniciar el temporizador
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
                // Si es HIT/KILLED, deshabilitar interaccion?
                if (cell.getStatus() != ModelCell.Status.EMPTY && cell.getStatus() != ModelCell.Status.SHIP) {
                    // Si ya fue disparado, deshabilitar si es tablero enemigo (IA)
                    // O player board?
                    // Logica original: stackPane.setDisable(true) al hacer click
                    // Debemos replicarlo
                    if (panes == stackPanesOfIA) {
                        // Solo deshabilitar si ya disparamos (MISS/HIT/KILLED)
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
                navesDestruidas.push(targetShip); // Agregar barco hundido al Stack
                showShipDestroyedNotification(targetShip); // Mostrar notificación
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

            TurnInfo turnInfo = new TurnInfo(false, ++turnCounter);
            System.out.println(turnInfo.getSummary());

            // FIX: Add delay before first AI shot of the turn
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(e -> randomShoot());
            pause.play();
        } else {
            playerOne.setHasPlayed(false);
            playerIA.setHasPlayed(true);

            // Uso de la clase interna TurnInfo
            TurnInfo turnInfo = new TurnInfo(true, ++turnCounter);
            System.out.println(turnInfo.getSummary());
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

        // USO DEL ADAPTADOR: Convertir estadísticas a diferentes formatos
        System.out.println("\n=== USO DE STATISTIC DISPLAY ADAPTER ===");
        StatisticsDisplayAdapter adapter = new StatisticsDisplayAdapter(playerStats);

        // Mostrar en formato tabla de consola
        System.out.println(adapter.toConsoleTable());

        // Mostrar en formato JSON (útil para APIs)
        System.out.println("Formato JSON:");
        System.out.println(adapter.toJSON());

        // Mostrar formato compacto
        System.out.println("\nFormato Compacto:");
        System.out.println(adapter.toCompactDisplay());

        System.out.println("\n=== FIN USO DEL ADAPTADOR ===\n");

        // Mostrar ventana de victoria o derrota
        try {
            // Actualizar estadísticas globales
            playerStats.incrementTotalGamesPlayed();

            if (playerIA.isHasLost()) {
                // El jugador humano ganó
                playerStats.incrementTotalGamesWon();
                System.out.println("[GAME] Jugador GANÓ - Cambiando a VictoryScene");
                SceneManager.switchTo("VictoryScene");
            } else if (playerOne.isHasLost()) {
                // El jugador humano perdió
                playerStats.incrementTotalGamesLost();
                System.out.println("[GAME] Jugador PERDIÓ - Cambiando a LostScene");
                SceneManager.switchTo("LostScene");
            }

            // Borrar la partida guardada para evitar "Ghost Games" (partidas ya terminadas
            // que se pueden cargar)
            GameSaver.deleteGame(playerStats.getCurrentProfileName());

            // Detener el timer
            if (gameTimer != null) {
                gameTimer.stopTimer();
            }

            // GUARDAR DATOS EN CSV
            saveProfileStats();

        } catch (IOException e) {
            System.err.println("Error al cargar la escena de fin de juego: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveProfileStats() {
        ProfileManager manager = new ProfileManager();
        String name = playerStats.getCurrentProfileName();

        // Usar los contadores globales oficiales de GameStatistics
        int played = playerStats.getTotalGamesPlayed();
        int won = playerStats.getTotalGamesWon();
        int lost = playerStats.getTotalGamesLost();

        System.out.println("[SAVE] Guardando perfil: " + name + " | Jugadas=" + played);

        manager.saveOrUpdateProfile(name, played, won, lost, generateShipStatusSummary());
    }

    private String generateShipStatusSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        // Iterar barcos del jugador
        for (Ship ship : playerShips) {
            String status = "Intacto";
            // Calcular salud. Ship no tiene 'health' directo visible aqui facil,
            // pero podemos verificar las celdas o el estado
            // O mejor, revisar si está 'KILLED' o si alguna parte está 'HIT'

            // Metodo alternativo: verificar modelo
            boolean isSunk = !isShipAlive(ship, playerOneBoard);
            if (isSunk) {
                status = "Hundido";
            } else if (isShipHit(ship, playerOneBoard)) {
                status = "Golpeado";
            }

            sb.append("Tam").append(ship.getSize()).append(":").append(status).append("|");
        }
        if (sb.length() > 1)
            sb.setLength(sb.length() - 1); // Remove last |
        sb.append("]");
        return sb.toString();
    }

    // Helper para verificar estado desde el board
    private boolean isShipAlive(Ship ship, Board board) {
        // Aprovechar logica existente en Board, o replicar
        // Board tiene isShipAlive pero es privado.
        // Accedemos a traves de ModelCell
        int[] coords = (int[]) ship.getUserData();
        return checkShipHealth(board, coords[0], coords[1], ship.getSize(), ship.isVertical()) > 0;
    }

    private boolean isShipHit(Ship ship, Board board) {
        int[] coords = (int[]) ship.getUserData();
        int health = checkShipHealth(board, coords[0], coords[1], ship.getSize(), ship.isVertical());
        return health < ship.getSize(); // Si salud < tamaño, fue golpeado
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
        // Crear DTOs
        BoardState pBoard = createBoardState(playerOneBoard);
        BoardState iaBoard = createBoardState(playerIABoard);

        GameState state = new GameState(
                playerStats.getCurrentProfileName(),
                pBoard,
                iaBoard,
                playerOne.isHasPlayed(), // Si playerOne 'isHasPlayed', significa que YA jugó su turno, le toca a la IA?
                // Revisa logica nextTurn: si isHasPlayed false -> setTrue -> IA turn.
                // Si guardamos, guardamos el estado actual.
                turnCounter);

        GameSaver.saveGame(state, playerStats.getCurrentProfileName());
    }

    private BoardState createBoardState(Board board) {
        // Necesitamos tamaño. Asumimos 10
        BoardState bs = new BoardState(10);

        // Copiar celdas
        ModelCell.Status[][] statuses = new ModelCell.Status[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                statuses[i][j] = board.getCell(i, j).getStatus();
            }
        }
        bs.setCellStatuses(statuses);

        // Copiar barcos
        for (Ship s : board.getShips()) {
            int[] coords = (int[]) s.getUserData();
            // Calcular salud actual para el estado
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
        saveGameState(); // Guardar el estado del juego (posición, etc)

        // FIX: También guardar las estadísticas para que no se pierdan al salir
        // Nota: Si sales a mitad de partida, no se cuenta como jugada/ganada/perdida,
        // pero DEBEMOS asegurar que la consistencia se mantenga si fuera necesario.
        // Sin embargo, el user se quejó de que "se reseteó". Si salió, no terminó.
        // Si no terminó, no debería incrementar contadores de partida jugada?
        // Pero si el sistema "resetea", quizas es porque no recargamos bien al volver?
        // Por ahora, solo guardamos si finalizamos. Si es BackButton, quizás no
        // deberíamos
        // contar la partida. Pero si el usuario dice "Se reseteó", quizás se refiere
        // a que sus stats ANTERIORES se borraron?
        // No, ProfileManager.saveOrUpdate SOBRESCRIBE.
        // Si llamamos a saveProfileStats aqui SIN incrementar jugadas,
        // se guardaría solo el ShipStatus actual?
        // Mejor NO guardar stats aqui si la partida no acabó, para evitar corrupcion.
        // El SaveGameState guarda el estado completo.

        // Pero el timer debe detenerse.
        if (gameTimer != null) {
            gameTimer.stopTimer();
        }

        SceneManager.switchTo("HomeScene");
    }
}
