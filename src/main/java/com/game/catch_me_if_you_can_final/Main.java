package com.game.catch_me_if_you_can_final;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    public static Stage MAIN_STAGE = null;
    @Override
    public void start(Stage stage) throws IOException {
        MAIN_STAGE = stage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(Main.class.getResource("/view/main-view.fxml")));
        stage.setTitle("Power Zone");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setScene(new Scene(root));
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.setOnCloseRequest(e -> {
            e.consume();
            System.exit(0);
        });
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}