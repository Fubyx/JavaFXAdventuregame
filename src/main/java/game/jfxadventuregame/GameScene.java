package game.jfxadventuregame;

import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.input.KeyEvent;

import javax.swing.*;




public class GameScene{

    private GamePanel gamePanel;
    SwingNode gameNode;
    Scene scene;
    
    public GameScene(){
        this.gamePanel = new GamePanel();
        this.gameNode = new SwingNode();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                gameNode.setContent(gamePanel);
            }
        });
        gameNode.setLayoutX(0);
        gameNode.setLayoutY(0);


        Pane pane = new Pane();
        pane.getChildren().add(gameNode);
        
        scene = new Scene(pane, 1280, 720);
    }

    public void keyPressed(KeyEvent e) {
        gamePanel.keyPressed(e.getCode().getCode());
    }

    public void keyReleased(KeyEvent e) {
        gamePanel.keyReleased(e.getCode().getCode());
    }
}
