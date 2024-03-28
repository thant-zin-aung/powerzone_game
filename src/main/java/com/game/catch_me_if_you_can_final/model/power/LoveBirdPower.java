package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.*;
import com.game.catch_me_if_you_can_final.model.manipulator.ImageManipulator;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

public class LoveBirdPower implements Power {
    private final int energyFillTime = 5;
    private final int FLASHING_SPEED = Player.PLAYER_SPEED*4;
    private final int ALLY_TOTAL_LIVE = 3;
    // using milli sec ( Note - 100 == 1s )
    private final int RESCUE_TIME = 200;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private final ObservableList<Player> allies;
    private final Map<Player,Integer> allyTotalLives;
    private final ImageManipulator imageManipulator;
    private final Map<Player,Boolean> playerMovement;

    private Player hunter;
    private final AnimationStyle animationStyle;
    private final double PADDING_SIZE = GameZone.ICON_WIDTH;
    private boolean isEnergyFull;
    private final String POWER_EFFECT_NAME = "red-power-effect";
    private boolean isAllyAlive = true;
    private boolean isTempDead = false;
    private Player targetPlayer;
    private double targetXPos,currentXPos;
    private double targetYPos,currentYPos;
    private AnchorPane parent;
    private int tempRescueTime = 0;

    public LoveBirdPower(Player currentPlayer, ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        this.allies = FXCollections.observableArrayList();
        this.allyTotalLives = FXCollections.observableHashMap();
        this.animationStyle = new AnimationStyle();
        this.imageManipulator = new ImageManipulator();
        this.playerMovement = new HashMap<>();
        isEnergyFull = true;
        enableFame();
        useAbility();
        initializePowerRequirements();
        startAlliesDeadMonitor();
        startRescueMonitor();
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("loveBird1",1,1);
        new AudioPlayer().play("loveBird2",0.7,0.5);
        targetPlayer = allies.get(NumberGenerator.generate(0,allies.size()));
        new AnimationTimer() {
            @Override
            public void handle(long l) {
                targetXPos = Math.round(targetPlayer.getIcon().getLayoutX());
                targetYPos = Math.round(targetPlayer.getIcon().getLayoutY());
                currentXPos = Math.round(currentPlayer.getIcon().getLayoutX());
                currentYPos = Math.round(currentPlayer.getIcon().getLayoutY());

                // if love bird is reached to the ally position...
                if ( ((targetXPos-currentXPos >= 0 && targetXPos-currentXPos <= PADDING_SIZE) || (targetXPos-currentXPos >= -PADDING_SIZE && targetXPos-currentXPos <= 0)) &&
                        ((targetYPos-currentYPos >= 0 && targetYPos-currentYPos <= PADDING_SIZE) || (targetYPos-currentYPos >= -PADDING_SIZE && targetYPos-currentYPos <= 0)) ) {
                    this.stop();
                } else {
                    double xSpeed = 0;
                    double ySpeed = 0;
                    if ( targetXPos-currentXPos > -FLASHING_SPEED && targetXPos-currentXPos < FLASHING_SPEED ) {
                        xSpeed = 0;
                    } else if ( targetXPos > currentXPos ) {
                        xSpeed = FLASHING_SPEED;
                    } else {
                        xSpeed = -FLASHING_SPEED;
                    }
                    if ( targetYPos-currentYPos > -FLASHING_SPEED && targetYPos-currentYPos < FLASHING_SPEED ) {
                        ySpeed = 0;
                    } else if ( targetYPos > currentYPos ) {
                        ySpeed = FLASHING_SPEED;
                    } else {
                        ySpeed = -FLASHING_SPEED;
                    }
                    currentPlayer.getIcon().setLayoutX(currentPlayer.getIcon().getLayoutX()+xSpeed);
                    currentPlayer.getIcon().setLayoutY(currentPlayer.getIcon().getLayoutY()+ySpeed);
                }
            }
        }.start();

        startEnergyFillTimer();
    }

    private void initializePowerRequirements() {
        new Thread(() -> {
            new TimeManipulator().waitTimer(1);
            if ( currentPlayer.isHunter() ) return;
            parent = (AnchorPane) currentPlayer.getIcon().getParent();
            for ( Player player : allPlayers ) {
                if ( !player.isHunter() && player != currentPlayer ) {
                    allies.add(player);
                    allyTotalLives.put(player,ALLY_TOTAL_LIVE);
                    player.setTemporaryHunter(true);
                }
                if ( player.isHunter() ) {
                    hunter = player;
                }
            }

            for ( Player ally : allies ) {
                playerMovement.put(ally,true);
            }
        }).start();
    }

    private void startAlliesDeadMonitor() {
        new Thread(() -> {
            new TimeManipulator().waitTimer(1);
            if ( currentPlayer.isHunter() ) return;
            new AnimationTimer() {
                @Override
                public void handle(long l) {
                    if ( !isAllyAlive ) this.stop();
                    for ( Player player : allies ) {
                        double hunterXPos = hunter.getIcon().getLayoutX();
                        double hunterYPos = hunter.getIcon().getLayoutY();
                        double preyXPos = player.getIcon().getLayoutX();
                        double preyYPos = player.getIcon().getLayoutY();
                        if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                                ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0))) {
                            ImageView currentPlayerIcon = (ImageView) player.getIcon().getChildren().get(0);

                            // if current player is not in emergency...
                            if ( !imageManipulator.getExtractedIconPath(new ImageView(currentPlayerIcon.getImage())).equalsIgnoreCase(
                                    imageManipulator.getImagePath("help")
                            )) {
                                currentPlayerIcon.setImage(imageManipulator.getImage("help"));
                                new Thread(() -> {
                                    // total lives are gone...
                                    System.out.println(allyTotalLives.get(player));
                                    if ( allyTotalLives.get(player) <= 0 ) {
                                        player.setTemporaryHunter(false);
                                        Platform.runLater(() -> parent.getChildren().removeIf(c -> c==player.getIcon()));
                                    } else {
                                        new TimeManipulator().waitTimer(1);
                                        allyTotalLives.put(player,allyTotalLives.get(player)-1);
//                                    isTempDead=false;
                                    }
                                }).start();

                                makePlayerToNotMove(player);
                            }

                        }
                    }
                }
            }.start();
        }).start();
    }

    private void startRescueMonitor() {
        new Thread(() -> {
            new TimeManipulator().waitTimer(1);
            if ( currentPlayer.isHunter() ) return;
            new AnimationTimer() {
                @Override
                public void handle(long l) {
                    if ( !isAllyAlive ) this.stop();
                    int tempTotalAllies = 0;
                    for ( Player player : allies ) {
                        double hunterXPos = currentPlayer.getIcon().getLayoutX();
                        double hunterYPos = currentPlayer.getIcon().getLayoutY();
                        double preyXPos = player.getIcon().getLayoutX();
                        double preyYPos = player.getIcon().getLayoutY();
                        if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                                ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) && !isTempDead) {
                            ImageView currentPlayerIcon = (ImageView) player.getIcon().getChildren().get(0);
                            // if current player is in emergency...
                            if ( imageManipulator.getExtractedIconPath(new ImageView(currentPlayerIcon.getImage())).equalsIgnoreCase(
                                    imageManipulator.getImagePath("help")
                            )) {
//                                new Thread(() -> {
//                                    for ( int id = 1 ; id < RESCUE_TIME*1000 ; id++ ) {
//
//                                    }
//                                }).start();
                                System.out.println("lovebird is touching..." + tempRescueTime);
                                ++tempRescueTime;
                                if ( tempRescueTime > RESCUE_TIME ) {
                                    ((ImageView)player.getIcon().getChildren().get(0)).setImage(imageManipulator.getImageByPlayerPower(player));
                                    playerMovement.put(player,true);
                                }
                            }
                        } else {
                            ++tempTotalAllies;
                            if ( tempTotalAllies == allies.size() ) {
                                tempRescueTime = 0;
                            }
                        }
                    }
                }
            }.start();
        }).start();
    }

    private void makePlayerToNotMove(Player player) {
        playerMovement.put(player,false);
        new Thread(() -> {
            while ( !playerMovement.get(player) ) {
                player.setPressedKey(player.getUpKey(),false);
                player.setPressedKey(player.getDownKey(),false);
                player.setPressedKey(player.getLeftKey(),false);
                player.setPressedKey(player.getRightKey(),false);
                player.setPressedKey(player.getPowerKey(),false);
                new TimeManipulator().waitTimerInMilliSec(1);
            }
        }).start();
    }

    private void useAbility() {
        if ( currentPlayer.isHunter() ) return;
        new Thread(() -> {
            new TimeManipulator().waitTimer(1);
            // set bird to immune while 2 allies are still alive...
            while ( allPlayers.size() > 2  ) {
                currentPlayer.setTemporaryHunter(true);
                new TimeManipulator().waitTimerInMilliSec(15);
            }
            currentPlayer.setTemporaryHunter(false);
            isAllyAlive = false;
        }).start();
    }

    private void enableFame() {
        new Thread(() -> {
            new TimeManipulator().waitTimerInMilliSec(200);
            AnchorPane fameNode = (AnchorPane) currentPlayer.getIcon().getChildren().get(2);
            ImageView playerIcon = (ImageView) currentPlayer.getIcon().getChildren().get(0);
            ProgressBar energyBar = currentPlayer.getEnergyBar();
            fameNode.setPrefWidth(GameZone.ICON_WIDTH);
            fameNode.setPrefHeight(GameZone.ICON_HEIGHT);
            if ( !playerIcon.getStyleClass().contains(POWER_EFFECT_NAME) ) {
                playerIcon.getStyleClass().add(POWER_EFFECT_NAME);
            }
            energyBar.setStyle("-fx-accent: red");
            animationStyle.sprinkleFlyingEffect(fameNode,7000,1000,2000,new int[]{1,2}, Color.RED);
        }).start();
    }



    private void startEnergyFillTimer() {
        isEnergyFull=false;
        currentPlayer.getEnergyBar().getStyleClass().add("energy-fill-style");
        new Thread(() -> {
            Task<Double> energyFillTask = new Task<Double>() {
                @Override
                protected Double call() throws Exception {
                    for ( int id = 0 ; id < energyFillTime ; id++ ) {
                        try {
                            updateProgress(id+1,energyFillTime);
                            Thread.sleep(1000);
                        }catch ( Exception e ) {
                            e.printStackTrace();
                        }
                    }
                    isEnergyFull=true;
                    currentPlayer.getEnergyBar().getStyleClass().removeIf(s->s.equalsIgnoreCase("energy-fill-style"));
                    new AudioPlayer().play("fullEnergy",1,energyFullSoundVolume);
                    return null;
                }
            };
            Platform.runLater(() -> currentPlayer.getEnergyBar().progressProperty().bind(energyFillTask.progressProperty()));
            energyFillTask.run();
        }).start();
    }
}
