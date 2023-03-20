package game.jfxadventuregame;

import java.io.Serializable;

public class Armour extends Item implements Serializable {
    protected double hpBuff;
    protected double defenceBuff;
    Armour(int []imageValues, String source, String name, String description, double hpBuff, double defenceBuff, Spell attack) {
        super(imageValues, source, name, 1, description, attack, false);
        this.hpBuff = hpBuff;
        this.defenceBuff = defenceBuff;
    }
}
