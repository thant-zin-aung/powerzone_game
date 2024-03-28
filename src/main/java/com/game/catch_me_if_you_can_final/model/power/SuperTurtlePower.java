package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.Player;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class SuperTurtlePower implements Power {
    private final int energyFillTime = 5;
    private final int SPEED_DURATION = 7;
    private final int FAST_SPEED = Player.PLAYER_SPEED*2;
    private final Player player;
    private boolean isEnergyFull;

    public SuperTurtlePower(Player player) {
        this.player = player;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        audioPlayer.play("superTurtle",1,1);
        audioPlayer.play("turtleRun",1,0.8);

        new Thread(() -> {
            for (int id = Player.PLAYER_SPEED ; id <= FAST_SPEED ; id++ ) {
                try {
                    Thread.sleep(100);
                    player.setSpeed(id);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for ( int id = 0 ; id < SPEED_DURATION ; id++ ) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            player.setSpeed(Player.PLAYER_SPEED);
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
                    for ( int id = 0 ; id < energyFillTime+SPEED_DURATION ; id++ ) {
                        try {
                            updateProgress(id+1,energyFillTime+SPEED_DURATION);
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
