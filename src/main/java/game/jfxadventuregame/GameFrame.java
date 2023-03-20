package game.jfxadventuregame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Math.*;


public class GameFrame extends JFrame implements KeyListener{

    private GamePanel gamePanel;

    GameFrame(String windowName) {
        super(windowName);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1280, 720);

        this.gamePanel = new GamePanel();

        this.add(gamePanel);

        this.addKeyListener(this);

        // "start" the window after adding all components
        this.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        gamePanel.keyPressed(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gamePanel.keyReleased(e.getKeyCode());
    }
}