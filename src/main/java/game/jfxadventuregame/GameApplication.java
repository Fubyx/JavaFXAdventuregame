package game.jfxadventuregame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameApplication extends Application{
    GameScene gameScene;
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("AdventureGame");

        stage.setOnCloseRequest(new EventHandler<javafx.stage.WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });

        gameScene = new GameScene();

        stage.setScene(gameScene.scene);

        gameScene.scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                gameScene.keyPressed(keyEvent);
            }
        });

        gameScene.scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                gameScene.keyReleased(keyEvent);
            }
        });

        stage.show();

        stage.requestFocus();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                gameScene.scene.getFocusOwner().requestFocus();
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }

}

