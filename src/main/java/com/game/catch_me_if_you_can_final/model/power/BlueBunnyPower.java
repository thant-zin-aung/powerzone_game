package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.Player;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class BlueBunnyPower implements Power {
    private final int energyFillTime = 5;
    private final double STEP_COUNT = 100;
    private final Player player;
    private boolean isEnergyFull;

    public BlueBunnyPower(Player player) {
        this.player = player;
        // Change move length here...
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("blueBunny",1,1);
//        new AudioPlayer().play("flash",1);
        if ( player.isKeyPressed("up") ) {
            new Thread(() -> {
                for (int id = 0; id < STEP_COUNT; id++ ) {
                    try {
                        Platform.runLater(() -> moveWithinBound(player.getIcon().getLayoutX(),player.getIcon().getLayoutY()-player.getSpeed()));
                        Thread.sleep(player.getSpeed());
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if ( player.isKeyPressed("down") ) {
            new Thread(() -> {
                for (int id = 0; id < STEP_COUNT; id++ ) {
                    try {
                        Platform.runLater(() -> moveWithinBound(player.getIcon().getLayoutX(),player.getIcon().getLayoutY()+player.getSpeed()));
                        Thread.sleep(player.getSpeed());
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if ( player.isKeyPressed("left") ) {
            new Thread(() -> {
                for (int id = 0; id < STEP_COUNT; id++ ) {
                    try {
                        Platform.runLater(() -> moveWithinBound(player.getIcon().getLayoutX()-player.getSpeed(),player.getIcon().getLayoutY()));
                        Thread.sleep(player.getSpeed());
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if ( player.isKeyPressed("right") ) {
            new Thread(() -> {
                for (int id = 0; id < STEP_COUNT; id++ ) {
                    try {
                        Platform.runLater(() -> moveWithinBound(player.getIcon().getLayoutX()+player.getSpeed(),player.getIcon().getLayoutY()));
                        Thread.sleep(player.getSpeed());
                    } catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        startEnergyFillTimer();
    }

    private void moveWithinBound( double x, double y) {
        if ( x >= GameZone.gameBounds.get("left") && x <= GameZone.gameBounds.get("right") ) {
            player.getIcon().setLayoutX(x);
        }
        if ( y >= GameZone.gameBounds.get("up") && y <= GameZone.gameBounds.get("down") ) {
            player.getIcon().setLayoutY(y);
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
