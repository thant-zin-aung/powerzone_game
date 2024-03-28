package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AnimationStyle;
import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.Player;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class PinkBunnyPower implements Power {

    // second
    private final int energyFillTime = 6;
    private final double FADE_DURATION = 5;
    // millisecond
    private final double FADE_DELAY = 500;
    private final Player player;
    private boolean isEnergyFull;

    public PinkBunnyPower(Player player) {
        this.player = player;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("pinkBunny",1,1);

        new Thread(() -> {
            AnimationStyle.playFadeEffect(player.getIcon(),FADE_DELAY,1,false,1,0);
            isEnergyFull = false;
            for ( int id = 0 ; id < FADE_DURATION ; id++ ) {
                try {
                    Thread.sleep(1000);
                } catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
            AnimationStyle.playFadeEffect(player.getIcon(),FADE_DELAY,1,false,0,1);
            startEnergyFillTimer();
        }).start();
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