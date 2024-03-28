package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AnimationStyle;
import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class MousePower implements Power {
    private final int energyFillTime = 10;
    private final int SMALL_TIME = 10;
    private final double SMALL_SIZE = 0.2;
    private final int FAST_SPEED_THAN_NORMAL = 1;
    private final Player player;
    private boolean isEnergyFull;

    public MousePower(Player player) {
        this.player = player;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("mouse",1,1);

        new Thread(() -> {
            AnimationStyle.playScaleEffect(player.getIcon(),300,1,false,1,1,SMALL_SIZE,SMALL_SIZE);
            player.setSpeed(player.getSpeed()+FAST_SPEED_THAN_NORMAL);
            new TimeManipulator().waitTimer(SMALL_TIME);

            AnimationStyle.playScaleEffect(player.getIcon(),300,1,false,SMALL_SIZE,SMALL_SIZE,1,1);
            player.setSpeed(player.getSpeed()-FAST_SPEED_THAN_NORMAL);
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
                    for ( int id = 0 ; id < energyFillTime+SMALL_TIME ; id++ ) {
                        try {
                            updateProgress(id+1,energyFillTime+SMALL_TIME);
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
