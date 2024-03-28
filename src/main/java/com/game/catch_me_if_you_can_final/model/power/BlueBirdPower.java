package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AnimationStyle;
import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class BlueBirdPower implements Power {
    private final int energyFillTime = 5;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private Player hunter;
    private final AnimationStyle animationStyle;
    private final double PADDING_SIZE = GameZone.ICON_WIDTH;
    private boolean isEnergyFull;
    private final String POWER_EFFECT_NAME = "deep-think-pink-power-effect";
    private boolean isAllyAlive = true;


    public BlueBirdPower(Player currentPlayer, ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        this.animationStyle = new AnimationStyle();
        isEnergyFull = true;
        enableFame();
        useAbility();
        startHunterGotPreyMonitor();
    }

    @Override
    public void use() {
        if ( !isEnergyFull || currentPlayer.isHunter() ) return;
        new AudioPlayer().play("blueBird",1,1);

        for ( Player player : allPlayers ) {
            if ( player.isHunter() || player == currentPlayer ) continue;

            ImageView playerIcon = (ImageView) player.getIcon().getChildren().get(0);
            if ( !playerIcon.getStyleClass().contains(POWER_EFFECT_NAME) ) {
                playerIcon.getStyleClass().add(POWER_EFFECT_NAME);
            }
            player.setTemporaryHunter(true);
        }

        startEnergyFillTimer();
    }

    private void startHunterGotPreyMonitor() {
        new Thread(() -> {
            new TimeManipulator().waitTimer(1);
            if ( currentPlayer.isHunter() ) return;
            allPlayers.forEach( p -> {
                if ( p.isHunter() ) hunter = p;
            });
            new AnimationTimer() {
                @Override
                public void handle(long l) {
                    if ( !isAllyAlive ) this.stop();
                    for ( Player p : allPlayers ) {
                        if ( p == currentPlayer && isAllyAlive ) continue;
                        double hunterXPos = hunter.getIcon().getLayoutX();
                        double hunterYPos = hunter.getIcon().getLayoutY();
                        double preyXPos = p.getIcon().getLayoutX();
                        double preyYPos = p.getIcon().getLayoutY();
                        if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                                ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                            if ( p.isTemporaryHunter() ) {
                                new Thread(() -> {
                                    p.getIcon().getChildren().get(0).getStyleClass().remove(POWER_EFFECT_NAME);
                                    new TimeManipulator().waitTimer(1);
                                    p.setTemporaryHunter(false);
                                }).start();
                            }
                        }
                    }
                }
            }.start();
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
            if ( !playerIcon.getStyleClass().contains("deep-pink-power-effect") ) {
                playerIcon.getStyleClass().add("deep-pink-power-effect");
            }
            energyBar.setStyle("-fx-accent: deeppink");
            animationStyle.sprinkleFlyingEffect(fameNode,7000,1000,2000,new int[]{1,2}, Color.DEEPPINK);
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
