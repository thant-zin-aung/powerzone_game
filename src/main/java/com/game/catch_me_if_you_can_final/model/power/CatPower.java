package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class CatPower implements Power {
    private final TimeManipulator timeManipulator;
    private final int energyFillTime = 10;
    private final int PENETRATION_TIME = 5;
    private final Player player;
    private boolean isEnergyFull;
    private boolean isPowerInUse;

    private double playerWidth;
    private double playerHeight;

    public CatPower(Player player) {
        timeManipulator = new TimeManipulator();
        this.player = player;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
//        new AudioPlayer().play("cat",1,1);
        playerWidth = this.player.getIcon().getPrefWidth();
        playerHeight = this.player.getIcon().getPrefHeight();
        isPowerInUse = true;
        player.setHasAbilityToExceedBounds(true);
        // is energy still affect thread...
        new Thread(() -> {
            while ( isPowerInUse ) {
                double xPos = player.getIcon().getLayoutX();
                double yPos = player.getIcon().getLayoutY();

                if (  xPos < GameZone.gameBounds.get("left")-playerWidth ) {
                    player.getIcon().setLayoutX(GameZone.gameBounds.get("right")+playerWidth);
                    new AudioPlayer().play("cat",1,1);
                }
                if ( xPos > GameZone.gameBounds.get("right")+playerWidth ) {
                    player.getIcon().setLayoutX(GameZone.gameBounds.get("left")-playerWidth);
                    new AudioPlayer().play("cat",1,1);
                }
                if ( yPos < GameZone.gameBounds.get("up")-playerHeight ) {
                    player.getIcon().setLayoutY(GameZone.gameBounds.get("down")+playerHeight);
                    new AudioPlayer().play("cat",1,1);
                }
                if ( yPos > GameZone.gameBounds.get("down")+playerHeight ) {
                    player.getIcon().setLayoutY(GameZone.gameBounds.get("up")-playerHeight);
                    new AudioPlayer().play("cat",1,1);
                }
                timeManipulator.waitTimerInMilliSec(15);
            }
        }).start();

        // energy fill time monitor thread...
        new Thread(() -> {
            timeManipulator.waitTimer(PENETRATION_TIME);
            isPowerInUse = false;
            player.setHasAbilityToExceedBounds(false);
            checkAndSetCatPosition();
        }).start();

        startEnergyFillTimer();
    }

    private void checkAndSetCatPosition() {
        double xPos = player.getIcon().getLayoutX();
        double yPos = player.getIcon().getLayoutY();

        if (  xPos < GameZone.gameBounds.get("left") ) {
            player.getIcon().setLayoutX(GameZone.gameBounds.get("left"));
        }
        if ( xPos > GameZone.gameBounds.get("right") ) {
            player.getIcon().setLayoutX(GameZone.gameBounds.get("right"));
        }
        if ( yPos < GameZone.gameBounds.get("up") ) {
            player.getIcon().setLayoutY(GameZone.gameBounds.get("up"));
        }
        if ( yPos > GameZone.gameBounds.get("down") ) {
            player.getIcon().setLayoutY(GameZone.gameBounds.get("down"));
        }
    }

    private void startEnergyFillTimer() {
        isEnergyFull=false;
        player.getEnergyBar().getStyleClass().add("energy-fill-style");
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
                    player.getEnergyBar().getStyleClass().removeIf(s->s.equalsIgnoreCase("energy-fill-style"));
                    new AudioPlayer().play("fullEnergy",1,energyFullSoundVolume);
                    return null;
                }
            };
            Platform.runLater(() -> player.getEnergyBar().progressProperty().bind(energyFillTask.progressProperty()));
            energyFillTask.run();
        }).start();
    }
}
