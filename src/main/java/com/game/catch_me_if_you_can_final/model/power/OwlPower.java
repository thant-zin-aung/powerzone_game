package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AnimationStyle;
import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.layout.AnchorPane;

public class OwlPower implements Power {
    private final int energyFillTime = 30;
    private final int FADING_DURATION = 10;
    // using milliseconds
    private final double FADE_TRANSITION_TIME = 300;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private boolean isEnergyFull;
    private boolean isPowerUsing;

    public OwlPower(Player currentPlayer, ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("owl",1,1);
        AnchorPane parent = (AnchorPane) currentPlayer.getIcon().getParent();
        parent.setStyle("-fx-background-color: black");
        isPowerUsing = true;
        allPlayers.forEach( player -> {
            AnimationStyle.playFadeEffect(player.getIcon(),FADE_TRANSITION_TIME,1,false,1,0);

        });
        new Thread(() -> {
            new TimeManipulator().waitTimer(FADING_DURATION);
            allPlayers.forEach( player -> {
                AnimationStyle.playFadeEffect(player.getIcon(),FADE_TRANSITION_TIME,1,false,0,1);
            });
            parent.setStyle("-fx-background-color: white");
        }).start();
        startEnergyFillTimer();
    }

    private void startEnergyFillTimer() {
        isEnergyFull=false;
        currentPlayer.getEnergyBar().getStyleClass().add("energy-fill-style");
        new Thread(() -> {
            Task<Double> energyFillTask = new Task<Double>() {
                @Override
                protected Double call() throws Exception {
                    for ( int id = 0 ; id < energyFillTime+FADING_DURATION ; id++ ) {
                        try {
                            updateProgress(id+1,energyFillTime+FADING_DURATION);
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
