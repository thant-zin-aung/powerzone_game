package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AnimationStyle;
import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class DogPower implements Power {
    private final int energyFillTime = 5;
    private final int IMMUNE_TIME = 5;
    private final Player player;
    private boolean isEnergyFull;
    private boolean isImmune;

    public DogPower(Player player) {
        this.player = player;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        isImmune = true;
        new AudioPlayer().play("dog",1,1);
        player.setTemporaryHunter(true);
        new Thread(() -> {
            new Thread(() -> {
                while(isImmune) {
                    AnimationStyle.playFadeEffect(player.getIcon(), 300,1,false,1,0);
                    try {
                        Thread.sleep(300);
                    } catch ( InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            for ( int id = 0 ; id < IMMUNE_TIME ; id++ ) {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            AnimationStyle.playFadeEffect(player.getIcon(), 300,1,false,0,1);
            player.setTemporaryHunter(false);
            isImmune = false;
        }).start();


        startEnergyFillTimer();
    }

    private void startEnergyFillTimer() {
        isEnergyFull=false;
        player.getEnergyBar().getStyleClass().add("energy-fill-style");
        new Thread(() -> {
            Task<Double> energyFillTask = new Task<Double>() {
                @Override
                protected Double call() throws Exception {
                    for ( int id = 0 ; id < energyFillTime+IMMUNE_TIME ; id++ ) {
                        try {
                            updateProgress(id+1,energyFillTime+IMMUNE_TIME);
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
