package com.game.catch_me_if_you_can_final.model.manipulator;

import com.game.catch_me_if_you_can_final.Main;

import com.game.catch_me_if_you_can_final.model.AnimationStyle;
import com.game.catch_me_if_you_can_final.model.NumberGenerator;
import com.game.catch_me_if_you_can_final.model.Player;
import com.game.catch_me_if_you_can_final.model.power.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.*;

public class ImageManipulator {
    private final double ICON_WIDTH_1366 = 35;
    private final double ICON_HEIGHT_1366 = 35;
    private final double ICON_WIDTH_1600 = 40;
    private final double ICON_HEIGHT_1600 = 40;
    private final double ICON_WIDTH_1920 = 45;
    private final double ICON_HEIGHT_1920 = 45;
    private final Map<String,String> iconMap;
    private double iconWidth;
    private double iconHeight;
    private final Set<String> initializeExcludeList;

    public ImageManipulator () {
        iconWidth = 40;
        iconHeight = 40;
        iconMap = new HashMap<>();
        iconMap.put("banana",convertIconPath("banana"));
        iconMap.put("bananaCover",convertIconPath("banana_cover"));
        iconMap.put("blueBird",convertIconPath("blue-bird"));
        iconMap.put("blackMonkey",convertIconPath("black_monkey"));
        iconMap.put("blueBunny",convertIconPath("blue_bunny"));
        iconMap.put("cat",convertIconPath("cat"));
        iconMap.put("dog",convertIconPath("dog"));
        iconMap.put("giant",convertIconPath("giant"));
        iconMap.put("happyDog",convertIconPath("happy_dog"));
        iconMap.put("koala",convertIconPath("koala"));
        iconMap.put("loveBird",convertIconPath("love_bird"));
        iconMap.put("orangeMonkey",convertIconPath("orange-monkey"));
        iconMap.put("mouse",convertIconPath("mouse"));
        iconMap.put("panda",convertIconPath("panda"));
        iconMap.put("pig",convertIconPath("pig"));
        iconMap.put("pinkBunny",convertIconPath("pink_bunny"));
        iconMap.put("owl",convertIconPath("owl"));
        iconMap.put("slow",convertIconPath("slow"));
        iconMap.put("speed",convertIconPath("speed"));
        iconMap.put("stone",convertIconPath("stone"));
        iconMap.put("superCat",convertIconPath("super_cat"));
        iconMap.put("superTurtle",convertIconPath("super_turtle"));
        iconMap.put("turtle",convertIconPath("turtle"));
        iconMap.put("zPanda",convertIconPath("z_panda"));
        iconMap.put("powerZone",convertIconPath("power_zone"));
        iconMap.put("one",convertIconPath("one"));
        iconMap.put("two",convertIconPath("two"));
        iconMap.put("three",convertIconPath("three"));
        iconMap.put("four",convertIconPath("four"));
        iconMap.put("dust",convertIconPath("dust"));
        iconMap.put("help",convertIconPath("help"));


        initializeExcludeList = new HashSet<>();
        initializeExcludeList.addAll(List.of("bananaCover","stone","speed","slow","giant","banana","powerZone","one","two","three","four","dust","help"));

    }

    private String convertIconPath(String iconPath) {
        return "/images/game_icons/"+iconPath+".png";
    }

    public Image getImage(String iconName) {
        return new Image(Objects.requireNonNull(Main.class.getResource(iconMap.get(iconName))).toString());
    }

    public String getImagePath(String iconName) {
        return getExtractedIconPath(new ImageView(getImage(iconName)));
    }

    public String getExtractedIconPath(ImageView icon) {
        String imagePath = icon.getImage().getUrl();
        String[] pathSplitter = imagePath.split("/");
        return pathSplitter[pathSplitter.length-1];
    }

    public void initializeIcons(Pane node) {
        node.getChildren().clear();
        if ( Main.MAIN_STAGE.getScene().getWidth() <= 1366 ) {
            iconWidth = ICON_WIDTH_1366;
            iconHeight = ICON_HEIGHT_1366;
        } else if ( Main.MAIN_STAGE.getScene().getWidth() <= 1600 ) {
            iconWidth = ICON_WIDTH_1600;
            iconHeight = ICON_HEIGHT_1600;
        } else if ( Main.MAIN_STAGE.getScene().getWidth() <= 1920 ) {
            iconWidth = ICON_WIDTH_1920;
            iconHeight = ICON_HEIGHT_1920;
        }

        ImageView icon = null;
        for ( String key : iconMap.keySet() ) {
            if ( !initializeExcludeList.contains(key) ) {
                icon = new ImageView(getImage(key));
                AnimationStyle.playScaleEffect(icon, NumberGenerator.generate(300,500)
                        , -1,true,1,1,1.2,1.2);
                icon.setFitWidth(iconWidth);
                icon.setFitHeight(iconHeight);
                node.getChildren().add(icon);
            }
        }
    }

    public void reInitializeIconSize(HBox hBox,double iconWidth,double iconHeight) {
        for ( Node child : hBox.getChildren() ) {
            ImageView imageView = (ImageView) child;
            imageView.setFitWidth(iconWidth);
            imageView.setFitHeight(iconHeight);
        }
    }

    public Image getImageByPlayerPower(Player player) {
        if ( player.getPower() instanceof BlackMonkeyPower ) {
            return getImage("blackMonkey");
        } else if ( player.getPower() instanceof BlueBirdPower ) {
            return getImage("blueBird");
        } else if ( player.getPower() instanceof BlueBunnyPower) {
            return getImage("blueBunny");
        } else if ( player.getPower() instanceof CatPower) {
            return getImage("cat");
        } else if ( player.getPower() instanceof DogPower) {
            return getImage("dog");
        } else if ( player.getPower() instanceof HappyDogPower ) {
            return getImage("happyDog");
        } else if ( player.getPower() instanceof KoalaPower ) {
            return getImage("koala");
        } else if ( player.getPower() instanceof LoveBirdPower ) {
            return getImage("loveBird");
        } else if ( player.getPower() instanceof MousePower ) {
            return getImage("mouse");
        } else if ( player.getPower() instanceof OrangeMonkeyPower ) {
            return getImage("orangeMonkey");
        } else if ( player.getPower() instanceof OwlPower ) {
            return getImage("owl");
        } else if ( player.getPower() instanceof PigPower ) {
            return getImage("pig");
        } else if ( player.getPower() instanceof PinkBunnyPower ) {
            return getImage("pinkBunny");
        } else if ( player.getPower() instanceof SuperCatPower ) {
            return getImage("superCat");
        } else if ( player.getPower() instanceof SuperTurtlePower ) {
            return getImage("superTurtle");
        } else if ( player.getPower() instanceof TurtlePower ) {
            return getImage("turtle");
        } else if ( player.getPower() instanceof ZPandaPower ) {
            return getImage("zPanda");
        } else {
            return getImage("dust");
        }
    }
}
