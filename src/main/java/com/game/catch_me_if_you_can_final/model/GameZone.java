package com.game.catch_me_if_you_can_final.model;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class GameZone {

    public static final Map<String,Double> gameBounds = new HashMap<>();

    private ObservableList<Player> players;
    private ScoreBoard scoreBoard;
    private AnchorPane gameRoot;

    public static final double ICON_WIDTH = 35;
    public static final double ICON_HEIGHT = 35;
    public static double PADDING_SIZE = ICON_WIDTH-5;

    private double topBound;
    private double rightBound;
    private double bottomBound;
    private double leftBound;
    private ObservableList<Map<String,Double>> deployablePlayerPositions;
    public static AnimationTimer gameAnimationTimer;
    private ObservableList<Player> deadPlayers;

    public GameZone() {
        deployablePlayerPositions = FXCollections.observableArrayList();
        deadPlayers = FXCollections.observableArrayList();
        scoreBoard = new ScoreBoard();
    }

    public void setGameRoot(AnchorPane gameRoot) {
        this.gameRoot = gameRoot;
    }

    public void setPlayers(ObservableList<Player> players) {
        this.players = players;
    }

    public void setBounds(double top,double right,double bottom,double left) {
        this.topBound = top-(ICON_HEIGHT/4);
        this.rightBound = right;
        this.bottomBound = bottom-(ICON_HEIGHT/3);
        this.leftBound = left;
    }

    private void deployPlayerPositions(ObservableList<Player> players) {
        Map<String,Double> topLeft = new HashMap<>();
        topLeft.put("x",leftBound);
        topLeft.put("y",topBound);
        Map<String,Double> topRight = new HashMap<>();
        topRight.put("x",rightBound);
        topRight.put("y",topBound);
        Map<String,Double> bottomLeft = new HashMap<>();
        bottomLeft.put("x",leftBound);
        bottomLeft.put("y",bottomBound);
        Map<String,Double> bottomRight = new HashMap<>();
        bottomRight.put("x",rightBound);
        bottomRight.put("y",bottomBound);
        deployablePlayerPositions.addAll(topLeft,topRight,bottomLeft,bottomRight);
        ObservableList<Map<String,Double>> tempDeployablePlayerPositions = FXCollections.observableArrayList(
                deployablePlayerPositions
        );
        for ( Player player : players ) {
            // Changes here...
            StackPane playerIcon = player.getIcon();
            playerIcon.setPrefWidth(ICON_WIDTH);
            playerIcon.setPrefHeight(ICON_HEIGHT+(ICON_HEIGHT/3));
            ImageView icon = (ImageView) playerIcon.getChildren().get(0);
            icon.setFitWidth(ICON_WIDTH);
            icon.setFitHeight(ICON_HEIGHT);
            VBox energyBarWrapper = (VBox) playerIcon.getChildren().get(1);
            energyBarWrapper.setPrefWidth(ICON_WIDTH);
            energyBarWrapper.setPrefHeight(ICON_HEIGHT);

            int randomPosId = NumberGenerator.generate(0,tempDeployablePlayerPositions.size());
            playerIcon.setLayoutX(tempDeployablePlayerPositions.get(randomPosId).get("x"));
            playerIcon.setLayoutY(tempDeployablePlayerPositions.get(randomPosId).get("y"));
            tempDeployablePlayerPositions.remove(randomPosId);
            gameRoot.getChildren().add(playerIcon);
        }
    }

    public void startGame() {
        Platform.runLater(() -> {
            gameRoot.setVisible(true);
            setBounds(0,gameRoot.getWidth()-ICON_WIDTH,gameRoot.getHeight()-ICON_HEIGHT,0);
            gameBounds.put("up",topBound); gameBounds.put("down",bottomBound); gameBounds.put("left",leftBound); gameBounds.put("right",rightBound);
            deployPlayerPositions(players);
            EventHandler<KeyEvent> keyPressEvent = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    KeyCode currentKey = event.getCode();
                    for ( Player player : players ) {
                        player.setPressedKey(currentKey,true);
                        // If player use or pressed power key...
                        if ( currentKey == player.getPowerKey() ) {
                            usePower(player);
                        }
                    }
                }
            };
            EventHandler<KeyEvent> keyReleaseEvent = new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    KeyCode currentKey = event.getCode();
                    for ( Player player : players ) {
                        player.setPressedKey(currentKey,false);
                    }
                }
            };
            gameRoot.getScene().addEventFilter(KeyEvent.KEY_PRESSED, keyPressEvent );
            gameRoot.getScene().addEventFilter(KeyEvent.KEY_RELEASED, keyReleaseEvent);
            gameAnimationTimer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    movePlayer();
                    checkDeadPlayerAndShowResult();
                }
            };
            gameAnimationTimer.start();
        });
    }
    private void movePlayer() {
        players.forEach( player -> {
            StackPane playerIcon = player.getIcon();
            if ( player.isKeyPressed("up") ) {
                moveWithinBound(player,playerIcon.getLayoutX(),playerIcon.getLayoutY()-player.getSpeed());
            }
            if ( player.isKeyPressed("down") ) {
                moveWithinBound(player,playerIcon.getLayoutX(),playerIcon.getLayoutY()+player.getSpeed());
            }
            if ( player.isKeyPressed("left") ) {
                moveWithinBound(player,playerIcon.getLayoutX()-player.getSpeed(),playerIcon.getLayoutY());
            }
            if ( player.isKeyPressed("right") ) {
                moveWithinBound(player,playerIcon.getLayoutX()+player.getSpeed(),playerIcon.getLayoutY());
            }
        });
    }
    private void moveWithinBound(Player player, double x, double y) {
        if ( x >= leftBound && x <= rightBound ) {
            player.getIcon().setLayoutX(x);
        }
        if ( y >= topBound && y <= bottomBound ) {
            player.getIcon().setLayoutY(y);
        }
        if ( player.hasAbilityToExceedBounds() ) {
            player.getIcon().setLayoutX(x);
            player.getIcon().setLayoutY(y);
        }
        // If hunter successfully catch the prey...
        if ( isHunterGotPrey(player) ) {
//                System.out.println("Hunter got the prey...");
        }
    }
    private boolean isHunterGotPrey(Player currentPlayer) {
        boolean gotPrey = false;
        for ( Player player : players ) {
            double hunterXPos = currentPlayer.getIcon().getLayoutX();
            double hunterYPos = currentPlayer.getIcon().getLayoutY();
            double preyXPos = player.getIcon().getLayoutX();
            double preyYPos = player.getIcon().getLayoutY();
            if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                    ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                if ( currentPlayer.isHunter() && !player.isHunter() && !player.isTemporaryHunter() ) {
                    gameRoot.getChildren().removeIf( i -> i == player.getIcon() );
                    player.getIcon().setLayoutX(-100);
                    player.getIcon().setLayoutY(-100);
//                    new AudioPlayer().play("die",1,0.5);
                    player.setToNoPower();
                    players.forEach(p -> p.removePlayer(player));

                } else if ( !currentPlayer.isTemporaryHunter() && !currentPlayer.isHunter() && player.isHunter() ) {
                    gameRoot.getChildren().removeIf( i -> i == currentPlayer.getIcon() );
                    currentPlayer.setToNoPower();
                    currentPlayer.getIcon().setLayoutX(-100);
                    currentPlayer.getIcon().setLayoutY(-100);
//                    new AudioPlayer().play("die",1,0.5);
                    players.forEach(p -> p.removePlayer(player));
                }
                gotPrey = true;
            }
        }
        return gotPrey;
    }
    private void checkDeadPlayerAndShowResult() {
        players.forEach(p -> {
            if ( !gameRoot.getChildren().contains(p.getIcon()) ) {
                if ( !deadPlayers.contains(p) ) {
                    deadPlayers.add(p);
                    new AudioPlayer().play("die",1,0.5);
                }
            }
        });
    }
    private void usePower(Player player) {
        player.getPower().use();
    }
}
