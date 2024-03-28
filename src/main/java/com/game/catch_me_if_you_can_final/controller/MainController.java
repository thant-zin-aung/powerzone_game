package com.game.catch_me_if_you_can_final.controller;

import com.game.catch_me_if_you_can_final.Main;
import com.game.catch_me_if_you_can_final.model.*;
import com.game.catch_me_if_you_can_final.model.manipulator.ImageManipulator;
import com.game.catch_me_if_you_can_final.model.manipulator.KeyCodeManipulator;
import com.game.catch_me_if_you_can_final.model.manipulator.TimeManipulator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Map;

public class MainController {

    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane homeView,keySetView,gameView;
    @FXML
    private HBox iconSelectionBar;
    @FXML
    private VBox sideBar,formWrapper;
    @FXML
    private AnchorPane introView,formView;
    @FXML
    private HBox twoPlayerBox,threePlayerBox,fourPlayerBox,twoVsTwoBox;
    @FXML
    private Text fillInfoText;
    @FXML
    private HBox formInfoBox1,formInfoBox2,formInfoBox3,formInfoBox4;
    @FXML
    private TextField player1Name,player2Name,player3Name,player4Name;
    @FXML
    private RadioButton player1HunterRadio,player2HunterRadio,player3HunterRadio,player4HunterRadio;
    @FXML
    private ImageView player1Icon,player2Icon,player3Icon,player4Icon,firstTotalPlayer,secondTotalPlayer;
    @FXML
    private HBox vsLabelWrapper;
    @FXML
    private VBox keySetWrapperContainer;
    @FXML
    private Button playerKeySetFront1,playerKeySetFront2,playerKeySetFront3,playerKeySetFront4,
                    playerKeySetBack1,playerKeySetBack2,playerKeySetBack3,playerKeySetBack4;
    @FXML
    private StackPane keySet1Wrapper,keySet2Wrapper,keySet3Wrapper,keySet4Wrapper;
    @FXML
    private Label keySetLabel1,keySetLabel2,keySetLabel3,keySetLabel4;
    @FXML
    private Label gameStartingLabel;
    @FXML
    private HBox gameStartingLabelWrapper;

    private final ImageManipulator imageManipulator = new ImageManipulator();
    private AudioPlayer audioPlayer;
    private ObservableList<HBox> playerBoxes = null;
    private ObservableList<HBox> formInfoBoxes = null;
    private int totalPlayer;
    private HBox selectedPlayerBox;
    private ObservableList<Player> players = null;
    private ObservableList<Button> keySetBackButtons = null;
    private ObservableList<StackPane> keySetWrappers = null;
    private ObservableList<Label> keySetLabels = null;
    private KeyCodeManipulator keyCodeManipulator;
    private GameZone gameZone;

    private ImageView playerIcon = null;


    private final int SCREEN_MAXIMIZE_ICON_WIDTH = 40;
    private final int SCREEN_MAXIMIZE_ICON_HEIGHT = 40;

    private final int SCREEN_NOT_MAXIMIZE_ICON_WIDTH = 35;
    private final int SCREEN_NOT_MAXIMIZE_ICON_HEIGHT = 35;

    private final int PLAYER_NAME_POSITION = 0;
    private final int PLAYER_ICON_POSITION = 2;
    private final int PLAYER_HUNTER_POSITION = 4;

    private final int SWITCH_DURATION = 700;

    public void initialize() {
        playerBoxes = FXCollections.observableArrayList(List.of(twoPlayerBox,threePlayerBox
                ,fourPlayerBox,twoVsTwoBox));
        formInfoBoxes = FXCollections.observableArrayList(List.of(formInfoBox1,formInfoBox2
                ,formInfoBox3,formInfoBox4));
        players = FXCollections.observableArrayList();
        keySetBackButtons = FXCollections.observableArrayList(List.of(playerKeySetBack1,playerKeySetBack2
                ,playerKeySetBack3,playerKeySetBack4));
        keySetWrappers = FXCollections.observableArrayList(List.of(keySet1Wrapper,keySet2Wrapper
                ,keySet3Wrapper,keySet4Wrapper));
        keySetLabels = FXCollections.observableArrayList(List.of(keySetLabel1,keySetLabel2
                ,keySetLabel3,keySetLabel4));
        keyCodeManipulator = new KeyCodeManipulator();
        gameZone = new GameZone();
        audioPlayer = new AudioPlayer();
        audioPlayer.play("startGame",1,1);
        initializeMaximizeButtonListener();

          // Just for testing
//        hideNodes(true,homeView);
//        Player p1 = new Player();
//        p1.setName("blacksky");
//        p1.setIcon(new ImageView(imageManipulator.getImage("zPanda")));
//        p1.setHunter(false);
//        p1.setKeySet(keyCodeManipulator.getKeyMap1(),1);
//
//        Player p2 = new Player();
//        p2.setName("perry");
//        p2.setIcon(new ImageView(imageManipulator.getImage("superCat")));
//        p2.setHunter(false);
//        p2.setKeySet(keyCodeManipulator.getKeyMap2(),2);
//
//        Player p3 = new Player();
//        p3.setName("kts");
//        p3.setIcon(new ImageView(imageManipulator.getImage("cat")));
//        p3.setHunter(true);
//        p3.setKeySet(keyCodeManipulator.getKeyMap3(),3);
//
//        Player p4 = new Player();
//        p4.setName("su mon kyaw");
//        p4.setIcon(new ImageView(imageManipulator.getImage("loveBird")));
//        p4.setHunter(false);
//        p4.setKeySet(keyCodeManipulator.getKeyMap4(),4);
//
//        players.addAll(p1,p2,p3,p4);
//        players.forEach( p -> {
//            players.forEach(p::addPlayer);
//        });
//        gameZone.setGameRoot(gameView);
//        gameZone.setPlayers(players);
//        gameZone.startGame();
        // Just for testing
    }

    private void initializeMaximizeButtonListener() {
        Main.MAIN_STAGE.maximizedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if ( !t1 ) {
                    imageManipulator.reInitializeIconSize(iconSelectionBar,SCREEN_NOT_MAXIMIZE_ICON_WIDTH,SCREEN_NOT_MAXIMIZE_ICON_HEIGHT);
                    iconSelectionBar.setSpacing(5);
                    sideBar.setSpacing(10);
                } else {
                    imageManipulator.reInitializeIconSize(iconSelectionBar,SCREEN_MAXIMIZE_ICON_WIDTH,SCREEN_MAXIMIZE_ICON_HEIGHT);
                    iconSelectionBar.setSpacing(10);
                    sideBar.setSpacing(20);
                }
            }
        });

        Main.MAIN_STAGE.fullScreenProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                if ( !t1 ) {
                    imageManipulator.reInitializeIconSize(iconSelectionBar,SCREEN_NOT_MAXIMIZE_ICON_WIDTH,SCREEN_NOT_MAXIMIZE_ICON_HEIGHT);
                    iconSelectionBar.setSpacing(5);
                    sideBar.setSpacing(10);
                    formWrapper.setSpacing(20);
                } else {
                    sideBar.setSpacing(30);
                    formWrapper.setSpacing(30);
                }
            }
        });
    }

    private void disableNodes(boolean setDisable,Node ... nodes) {
        for ( Node node : nodes ) {
            node.setDisable(setDisable);
        }
    }
    private void hideNodes(boolean setHide,Node ... nodes) {
        for ( Node node : nodes ) {
            node.setVisible(!setHide);
        }
    }

    private void setVsLabel(String firstImageName , String secondImageName) {
        firstTotalPlayer.setImage(imageManipulator.getImage(firstImageName));
        secondTotalPlayer.setImage(imageManipulator.getImage(secondImageName));
    }

    @FXML
    public void hoverOnPlayerBoxes(MouseEvent event) {
        HBox currentBox = (HBox) event.getSource();
        audioPlayer.play("selectionSound",1,1);
        AnimationStyle.playTranslateEffect(currentBox,200,1,false,currentBox.getTranslateX(),70,0,0);
    }
    @FXML
    public void hoverExitOnPlayerBoxes(MouseEvent event) {
        HBox currentBox = (HBox) event.getSource();
        AnimationStyle.playTranslateEffect(currentBox,200,1,false,70,0,0,0);
    }
    @FXML
    public void clickOnPlayerBoxes(MouseEvent event) {
        imageManipulator.initializeIcons(iconSelectionBar);
        vsLabelWrapper.setVisible(true);
        HBox currentBox = (HBox) event.getSource();
        selectedPlayerBox = currentBox;
        if ( currentBox == twoPlayerBox ) {
            disableNodes(true,formInfoBox3,formInfoBox4);
            setVsLabel("one","one");
            totalPlayer = 2;
        } else if ( currentBox == threePlayerBox ) {
            disableNodes(false,formInfoBox3);
            disableNodes(true,formInfoBox4);
            setVsLabel("one","two");
            totalPlayer = 3;
        } else if ( currentBox == fourPlayerBox ) {
            disableNodes(false,formInfoBox3,formInfoBox4);
            setVsLabel("one","three");
            totalPlayer = 4;
        } else {
            disableNodes(false,formInfoBox3,formInfoBox4);
            setVsLabel("two","two");
            totalPlayer = 4;
        }

        for ( HBox box : playerBoxes ) {
            if ( box == currentBox ) {
                if ( !currentBox.getStyleClass().contains("selected-player-selection-box") ) {
                    currentBox.getStyleClass().add("selected-player-selection-box");
                }
            } else {
                box.getStyleClass().removeIf( e -> e.equalsIgnoreCase("selected-player-selection-box"));
            }
        }

        // Switch between intro view and form info view
        if ( !formView.isVisible() ) {
            AnimationStyle.playTranslateFadeEffect(introView,SWITCH_DURATION,SWITCH_DURATION,1,false,0,0,0,-2000,1,0);
            formView.setVisible(true);
            AnimationStyle.playTranslateFadeEffect(formView,SWITCH_DURATION,SWITCH_DURATION,1,false,0,0,1000,0,0,1);
        }

    }
    @FXML
    public void clickOnIconButtons(MouseEvent event) {
        if ( iconSelectionBar.getStyleClass().contains("icon-selection-style") ) return;
        Button currentButton = (Button) event.getSource();
        HBox currentFormInfoBox = (HBox) currentButton.getParent();
        iconSelectionBar.getStyleClass().add("icon-selection-style");
        EventHandler<MouseEvent> iconClickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                setPlayerIcon((ImageView) event.getSource());
                if ( !isIconReserved(playerIcon) ) {
                    AnimationStyle.playScaleEffect(currentFormInfoBox.getChildren().get(PLAYER_ICON_POSITION),500,1,false,0,0,1,1);
                    ((ImageView)currentFormInfoBox.getChildren().get(PLAYER_ICON_POSITION)).setImage(playerIcon.getImage());
                    iconSelectionBar.getStyleClass().removeIf(e -> e.equalsIgnoreCase("icon-selection-style"));
                    for ( Node node : iconSelectionBar.getChildren() ) {
                        ImageView imageView = (ImageView) node;
                        imageView.removeEventHandler(MouseEvent.MOUSE_CLICKED,this);
                    }
                }
                setPlayerIcon(null);
            }
        };

        for ( Node node : iconSelectionBar.getChildren() ) {
            ImageView imageView = (ImageView) node;
            imageView.addEventHandler(MouseEvent.MOUSE_CLICKED,iconClickHandler);
        }
    }

    private boolean isIconReserved(ImageView selectedIcon) {
        boolean isReserved = false;
        for ( HBox formInfoBox : formInfoBoxes ) {
            ImageView currentImage = (ImageView) formInfoBox.getChildren().get(PLAYER_ICON_POSITION);
            if ( currentImage.getImage() != null && imageManipulator.getExtractedIconPath(currentImage).equalsIgnoreCase(
                    imageManipulator.getExtractedIconPath(selectedIcon)
            ) ) {
                isReserved = true;
                break;
            }
        }
        return isReserved;
    }
    private void setPlayerIcon(ImageView icon) {
        playerIcon = icon;
    }

    @FXML
    public boolean checkIfContinueButtonReady() {
        boolean isReady = true;
        if ( selectedPlayerBox == twoPlayerBox ) {
            if ( !( isAllInfoFilledUp(player1Name,player1Icon) && isAllInfoFilledUp(player2Name,player2Icon) ) ) {
                isReady = false;
            }
        } else if ( selectedPlayerBox == threePlayerBox ) {
            if ( !( isAllInfoFilledUp(player1Name,player1Icon) && isAllInfoFilledUp(player2Name,player2Icon) &&
                    isAllInfoFilledUp(player3Name,player3Icon) ) ) {
                isReady = false;
            }
        } else {
            if ( !( isAllInfoFilledUp(player1Name,player1Icon) && isAllInfoFilledUp(player2Name,player2Icon) &&
                    isAllInfoFilledUp(player3Name,player3Icon) && isAllInfoFilledUp(player3Name,player3Icon) ) ) {
                isReady = false;
            }
        }

        if ( !player1HunterRadio.isSelected() && !player2HunterRadio.isSelected() && !player3HunterRadio.isSelected() &&
                !player4HunterRadio.isSelected() ) isReady = false;

        if ( !isReady ) {
            AnimationStyle.playTranslateSequentialEffect(fillInfoText,70,2,true,0,100,0,0,
                    70,2,true,0,-100,0,0);
        }
        return isReady;
    }

    private boolean isAllInfoFilledUp(TextField playerName , ImageView playerIcon ) {
        return !playerName.getText().isEmpty() && playerIcon.getImage() != null;
    }

    @FXML
    public void clickOnContinueButton() {
        if ( !checkIfContinueButtonReady() ) return;

        for ( int id = 0 ; id < totalPlayer ; id++ ) {
            String playerName = ((TextField)formInfoBoxes.get(id).getChildren().get(PLAYER_NAME_POSITION)).getText();
            ImageView playerIcon = ((ImageView)formInfoBoxes.get(id).getChildren().get(PLAYER_ICON_POSITION));
            boolean isHunter = ((RadioButton)formInfoBoxes.get(id).getChildren().get(PLAYER_HUNTER_POSITION)).isSelected();

            Player player = new Player();
            player.setName(playerName);
            player.setIcon(playerIcon);
            player.setHunter(isHunter);

            players.add(player);
            players.forEach(player::addPlayer);
        }

        if ( players.size() == 2 ) {
            playerKeySetFront1.setText(players.get(0).getName());
            playerKeySetFront2.setText(players.get(1).getName());
            hideNodes(true,playerKeySetFront3,playerKeySetFront4,playerKeySetBack3,playerKeySetBack4);
        }
        else if ( players.size() == 3 ) {
            playerKeySetFront1.setText(players.get(0).getName());
            playerKeySetFront2.setText(players.get(1).getName());
            playerKeySetFront3.setText(players.get(2).getName());
            hideNodes( true,playerKeySetFront4,playerKeySetBack4);
        } else {
            playerKeySetFront1.setText(players.get(0).getName());
            playerKeySetFront2.setText(players.get(1).getName());
            playerKeySetFront3.setText(players.get(2).getName());
            playerKeySetFront4.setText(players.get(3).getName());
        }

        AnimationStyle.playTranslateFadeEffect(homeView,SWITCH_DURATION,SWITCH_DURATION,1,false,0,0,0,-2000,1,0);
        keySetView.setVisible(true);
        AnimationStyle.playTranslateFadeEffect(keySetView,SWITCH_DURATION,SWITCH_DURATION,1,false,0,0,1000,0,0,1);
    }

    @FXML
    public void clickOnKeySetButton(MouseEvent mouseEvent) {
        if ( keySetWrapperContainer.getStyleClass().contains("key-set-wrapper-container-selection")) return;
        keySetWrapperContainer.getStyleClass().add("key-set-wrapper-container-selection");

        // Resetting selected key set
        Button currentBackButton = (Button) mouseEvent.getSource();
        int playerId = 0;
        for ( int id = 0 ; id < keySetBackButtons.size() ; id++ ) {
            if ( keySetBackButtons.get(id) == currentBackButton ) {
                playerId = id;
                break;
            }
        }
        if ( players.get(playerId).getKeySet() != null ) {
            setDisableAndChangeOpacity(false , 1 , keySetWrappers.get(players.get(playerId).getKeySetId()));
            keySetLabels.get(players.get(playerId).getKeySetId()).setText("");
            hideNodes(true,keySetLabels.get(players.get(playerId).getKeySetId()));
        }
        // Resetting selected key set

        EventHandler<MouseEvent> keySetClickHandler = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                StackPane currentWrapper = (StackPane) event.getSource();
                Button currentButton = (Button) mouseEvent.getSource();

                if ( currentButton == playerKeySetBack1 ) {
                    players.get(0).setKeySet(
                            getKeyMap(currentWrapper,playerKeySetFront1,playerKeySetBack1),
                            getKeySetWrapperIndex(currentWrapper)
                    );
                    keySetLabels.get(getKeySetWrapperIndex(currentWrapper)).setText(players.get(0).getName());
                } else if ( currentButton == playerKeySetBack2 ) {
                    players.get(1).setKeySet(
                            getKeyMap(currentWrapper,playerKeySetFront2,playerKeySetBack2),
                            getKeySetWrapperIndex(currentWrapper)
                    );
                    keySetLabels.get(getKeySetWrapperIndex(currentWrapper)).setText(players.get(1).getName());
                } else if ( currentButton == playerKeySetBack3 ) {
                    players.get(2).setKeySet(
                            getKeyMap(currentWrapper,playerKeySetFront3,playerKeySetBack3),
                            getKeySetWrapperIndex(currentWrapper)
                    );
                    keySetLabels.get(getKeySetWrapperIndex(currentWrapper)).setText(players.get(2).getName());
                } else if ( currentButton == playerKeySetBack4 ) {
                    players.get(3).setKeySet(
                            getKeyMap(currentWrapper,playerKeySetFront4,playerKeySetBack4),
                            getKeySetWrapperIndex(currentWrapper)
                    );
                    keySetLabels.get(getKeySetWrapperIndex(currentWrapper)).setText(players.get(3).getName());
                }
                hideNodes(false,keySetLabels.get(getKeySetWrapperIndex(currentWrapper)));

                for ( StackPane wrapper : keySetWrappers ) {
                    wrapper.removeEventHandler(MouseEvent.MOUSE_CLICKED,this);
                }

                keySetWrapperContainer.getStyleClass()
                        .removeIf(e -> e.equalsIgnoreCase("key-set-wrapper-container-selection"));
            }
        };
        for ( StackPane wrapper : keySetWrappers ) {
            wrapper.addEventHandler(MouseEvent.MOUSE_CLICKED,keySetClickHandler);
        }
    }

    private int getKeySetWrapperIndex(StackPane currentWrapper,Button frontButton,Button backButton) {
        int keySetId = 0;
        for ( int id=0 ; id < keySetWrappers.size() ; id++ ) {
            if ( currentWrapper == keySetWrappers.get(id) ) {
                keySetId = id;
                break;
            }
        }
        frontButton.setTextFill(Color.BLACK);

        if ( keySetId == 0 ) {
            backButton.setText("KeySet-1");
        } else if ( keySetId == 1 ) {
            backButton.setText("KeySet-2");
        } else if ( keySetId == 2 ) {
            backButton.setText("KeySet-3");
        } else if ( keySetId == 3 ) {
            backButton.setText("KeySet-4");
        }
        return keySetId;
    }
    private int getKeySetWrapperIndex(StackPane currentWrapper) {
        int keySetId = 0;
        for ( int id=0 ; id < keySetWrappers.size() ; id++ ) {
            if ( currentWrapper == keySetWrappers.get(id) ) {
                keySetId = id;
                break;
            }
        }
        return keySetId;
    }
    private Map<String, KeyCode> getKeyMap(StackPane currentWrapper,Button frontButton,Button backButton) {
        Map<String, KeyCode> keyMap = null;
        if ( getKeySetWrapperIndex(currentWrapper,frontButton,backButton) == 0 ) {
            keyMap = keyCodeManipulator.getKeyMap1();
            setDisableAndChangeOpacity(true,0.6,keySetWrappers.get(0));
        } else if ( getKeySetWrapperIndex(currentWrapper,frontButton,backButton) == 1 ) {
            keyMap = keyCodeManipulator.getKeyMap2();
            setDisableAndChangeOpacity(true,0.6,keySetWrappers.get(1));
        } else if ( getKeySetWrapperIndex(currentWrapper,frontButton,backButton) == 2 ) {
            keyMap = keyCodeManipulator.getKeyMap3();
            setDisableAndChangeOpacity(true,0.6,keySetWrappers.get(2));
        } else if ( getKeySetWrapperIndex(currentWrapper,frontButton,backButton) == 3 ) {
            keyMap = keyCodeManipulator.getKeyMap4();
            setDisableAndChangeOpacity(true,0.6,keySetWrappers.get(3));
        }
        return keyMap;
    }
    private void setDisableAndChangeOpacity(boolean setDisable , double opacityLevel, Node ... nodes) {
        for ( Node node: nodes) {
            disableNodes(setDisable,node);
            node.setOpacity(opacityLevel);
        }
    }

    @FXML
    public void clickOnStartGameButton() {
        boolean isKeyMapSet = true;
        for (Player player : players) {
            if (player.getKeySet() == null) {
                isKeyMapSet = false;
                break;
            }
        }
        if ( !isKeyMapSet ) return;

        // Starting countdown and game is about to start
        new Thread(() -> {
            hideNodes(false,gameStartingLabelWrapper);
            TimeManipulator.countDown(gameStartingLabel,5);
            AnimationStyle.playFadeEffect(keySetView,500,1,false,1,0);
//            AnimationStyle.playTranslateEffect(keySetView,SWITCH_DURATION,1,false,0,0,0,-2500);
//            players.forEach(p -> p.setSpeed(5));
            gameZone.setGameRoot(gameView);
            gameZone.setPlayers(players);
            gameZone.startGame();
        }).start();
    }
}