package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.ImageManipulator;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;
import java.util.Map;

public class BlackMonkeyPower implements Power {
    private final int energyFillTime = 3;
    private final double BANANA_SPEED = 20;
    private final int BACK_STEP_COUNT = 100;
    private final double BANANA_SIZE = GameZone.ICON_WIDTH-10;
    private final double PADDING_SIZE = BANANA_SIZE;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private boolean isEnergyFull;
    private final ImageManipulator imageManipulator;
    ImageView bananaIcon;

    public BlackMonkeyPower(Player currentPlayer, ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        isEnergyFull = true;
        imageManipulator = new ImageManipulator();
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        allPlayers.removeIf(p -> p==currentPlayer );
//        new AudioPlayer().play("blackMonkey",1,1);
        shoot();

        startEnergyFillTimer();
    }

    private void shoot() {
        new AudioPlayer().play("blackMonkeyThrow",1,1);
        bananaIcon = new ImageView(imageManipulator.getImage("banana"));
        bananaIcon.setFitWidth(BANANA_SIZE);
        bananaIcon.setFitHeight(BANANA_SIZE);
        double playerXPos = currentPlayer.getIcon().getLayoutX();
        double playerYPos = currentPlayer.getIcon().getLayoutY();
        AnchorPane parent = (AnchorPane) currentPlayer.getIcon().getParent();
        bananaIcon.setLayoutX(playerXPos);
        bananaIcon.setLayoutY(playerYPos);

        new Thread(() -> {
            final Map<String,Boolean> isKePressed = new HashMap<>();
            isKePressed.put("up",false); isKePressed.put("down",false); isKePressed.put("left",false); isKePressed.put("right",false);
            boolean isUp = false; boolean isDown = false; boolean isLeft = false; boolean isRight = false;

            if ( currentPlayer.isKeyPressed("up") ) {
                isUp = true;
                isKePressed.put("up",true);
            } else if ( currentPlayer.isKeyPressed("down") ) {
                isDown = true;
                isKePressed.put("down",true);
            }
            if ( currentPlayer.isKeyPressed("left") ) {
                isLeft = true;
                isKePressed.put("left",true);
            } else if ( currentPlayer.isKeyPressed("right") ) {
                isRight = true;
                isKePressed.put("right",true);
            }

            if ( isUp || isDown || isLeft || isRight ) Platform.runLater(() -> parent.getChildren().add(bananaIcon));

            AnimationTimer bananaShotThread = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    if ( ((bananaIcon.getLayoutX() > GameZone.gameBounds.get("left")-10 && bananaIcon.getLayoutX() < GameZone.gameBounds.get("right")+BANANA_SIZE) &&
                            (bananaIcon.getLayoutY() > GameZone.gameBounds.get("up")-10 && bananaIcon.getLayoutY() < GameZone.gameBounds.get("down")+BANANA_SIZE))) {
                        if ( isKePressed.get("up") ) {
                            bananaIcon.setLayoutY(bananaIcon.getLayoutY()-BANANA_SPEED);
                        }
                        if ( isKePressed.get("down") ) {
                            bananaIcon.setLayoutY(bananaIcon.getLayoutY()+BANANA_SPEED);
                        }
                        if ( isKePressed.get("left") ) {
                            bananaIcon.setLayoutX(bananaIcon.getLayoutX()-BANANA_SPEED);
                        }
                        if ( isKePressed.get("right") ) {
                            bananaIcon.setLayoutX(bananaIcon.getLayoutX()+BANANA_SPEED);
                        }
                    } else {
                        parent.getChildren().removeIf( c -> c == bananaIcon );
                        this.stop();
                    }


                    // If banana hit to the prey...
                    for ( Player player : allPlayers ) {
                        double hunterXPos = bananaIcon.getLayoutX();
                        double hunterYPos = bananaIcon.getLayoutY();
                        double preyXPos = player.getIcon().getLayoutX();
                        double preyYPos = player.getIcon().getLayoutY();
                        if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                                ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                            if ( currentPlayer.isHunter() ) {
                                parent.getChildren().removeIf( c -> c==player.getIcon() );
                            } else {
                                if ( player.isHunter() ) {
                                    if ( isKePressed.get("up") ) {
                                        new Thread(() -> {
                                            for ( int id = 0 ; id < BACK_STEP_COUNT ; id++ ) {
                                                moveWithinBound(player,player.getIcon().getLayoutX(),player.getIcon().getLayoutY()-player.getSpeed());
                                                new TimeManipulator().waitTimerInMilliSec(player.getSpeed());
                                            }
                                        }).start();
                                    }
                                    if ( isKePressed.get("down") ) {
                                        new Thread(() -> {
                                            for ( int id = 0 ; id < BACK_STEP_COUNT ; id++ ) {
                                                moveWithinBound(player,player.getIcon().getLayoutX(),player.getIcon().getLayoutY()+player.getSpeed());
                                                new TimeManipulator().waitTimerInMilliSec(player.getSpeed());
                                            }
                                        }).start();
                                    }
                                    if ( isKePressed.get("left") ) {
                                        new Thread(() -> {
                                            for ( int id = 0 ; id < BACK_STEP_COUNT ; id++ ) {
                                                moveWithinBound(player,player.getIcon().getLayoutX()-player.getSpeed(),player.getIcon().getLayoutY());
                                                new TimeManipulator().waitTimerInMilliSec(player.getSpeed());
                                            }
                                        }).start();
                                    }
                                    if ( isKePressed.get("right") ) {
                                        new Thread(() -> {
                                            for ( int id = 0 ; id < BACK_STEP_COUNT ; id++ ) {
                                                moveWithinBound(player,player.getIcon().getLayoutX()+player.getSpeed(),player.getIcon().getLayoutY());
                                                new TimeManipulator().waitTimerInMilliSec(player.getSpeed());
                                            }
                                        }).start();
                                    }
                                }
                            }

                            parent.getChildren().removeIf(c -> c==bananaIcon);
                            this.stop();
                            break;
                        }
                    }



                }
            };
            bananaShotThread.start();
        }).start();
    }

    private void moveWithinBound(Player player, double x, double y) {
        if ( x >= GameZone.gameBounds.get("left") && x <= GameZone.gameBounds.get("right") ) {
            player.getIcon().setLayoutX(x);
        }
        if ( y >= GameZone.gameBounds.get("up") && y <= GameZone.gameBounds.get("down") ) {
            player.getIcon().setLayoutY(y);
        }
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
                    new AudioPlayer().play("blackMonkeyReload",1,energyFullSoundVolume);
                    return null;
                }
            };
            Platform.runLater(() -> currentPlayer.getEnergyBar().progressProperty().bind(energyFillTask.progressProperty()));
            energyFillTask.run();
        }).start();
    }
}
