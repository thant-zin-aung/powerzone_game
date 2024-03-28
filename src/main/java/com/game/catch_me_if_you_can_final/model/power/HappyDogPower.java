package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AnimationStyle;
import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class HappyDogPower implements Power {
    private final int energyFillTime = 20;
    private final int CRAZY_TIME = 10;
    private final Player currentPlayer;
    private Player hunter;
    private final ObservableList<Player> allPlayers;
    private boolean isEnergyFull;
    private boolean isCrazy;

    public HappyDogPower(Player currentPlayer, ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        audioPlayer.play("happyDog",1,1);
        new AudioPlayer().play("happyDog2",1,0.6);
        allPlayers.removeIf(p -> p == currentPlayer );
        // Searching for hunter...
        for ( Player player : allPlayers ) {
            if ( player.isHunter() ) {
                hunter = player;
                break;
            }
        }

        new Thread(() -> {
            isCrazy = true;
            new Thread(() -> {
                while(isCrazy) {
                    if ( currentPlayer.isHunter() ) {
                        allPlayers.forEach(p -> {
                            AnimationStyle.playRotateEffect(p.getIcon(),700,1,false,0,360);
                        });
                    } else {
                        AnimationStyle.playRotateEffect(hunter.getIcon(),700,1,false,0,360);
                    }
                    try {
                        Thread.sleep(700);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            switchKeys();
            new TimeManipulator().waitTimer(CRAZY_TIME);
            switchKeys();
            isCrazy = false;
        }).start();

        startEnergyFillTimer();
    }

    private void switchKeys() {
        GameZone.gameAnimationTimer.stop();
        if ( currentPlayer.isHunter() ) {
            allPlayers.forEach( player -> {
                KeyCode up = player.getKeySet().get("up");
                KeyCode down = player.getKeySet().get("down");
                KeyCode left = player.getKeySet().get("left");
                KeyCode right = player.getKeySet().get("right");
                KeyCode power = player.getKeySet().get("power");

                Map<String ,KeyCode> switchedKeySet = new HashMap<>();
                switchedKeySet.put("up",down);
                switchedKeySet.put("down",up);
                switchedKeySet.put("left",right);
                switchedKeySet.put("right",left);
                switchedKeySet.put("power",power);
                int switchedKeySetId = player.getKeySetId();

                player.setKeySet(switchedKeySet,switchedKeySetId);
            });
        } else {

            KeyCode up = hunter.getKeySet().get("up");
            KeyCode down = hunter.getKeySet().get("down");
            KeyCode left = hunter.getKeySet().get("left");
            KeyCode right = hunter.getKeySet().get("right");
            KeyCode power = hunter.getKeySet().get("power");

            Map<String ,KeyCode> switchedKeySet = new HashMap<>();
            switchedKeySet.put("up",down);
            switchedKeySet.put("down",up);
            switchedKeySet.put("left",right);
            switchedKeySet.put("right",left);
            switchedKeySet.put("power",power);
            int switchedKeySetId = hunter.getKeySetId();

            hunter.setKeySet(switchedKeySet,switchedKeySetId);
        }
        GameZone.gameAnimationTimer.start();
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
