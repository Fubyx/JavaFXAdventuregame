package game.jfxadventuregame;

import javax.swing.*;
import java.io.Serializable;

public class GameObject implements Serializable, LoadHelp {
    transient protected ImageIcon icon;
    protected double x;
    protected double y;
    protected double radius; // or thickness if a wall


    @Override
    public void load() {
        //Method for changes to loading
    }
}
