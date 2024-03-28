package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.input.KeyCode;

import java.util.Map;

public class ZPandaPower implements Power {

    private final int energyFillTime = 10;
    private final double FADE_DURATION = 300;
    private final Player player;
    private boolean isEnergyFull;

    public ZPandaPower(Player player) {
        this.player = player;
        isEnergyFull = true;
    }
    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("zPanda",1,1);
        new Thread(() -> {
            Map<String ,KeyCode> keySet = player.getKeySet();
            int keySetId = player.getKeySetId();
            // Stop player from running
            GameZone.gameAnimationTimer.stop();
            keySet.keySet().forEach(key -> player.setPressedKey(keySet.get(key),false));
            GameZone.gameAnimationTimer.start();
            player.setKeySet(null,0);
            player.setTemporaryHunter(true);
            int up = Integer.parseInt(String.valueOf(Math.round(GameZone.gameBounds.get("up"))));
            int down = Integer.parseInt(String.valueOf(Math.round(GameZone.gameBounds.get("down"))));
            int left = Integer.parseInt(String.valueOf(Math.round(GameZone.gameBounds.get("left"))));
            int right = Integer.parseInt(String.valueOf(Math.round(GameZone.gameBounds.get("right"))));
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            AnimationStyle.playFadeEffect(player.getIcon(),FADE_DURATION,1,false,1,0);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            player.setTemporaryHunter(false);
            player.setKeySet(keySet,keySetId);
            player.getIcon().setLayoutX(NumberGenerator.generate(right,left));
            player.getIcon().setLayoutY(NumberGenerator.generate(down,up));
            AnimationStyle.playFadeEffect(player.getIcon(),FADE_DURATION,1,false,0,1);
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
