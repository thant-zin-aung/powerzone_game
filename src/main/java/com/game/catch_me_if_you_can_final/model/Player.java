package com.game.catch_me_if_you_can_final.model;

import com.game.catch_me_if_you_can_final.model.manipulator.ImageManipulator;
import com.game.catch_me_if_you_can_final.model.power.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class Player {
    public static final int PLAYER_SPEED = 4;

    private ImageManipulator imageManipulator;
    private String name;
    private ImageView icon;
    private StackPane playerIcon;
    private ProgressBar energyBar;
    private Power power;
    private boolean isHunter;
    private boolean isTemporaryHunter;
    private boolean hasAbilityToExceedBounds;
    private Map<String , KeyCode> keySet;
    private int keySetId;
    private KeyCode upKey;
    private KeyCode downKey;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private KeyCode powerKey;
    private boolean isUpKeyPressed;
    private boolean isDownKeyPressed;
    private boolean isLeftKeyPressed;
    private boolean isRightKeyPressed;
    private boolean isPowerKeyPressed;
    private int speed;
    private ObservableList<Player> allPlayers;


    public Player() {
        imageManipulator = new ImageManipulator();
        playerIcon = new StackPane();
        allPlayers = FXCollections.observableArrayList();
        this.setSpeed(PLAYER_SPEED);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(ImageView icon) {
        this.icon = icon;
        setEnergyBar();
        setPower();
        VBox energyBarWrapper = new VBox();
        energyBarWrapper.setAlignment(Pos.BOTTOM_CENTER);
        energyBarWrapper.setFillWidth(true);
        energyBarWrapper.getChildren().add(energyBar);
        playerIcon.setAlignment(Pos.CENTER);
        playerIcon.getChildren().addAll(icon,energyBarWrapper);
        if ( imageManipulator.getExtractedIconPath(icon).equalsIgnoreCase(imageManipulator.getExtractedIconPath(new ImageView(imageManipulator.getImage("blueBird")))) ||
                imageManipulator.getExtractedIconPath(icon).equalsIgnoreCase(imageManipulator.getExtractedIconPath(new ImageView(imageManipulator.getImage("loveBird"))))) {
            playerIcon.getChildren().add(new AnchorPane());
        }
    }

    private void setEnergyBar() {
        energyBar = new ProgressBar();
        energyBar.setProgress(1);
        energyBar.getStyleClass().add("energy-bar");
    }

    public void addPlayer(Player player) {
        if ( !allPlayers.contains(player) ) allPlayers.add(player);
    }

    public void removePlayer(Player player) { allPlayers.removeIf(p -> p==player); }

    private void setPower() {
        if ( imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("blueBunny")) ) {
            power = new BlueBunnyPower(this);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("pinkBunny")) ) {
            power = new PinkBunnyPower(this);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("turtle")) ) {
            power = new TurtlePower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("superTurtle")) ) {
            power = new SuperTurtlePower(this);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("pig")) ) {
            power = new PigPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("zPanda")) ) {
            power = new ZPandaPower(this);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("dog")) ) {
            power = new DogPower(this);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("happyDog")) ) {
            power = new HappyDogPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("cat")) ) {
            power = new CatPower(this);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("mouse")) ) {
            power = new MousePower(this);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("blackMonkey")) ) {
            power = new BlackMonkeyPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("orangeMonkey")) ) {
            power = new OrangeMonkeyPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("koala")) ) {
            power = new KoalaPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("superCat")) ) {
            power = new SuperCatPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("owl")) ) {
            power = new OwlPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("blueBird")) ) {
            power = new BlueBirdPower(this,allPlayers);
        } else if (imageManipulator.getExtractedIconPath(this.icon).equalsIgnoreCase(imageManipulator.getImagePath("loveBird")) ) {
            power = new LoveBirdPower(this,allPlayers);
        }
        else {
            setToNoPower();
        }
    }

    public void setToNoPower() {
        power = new NoPower();
    }

    public void setHunter(boolean hunter) {
        isHunter = hunter;
    }

    public void setTemporaryHunter(boolean temporaryHunter) { isTemporaryHunter = temporaryHunter; }

    public void setHasAbilityToExceedBounds(boolean ability) {
        hasAbilityToExceedBounds = ability;
    }

    public String getName() {
        return name;
    }

    public StackPane getIcon() {
        return playerIcon;
    }

    public ImageView getAnimalIcon() { return icon; }

    public ProgressBar getEnergyBar() {
        return energyBar;
    }

    public Power getPower() { return power; }

    public boolean isHunter() {
        return isHunter;
    }

    public boolean isTemporaryHunter() { return isTemporaryHunter; }

    public boolean hasAbilityToExceedBounds() {
        return hasAbilityToExceedBounds;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setKeySet(Map<String, KeyCode> keySet, int keySetId) {
        this.keySet = keySet;
        this.keySetId = keySetId;
        setKeys(keySet);
    }
    private void setKeys(Map<String ,KeyCode> keySet) {
        if ( keySet != null ) {
            upKey = keySet.get("up");
            downKey = keySet.get("down");
            leftKey = keySet.get("left");
            rightKey = keySet.get("right");
            powerKey = keySet.get("power");
        } else {
            upKey = null; downKey = null; leftKey = null; rightKey = null; powerKey = null;
        }
    }

    public Map<String, KeyCode> getKeySet() {
        return keySet;
    }

    public int getKeySetId() {
        return keySetId;
    }

    public KeyCode getUpKey() {
        return upKey;
    }

    public KeyCode getDownKey() {
        return downKey;
    }

    public KeyCode getLeftKey() {
        return leftKey;
    }

    public KeyCode getRightKey() {
        return rightKey;
    }

    public KeyCode getPowerKey() {
        return powerKey;
    }

    public void setPressedKey(KeyCode keyCode,boolean isPressed) {
        if ( keyCode == upKey ) {
            isUpKeyPressed = isPressed;
        } else if ( keyCode == downKey ) {
            isDownKeyPressed = isPressed;
        } else if ( keyCode == leftKey ) {
            isLeftKeyPressed = isPressed;
        } else if ( keyCode == rightKey ) {
            isRightKeyPressed = isPressed;
        } else if ( keyCode == powerKey ) {
            isPowerKeyPressed = isPressed;
        }
    }
    public boolean isKeyPressed(String keyName) {
        return switch (keyName) {
            case "up" -> isUpKeyPressed;
            case "down" -> isDownKeyPressed;
            case "left" -> isLeftKeyPressed;
            case "right" -> isRightKeyPressed;
            case "power" -> isPowerKeyPressed;
            default -> false;
        };
    }
}
