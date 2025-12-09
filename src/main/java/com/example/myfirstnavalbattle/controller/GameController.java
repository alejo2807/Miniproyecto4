package com.example.myfirstnavalbattle.controller;


import com.example.myfirstnavalbattle.controller.setupStage.Ship;
import com.example.myfirstnavalbattle.model.Board;
import com.example.myfirstnavalbattle.model.ModelCell;
import com.example.myfirstnavalbattle.model.Player;
import com.example.myfirstnavalbattle.view.SceneManager;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GameController {
    @FXML AnchorPane anchorPane;
    @FXML private GridPane gridPanePlayer;
    @FXML private GridPane gridPaneIA;
    @FXML Circle playerOneCharacter;
    @FXML Circle playerTwoCharacter;
    @FXML Label labelName;

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
                stackPane.setPrefSize(stackSize,stackSize);
                stackPane.getStyleClass().add("StackPane");

                GridPane.setRowIndex(stackPane, row);
                GridPane.setColumnIndex(stackPane, col);
                gridPane.getChildren().add(stackPane);

                stackPane.setUserData(new int[]{row, col});

                if (gridPane == gridPaneIA) {
                    stackPanesOfIA[row][col] = stackPane;
                    stackPaneListener(stackPane);
                }
                else{
                    stackPanesOfPlayer[row][col] = stackPane;
                    stackPanesPlayerAlive.add(stackPane);
                }
            }
        }
    }


    private void initShips(){
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
        int height = (45*size)-10;

        gridPane.add(shipImage, col, row);
        if (vertical) {
            shipImage.setFitHeight(height);
            shipImage.setFitWidth(width);
            GridPane.setColumnSpan(shipImage, 1);
            GridPane.setRowSpan(shipImage, size);
        }
        else{
            shipImage.setFitHeight(width);
            shipImage.setFitWidth(height);
            GridPane.setColumnSpan(shipImage, size);
            GridPane.setRowSpan(shipImage, 1);
        }
        shipImage.setUserData(coords);
    }


    private void stackPaneListener(StackPane stackPane) {
        stackPane.setOnMouseClicked(mouseEvent -> {
            if(!playerOne.isHasPlayed()){
                stackPane.setDisable(true);
                int[] coords = (int[]) stackPane.getUserData();
                shootInGame(playerIA, playerIABoard, coords[0], coords[1], stackPane);
            }
        });
    }

    private void shootInGame(Player player, Board board, int row, int col, StackPane stackPane) {
        ModelCell.Status status = player.shoot(row, col);
        boolean playerIsIA = (player == playerOne);

        if (status == ModelCell.Status.MISS) {
            stackPane.getStyleClass().add("water");
            nextTurn();
        }
        else if (status == ModelCell.Status.HIT) {
            stackPane.getStyleClass().add("hit");
            if (playerIsIA) {
                randomShoot();
            }
        }
        else if (status == ModelCell.Status.KILLED) {
            Ship targetShip = board.getShip(row, col);
            int[] targetCoords = (int[]) targetShip.getUserData();
            int shipRow = targetCoords[0];
            int shipCol = targetCoords[1];

            if (!playerIsIA) {
                setImageVisibility(shipRow, shipCol);
            }

            setStackPaneState(player, shipRow, shipCol, targetShip.getSize(), targetShip.isVertical());
            if(player.isHasLost()){
                finishGame();
                if(playerIsIA)
                    System.out.println("playerOne has lost");
                else
                    System.out.println("playerIA has lost");
            } else if (playerIsIA) {
                randomShoot();
            }
        }
    }

    private void randomShoot(){
        Collections.shuffle(stackPanesPlayerAlive);
        StackPane stackPane = stackPanesPlayerAlive.get(0);

        if(stackPane != null && !playerOne.isHasLost()){
            stackPanesPlayerAlive.remove(0);
            stackPane.setStyle("-fx-border-color: red;");
            int[] coords = (int[]) stackPane.getUserData();
            int row = coords[0];
            int col = coords[1];

            shootInGame(playerOne, playerOneBoard, row, col, stackPane);
        }
    }

    private void nextTurn(){
        if (!playerOne.isHasPlayed()) {
            playerOne.setHasPlayed(true);
            playerIA.setHasPlayed(false);
            randomShoot();
        }
        else {
            playerOne.setHasPlayed(false);
            playerIA.setHasPlayed(true);
        }
    }

    private void setStackPaneState(Player player, int row, int col, int size, boolean vertical) {
        int init = vertical? row : col;

        for (int target = init; target < init + size; target++) {

            StackPane stackPane;
            if (vertical) {
                stackPane = getStackPane(player, target, col); //iteras el row
            }
            else{
                stackPane = getStackPane(player, row, target); //iteras el col
            }
            assert stackPane != null;
            stackPane.getStyleClass().add("killed");
        }
    }

    private StackPane getStackPane(Player player, int row, int col) {
        if (player == playerIA) {
            return stackPanesOfIA[row][col];
        }
        else {
            return stackPanesOfPlayer[row][col];
        }
    }

    private void setIAView(boolean show){
        for (ImageView imageView : iaShipsImageView) {
            imageView.setVisible(show);
        }
    }

    private void setImageVisibility(int row, int col) {
        int[] coords = new int[]{row, col};
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

    private void finishGame(){
        for (StackPane[] stackPaneArray : stackPanesOfIA) {
            for (StackPane stackPane : stackPaneArray) {
                stackPane.setDisable(true);
            }
        }
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
