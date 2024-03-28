package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class KoalaPower implements Power {
    private final int energyFillTime = 10;
    private final double FLYING_SPEED = 2;
    private final int FLY_TIME = 3;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private boolean isEnergyFull;
    private boolean isFlying;

    public KoalaPower(Player currentPlayer, ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("mouse",1,1);
        allPlayers.removeIf(p->p==currentPlayer);
        isFlying = true;

        new Thread(() -> {
            while (isFlying) {
                for ( Player player : allPlayers ) {
                    player.setPressedKey(player.getUpKey(),false);
                    player.setPressedKey(player.getDownKey(),false);
                    player.setPressedKey(player.getLeftKey(),false);
                    player.setPressedKey(player.getRightKey(),false);
                }
                new TimeManipulator().waitTimerInMilliSec(1);
            }
        }).start();
        new Thread(() -> {
            while (isFlying) {
                for ( Player player : allPlayers ) {
                    moveWithinBound(player,player.getIcon().getLayoutX(),player.getIcon().getLayoutY()-FLYING_SPEED);
                }
                new TimeManipulator().waitTimerInMilliSec(300);
            }
        }).start();
        new Thread(() -> {
            new TimeManipulator().waitTimer(FLY_TIME);
            isFlying = false;
        }).start();


        startEnergyFillTimer();
    }

    private void moveWithinBound(Player player, double x, double y) {
        if ( x >= GameZone.gameBounds.get("left") && x <= GameZone.gameBounds.get("right") ) {
            player.getIcon().setLayoutX(x);
        }
        if ( y >= GameZone.gameBounds.get("up") && y <= GameZone.gameBounds.get("down") ) {
            player.getIcon().setLayoutY(y);
        }
    }

    private void startEnergyFillTimer() {
        isEnergyFull=false;
        currentPlayer.getEnergyBar().getStyleClass().add("energy-fill-style");
        new Thread(() -> {
            Task<Double> energyFillTask = new Task<Double>() {
                @Override
                protected Double call() throws Exception {
                    for ( int id = 0 ; id < energyFillTime+FLY_TIME ; id++ ) {
                        try {
                            updateProgress(id+1,energyFillTime+FLY_TIME);
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
