package com.game.catch_me_if_you_can_final.model.power;

import com.game.catch_me_if_you_can_final.model.AudioPlayer;
import com.game.catch_me_if_you_can_final.model.GameZone;
import com.game.catch_me_if_you_can_final.model.NumberGenerator;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.manipulator.ImageManipulator;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class OrangeMonkeyPower implements Power {
    private final int energyFillTime = 5;
    private final double STEP_COUNT = 50;
    private final int NON_HUNTER_BANANA_COVER_TIME = 5;
    private final int HUNTER_BANANA_COVER_TIME = 30;
    private final double BANANA_COVER_SIZE = GameZone.ICON_WIDTH-5;
    private final double PADDING_SIZE = BANANA_COVER_SIZE;
    private final Player currentPlayer;
    private final ObservableList<Player> allPlayers;
    private Player hunter;
    private boolean isEnergyFull;
    private final ImageManipulator imageManipulator;
    private AnchorPane parent = null;

    public OrangeMonkeyPower(Player currentPlayer,ObservableList<Player> allPlayers) {
        this.currentPlayer = currentPlayer;
        this.allPlayers = allPlayers;
        imageManipulator = new ImageManipulator();
        isEnergyFull = true;
    }

    @Override
    public void use() {
        if ( !isEnergyFull ) return;
        //        new AudioPlayer().play("blueBunny",1,1);


        // if current player is not a hunter...
        if ( !currentPlayer.isHunter() ) {
            ImageView bananaCoverIcon = new ImageView(imageManipulator.getImage("bananaCover"));
            bananaCoverIcon.setFitWidth(BANANA_COVER_SIZE);
            bananaCoverIcon.setFitHeight(BANANA_COVER_SIZE);
            bananaCoverIcon.setLayoutX(currentPlayer.getIcon().getLayoutX());
            bananaCoverIcon.setLayoutY(currentPlayer.getIcon().getLayoutY());

            parent = (AnchorPane) currentPlayer.getIcon().getParent();
            parent.getChildren().add(bananaCoverIcon);

            for ( Player player : allPlayers ) {
                if ( player.isHunter() ) {
                    hunter = player;
                    break;
                }
            }

            new AnimationTimer() {
                @Override
                public void handle(long l) {
                    double hunterXPos = bananaCoverIcon.getLayoutX();
                    double hunterYPos = bananaCoverIcon.getLayoutY();
                    double preyXPos = hunter.getIcon().getLayoutX();
                    double preyYPos = hunter.getIcon().getLayoutY();
                    if (  ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                            ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                        parent.getChildren().removeIf(c->c==bananaCoverIcon);

                        new Thread(() -> {
                            int xSpeed = (NumberGenerator.generate(0,2) == 0 ) ? -hunter.getSpeed() : hunter.getSpeed();
                            int ySpeed = (NumberGenerator.generate(0,2) == 0 ) ? -hunter.getSpeed() : hunter.getSpeed();
                            for ( int id = 0 ; id < STEP_COUNT ; id++ ) {
                                moveWithinBound(hunter,hunter.getIcon().getLayoutX()+xSpeed,hunter.getIcon().getLayoutY());
                                moveWithinBound(hunter,hunter.getIcon().getLayoutX(),hunter.getIcon().getLayoutY()+ySpeed);
                                new TimeManipulator().waitTimerInMilliSec(hunter.getSpeed());
                            }
                        }).start();

                        this.stop();
                    }
                }
            }.start();

            new Thread(() -> {
                new TimeManipulator().waitTimer(NON_HUNTER_BANANA_COVER_TIME);
                Platform.runLater(()->parent.getChildren().removeIf(c -> c==bananaCoverIcon));
            }).start();
        }
        // if current player is a hunter...
        else {
            allPlayers.removeIf(p -> p==currentPlayer);

            ImageView bananaCoverIcon = new ImageView(imageManipulator.getImage("bananaCover"));
            bananaCoverIcon.setFitWidth(BANANA_COVER_SIZE);
            bananaCoverIcon.setFitHeight(BANANA_COVER_SIZE);
            bananaCoverIcon.setLayoutX(currentPlayer.getIcon().getLayoutX());
            bananaCoverIcon.setLayoutY(currentPlayer.getIcon().getLayoutY());

            parent = (AnchorPane) currentPlayer.getIcon().getParent();
            parent.getChildren().add(bananaCoverIcon);

            new AnimationTimer() {
                @Override
                public void handle(long l) {
                    for ( Player player : allPlayers ) {
                        double hunterXPos = bananaCoverIcon.getLayoutX();
                        double hunterYPos = bananaCoverIcon.getLayoutY();
                        double preyXPos = player.getIcon().getLayoutX();
                        double preyYPos = player.getIcon().getLayoutY();
                        if (  ((hunterXPos-preyXPos >= 0 && hunterXPos-preyXPos <= PADDING_SIZE) || (hunterXPos-preyXPos >= -PADDING_SIZE && hunterXPos-preyXPos <= 0)) &&
                                ((hunterYPos-preyYPos >= 0 && hunterYPos-preyYPos <= PADDING_SIZE) || (hunterYPos-preyYPos >= -PADDING_SIZE && hunterYPos-preyYPos <= 0)) ) {
                            parent.getChildren().removeIf(c->c==bananaCoverIcon);

                            new Thread(() -> {
                                int xSpeed = (NumberGenerator.generate(0,2) == 0 ) ? -player.getSpeed() : player.getSpeed();
                                int ySpeed = (NumberGenerator.generate(0,2) == 0 ) ? -player.getSpeed() : player.getSpeed();
                                for ( int id = 0 ; id < STEP_COUNT ; id++ ) {
                                    moveWithinBound(player,player.getIcon().getLayoutX()+xSpeed,player.getIcon().getLayoutY());
                                    moveWithinBound(player,player.getIcon().getLayoutX(),player.getIcon().getLayoutY()+ySpeed);
                                    new TimeManipulator().waitTimerInMilliSec(player.getSpeed());
                                }
                            }).start();

                            this.stop();
                        }
                    }
                }
            }.start();

            new Thread(() -> {
                new TimeManipulator().waitTimer(HUNTER_BANANA_COVER_TIME);
                Platform.runLater(()->parent.getChildren().removeIf(c -> c==bananaCoverIcon));
            }).start();
        }


        startEnergyFillTimer();
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
                    new AudioPlayer().play("fullEnergy",1,energyFullSoundVolume);
                    return null;
                }
            };
            Platform.runLater(() -> currentPlayer.getEnergyBar().progressProperty().bind(energyFillTask.progressProperty()));
            energyFillTask.run();
        }).start();
    }
}
