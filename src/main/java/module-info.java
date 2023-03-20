module game.jfxadventuregame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens game.jfxadventuregame to javafx.fxml;
    exports game.jfxadventuregame;
}