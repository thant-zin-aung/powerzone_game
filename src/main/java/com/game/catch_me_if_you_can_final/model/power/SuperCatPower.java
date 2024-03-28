package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.NumberGenerator;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.ImageManipulator;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class SuperCatPower implements Power {
    private final int energyFillTime = 15;
    private final int DUPLICATE_DURATION = 7;
    private final int JUMPING_TIMES = 15;
    // using milliseconds...
    private final int JUMPING_DURATION = 200;
    private final int FLASH_DURATION = 100;
    private final int FLASH_SPEED = Player.PLAYER_SPEED*8;
    private final int TOTAL_DUPLICATE_NINJA = 3;
    private final double PADDING_SIZE = GameZone.ICON_WIDTH;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private boolean isEnergyFull;
    private ObservableList<String> ninjaSounds;
    private ObservableList<StackPane> duplicateNinjas;
    private AudioPlayer audioPlayer;
    private ImageManipulator imageManipulator;
    private ObservableList<ObservableList<Double>> angles;
    private AnchorPane parent;
    private double moveXPos,jumpXPos;
    private double moveYPos,jumpYPos;
    private int flashSoundCounter = 0;

    public SuperCatPower(Player player,ObservableList<Player> allPlayers) {
        this.currentPlayer = player;
        this.allPlayers = allPlayers;
        audioPlayer = new AudioPlayer();
        imageManipulator = new ImageManipulator();
        angles = FXCollections.observableArrayList();
        ninjaSounds = FXCollections.observableArrayList("superCatNinja1","superCatNinja2","superCatNinja3","superCatNinja4","superCatNinja5","superCatNinja6","superCatNinja7");
        duplicateNinjas = FXCollections.observableArrayList();
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        parent = (AnchorPane) currentPlayer.getIcon().getParent();

        if ( !currentPlayer.isHunter() ) {
            new Thread(() -> {
                Platform.runLater(() -> parent.getChildren().removeIf(c -> c== currentPlayer.getIcon()));
                angles.add(FXCollections.observableArrayList(GameZone.gameBounds.get("left"),GameZone.gameBounds.get("up")));
                angles.add(FXCollections.observableArrayList(GameZone.gameBounds.get("left"),GameZone.gameBounds.get("down")));
                angles.add(FXCollections.observableArrayList(GameZone.gameBounds.get("right"),GameZone.gameBounds.get("up")));
                angles.add(FXCollections.observableArrayList(GameZone.gameBounds.get("right"),GameZone.gameBounds.get("down")));

                duplicateNinjas.add(currentPlayer.getIcon());
                ((VBox) currentPlayer.getIcon().getChildren().get(1)).getChildren().get(0).setVisible(false);
                for ( int id = 0 ; id < TOTAL_DUPLICATE_NINJA ; id++ ) {
                    StackPane duplicateNinja = new StackPane();
                    duplicateNinja.setPrefWidth(currentPlayer.getIcon().getPrefWidth());
                    duplicateNinja.setPrefHeight(currentPlayer.getIcon().getPrefHeight());
                    ImageView oriIcon = (ImageView) currentPlayer.getIcon().getChildren().get(0);
                    ImageView icon = new ImageView(imageManipulator.getImage("superCat"));
                    icon.setFitWidth(oriIcon.getFitWidth());
                    icon.setFitHeight(oriIcon.getFitHeight());
                    duplicateNinja.getChildren().add(icon);
                    duplicateNinjas.add(duplicateNinja);
                }
                while ( duplicateNinjas.size() > 0 ) {
                    new Thread(() -> {
                        StackPane currentNinja = duplicateNinjas.get(NumberGenerator.generate(0,duplicateNinjas.size()));
                        currentNinja.setLayoutX(angles.get(0).get(0));
                        currentNinja.setLayoutY(angles.get(0).get(1));
                        Platform.runLater(() -> parent.getChildren().add(currentNinja));
                        angles.remove(0);
                        duplicateNinjas.removeIf(n -> n==currentNinja);
                        audioPlayer.play("superCatDuplicate",1,1);

                        new TimeManipulator().waitTimer(DUPLICATE_DURATION);
                        if ( currentNinja != currentPlayer.getIcon() ) {
                            Platform.runLater(() -> parent.getChildren().removeIf( c -> c == currentNinja ));
                        }
                        ((VBox) currentPlayer.getIcon().getChildren().get(1)).getChildren().get(0).setVisible(true);
                    }).start();
                    new TimeManipulator().waitTimerInMilliSec(300);
                }
            }).start();
        }
        // if current player is a hunter...
        else {
            Thread jumperThread = new Thread(() -> {
                allPlayers.removeIf( p -> p == currentPlayer);
                if ( !currentPlayer.getIcon().getStyleClass().contains("red-power-effect") ) currentPlayer.getIcon().getStyleClass().add("red-power-effect");
                for ( int id = 0 ; id < JUMPING_TIMES ; id++ ) {
                    // delay milli second here...
                    new TimeManipulator().waitTimerInMilliSec(JUMPING_DURATION);
                    jumpXPos = NumberGenerator.generate((int)Math.round(GameZone.gameBounds.get("right")),(int)Math.round(GameZone.gameBounds.get("left")));
                    jumpYPos = NumberGenerator.generate((int)Math.round(GameZone.gameBounds.get("down")),(int)Math.round(GameZone.gameBounds.get("up")));
                    audioPlayer.play("superCatDuplicate",1,0.1);
                    currentPlayer.getIcon().setLayoutX(jumpXPos);
                    currentPlayer.getIcon().setLayoutY(jumpYPos);

                    allPlayers.parallelStream().forEach(p -> {
                        double hunterXPos = jumpXPos;
                        double hunterYPos = jumpYPos;
                        double preyXPos = p.getIcon().getLayoutX();
                        double preyYPos = p.getIcon().getLayoutY();
                        if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                                ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                            if ( !p.isTemporaryHunter() ) {
                                Platform.runLater(() -> parent.getChildren().removeIf( c -> c == p.getIcon() ));
                            }
                        }
                    });

                }
            });
            Thread flashThread = new Thread(() -> {
                audioPlayer.play(ninjaSounds.get(flashSoundCounter),1,1);
                flashSoundCounter++;
                moveXPos = NumberGenerator.generate((int)Math.round(GameZone.gameBounds.get("right")),(int)Math.round(GameZone.gameBounds.get("left")));
                moveYPos = NumberGenerator.generate((int)Math.round(GameZone.gameBounds.get("down")),(int)Math.round(GameZone.gameBounds.get("up")));
                new AnimationTimer() {
                    @Override
                    public void handle(long l) {
                        double currentXPos = Math.round(currentPlayer.getIcon().getLayoutX());
                        double currentYPos = Math.round(currentPlayer.getIcon().getLayoutY());
                        // if current position and next move position are not equal...
                        if ( !(( moveXPos-currentXPos > -FLASH_SPEED && moveXPos-currentXPos < FLASH_SPEED ) &&
                                ( moveYPos-currentYPos > -FLASH_SPEED && moveYPos-currentYPos < FLASH_SPEED )) ) {
                            double xSpeed = 0;
                            double ySpeed = 0;
                            if ( ( moveXPos-currentXPos > -FLASH_SPEED && moveXPos-currentXPos < FLASH_SPEED ) ) {
                                xSpeed = 0;
                            } else if ( moveXPos > currentXPos ) {
                                xSpeed = FLASH_SPEED;
                            } else {
                                xSpeed = -FLASH_SPEED;
                            }
                            if ( ( moveYPos-currentYPos > -FLASH_SPEED && moveYPos-currentYPos < FLASH_SPEED ) ) {
                                ySpeed = 0;
                            } else if ( moveYPos > currentYPos ) {
                                ySpeed = FLASH_SPEED;
                            } else {
                                ySpeed = -FLASH_SPEED;
                            }
                            currentPlayer.getIcon().setLayoutX(currentPlayer.getIcon().getLayoutX()+xSpeed);
                            currentPlayer.getIcon().setLayoutY(currentPlayer.getIcon().getLayoutY()+ySpeed);

                            allPlayers.forEach(p -> {
                                double hunterXPos = currentPlayer.getIcon().getLayoutX();
                                double hunterYPos = currentPlayer.getIcon().getLayoutY();
                                double preyXPos = p.getIcon().getLayoutX();
                                double preyYPos = p.getIcon().getLayoutY();
                                if ( ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                                        ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                                    if ( !p.isTemporaryHunter() ) {
                                        Platform.runLater(() -> parent.getChildren().removeIf( c -> c == p.getIcon() ));
                                    }
                                }
                            });

                        } else {
                            audioPlayer.play(ninjaSounds.get(flashSoundCounter),1,1);
                            flashSoundCounter++;
                            moveXPos = NumberGenerator.generate((int)Math.round(GameZone.gameBounds.get("right")),(int)Math.round(GameZone.gameBounds.get("left")));
                            moveYPos = NumberGenerator.generate((int)Math.round(GameZone.gameBounds.get("down")),(int)Math.round(GameZone.gameBounds.get("up")));
                            if ( flashSoundCounter == ninjaSounds.size() ) {
                                flashSoundCounter = 0;
                                currentPlayer.getIcon().getStyleClass().removeIf(s -> s.equalsIgnoreCase("red-power-effect"));
                                this.stop();
                            }
                        }
                    }
                }.start();
            });

            new Thread(() -> {
                try {
                    jumperThread.start();
                    jumperThread.join();
                    flashThread.start();
                } catch (Exception e) {
                    e.printStackTrace();
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
                    ((VBox) currentPlayer.getIcon().getChildren().get(1)).getChildren().get(0).setVisible(true);
                    return null;
                }
            };
            Platform.runLater(() -> currentPlayer.getEnergyBar().progressProperty().bind(energyFillTask.progressProperty()));
            energyFillTask.run();
        }).start();
    }
}
