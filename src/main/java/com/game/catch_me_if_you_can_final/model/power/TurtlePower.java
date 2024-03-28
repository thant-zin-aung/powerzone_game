package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.NumberGenerator;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class TurtlePower implements Power {
    private final int energyFillTime = 10;
    private final int slowTime = 2;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private boolean isEnergyFull;

    public TurtlePower(Player currentPlayer,ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        new AudioPlayer().play("turtle",1,1);
        allPlayers.removeIf(p -> p==currentPlayer);
        if ( currentPlayer.isHunter() ) {
            new Thread(() -> {
                int randomId = NumberGenerator.generate(0,allPlayers.size());
                for (int id = Player.PLAYER_SPEED ; id > 0 ; --id ) {
                    try {
                        allPlayers.get(randomId).setSpeed(id);
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("No player to slow...");
                    }
                }
                for (int id = 0 ; id < slowTime ; id++ ) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    allPlayers.get(randomId).setSpeed(Player.PLAYER_SPEED);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("No player to use power....");
                }
            }).start();
        } else {
            new Thread(() -> {
                Player hunter = null;
                for ( Player player : allPlayers ) {
                    System.out.println(player.getName());
                    if (player.isHunter()) {
                        hunter = player;
                        break;
                    }
                }
                for (int id = Player.PLAYER_SPEED ; id > 0 ; --id ) {
                    try {
                        hunter.setSpeed(id);
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        System.out.println("No player to slow...");
                    }
                }
                new TimeManipulator().waitTimer(slowTime);
                try {
                    hunter.setSpeed(Player.PLAYER_SPEED);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("No player to use power....");
                }
            }).start();
        }

        startEnergyFillTimer();
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
