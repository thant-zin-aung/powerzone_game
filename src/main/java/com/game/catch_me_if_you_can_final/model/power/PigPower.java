package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.Player;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class PigPower implements Power {

    private final int energyFillTime = 5;
    private final double FLAT_DURATION = 5;
    private final int SLOW_SPEED = Player.PLAYER_SPEED/2;
    private final double FAT_SIZE = 50;
    private final double HUNTER_FAT_SIZE = 70;
    private final AudioPlayer audioPlayer;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private boolean isEnergyFull;
    private double PADDING_SIZE;

    public PigPower(Player currentPlayer,ObservableList<Player> allPlayers) {
        this.audioPlayer = new AudioPlayer();
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        isEnergyFull = true;
        PADDING_SIZE = GameZone.PADDING_SIZE+(FAT_SIZE);
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;


        if ( !currentPlayer.isHunter() ) {
            StackPane playerIcon = currentPlayer.getIcon();
            ImageView icon = (ImageView) currentPlayer.getIcon().getChildren().get(0);
            playerIcon.setPrefWidth(playerIcon.getPrefWidth()+FAT_SIZE);
            playerIcon.setPrefHeight(playerIcon.getPrefHeight()+FAT_SIZE);
            icon.setFitWidth(icon.getFitWidth()+FAT_SIZE);
            icon.setFitHeight(icon.getFitHeight()+FAT_SIZE);

            new Thread(() -> {
                currentPlayer.setTemporaryHunter(true);
                while ( !isEnergyFull ) {
                    final Player collidePlayer = getCollidedPlayer(currentPlayer);
                    if ( collidePlayer != null ) {
                        if ( !audioPlayer.getAudioClip().isPlaying() ) {
                            audioPlayer.play("pig",1,1);
                        }
//                        System.out.println(((ImageView)collidePlayer.getIcon().getChildren().get(0)).getImage().getUrl() );
                        new Thread(() -> {
                            ImageView enemyIcon = ((ImageView)collidePlayer.getIcon().getChildren().get(0));
                            enemyIcon.setFitWidth(60);
                            enemyIcon.setFitHeight(15);
                            collidePlayer.setSpeed(SLOW_SPEED);
                            for (int id=0 ; id < FLAT_DURATION ; id++ ) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            enemyIcon.setFitWidth(GameZone.ICON_WIDTH);
                            enemyIcon.setFitHeight(GameZone.ICON_HEIGHT);
                            collidePlayer.setSpeed(Player.PLAYER_SPEED);
                        }).start();
                    }
                    try {
                        Thread.sleep(15);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
//                ((ImageView)currentPlayer.getIcon().getChildren().get(0)).setFitWidth(GameZone.ICON_WIDTH);
//                ((ImageView)currentPlayer.getIcon().getChildren().get(0)).setFitHeight(GameZone.ICON_HEIGHT);
                playerIcon.setPrefWidth(playerIcon.getPrefWidth()-FAT_SIZE);
                playerIcon.setPrefHeight(playerIcon.getPrefHeight()-FAT_SIZE);
                icon.setFitWidth(icon.getFitWidth()-FAT_SIZE);
                icon.setFitHeight(icon.getFitHeight()-FAT_SIZE);
                currentPlayer.setTemporaryHunter(false);
            }).start();
        } else {
            new Thread(() -> {
                PADDING_SIZE = GameZone.ICON_WIDTH+HUNTER_FAT_SIZE;
                GameZone.PADDING_SIZE += HUNTER_FAT_SIZE;
                allPlayers.forEach( player -> {
                    StackPane playerIcon = player.getIcon();
                    ImageView icon = (ImageView) player.getIcon().getChildren().get(0);
                    playerIcon.setPrefWidth(playerIcon.getPrefWidth()+HUNTER_FAT_SIZE);
                    playerIcon.setPrefHeight(playerIcon.getPrefHeight()+HUNTER_FAT_SIZE);
                    icon.setFitWidth(icon.getFitWidth()+HUNTER_FAT_SIZE);
                    icon.setFitHeight(icon.getFitHeight()+HUNTER_FAT_SIZE);
                });
                for ( int id = 0 ; id < energyFillTime ; id++ ) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                allPlayers.forEach( player -> {
                    StackPane playerIcon = player.getIcon();
                    ImageView icon = (ImageView) player.getIcon().getChildren().get(0);
                    playerIcon.setPrefWidth(playerIcon.getPrefWidth()-HUNTER_FAT_SIZE);
                    playerIcon.setPrefHeight(playerIcon.getPrefHeight()-HUNTER_FAT_SIZE);
                    icon.setFitWidth(icon.getFitWidth()-HUNTER_FAT_SIZE);
                    icon.setFitHeight(icon.getFitHeight()-HUNTER_FAT_SIZE);
                });
                PADDING_SIZE = GameZone.ICON_WIDTH+FAT_SIZE;
                GameZone.PADDING_SIZE -= HUNTER_FAT_SIZE;

            }).start();
        }

        startEnergyFillTimer();
    }

    private Player getCollidedPlayer(Player currentPlayer) {
        Player collidePlayer = null;
        for ( Player player : allPlayers ) {
            if ( player == currentPlayer ) continue;
            double hunterXPos = currentPlayer.getIcon().getLayoutX();
            double hunterYPos = currentPlayer.getIcon().getLayoutY();
            double preyXPos = player.getIcon().getLayoutX();
            double preyYPos = player.getIcon().getLayoutY();
            if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                    ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                collidePlayer = player;
                break;
            }
        }
        return collidePlayer;
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
