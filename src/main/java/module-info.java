module com.game.catch_me_if_you_can_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.game.catch_me_if_you_can_final to javafx.fxml;
    exports com.game.catch_me_if_you_can_final;
    exports com.game.catch_me_if_you_can_final.controller;
    opens com.game.catch_me_if_you_can_final.controller to javafx.fxml;
}