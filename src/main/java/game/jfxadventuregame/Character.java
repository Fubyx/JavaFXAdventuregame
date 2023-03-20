package game.jfxadventuregame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import static java.lang.Math.pow;

public class Character extends GameObject implements ActionListener{
    public String faction;

    transient protected int stage;
    protected int intelligence,strength,dexterity,wisdom,endurance;
    transient protected double def;
    transient protected int stamina;
    transient protected int maxStamina;

    transient protected double baseDamage, spell_effectiveness, mana_recovery_speed/*in Enemy, cooldown between casts*/, melee_dmg_multiplier, movement_speed, combo_dmg_multiplier, crit_dmg_multiplier, maxHP;

    protected double hp;
    transient protected double critrate; //Dexterity
    protected ArrayList<Spell> spells;
    transient protected Timer comboTimer;
    transient protected int combo;


    public Character(String faction) {
        this.icon = new ImageIcon("img_1.png");
        this.faction = faction;

        this.stage = 1;

        this.intelligence = 1;
        this.endurance = 1;
        this.strength = 1;
        this.dexterity = 1;
        this.wisdom = 1;
        this.levelUp(0);

        this.spells = new ArrayList<>();

        this.comboTimer = new Timer(Game.COMBO_TIMER_BASE_VALUE, this);
        this.combo = 0;

    }

    public void levelUp(int amount) {
        this.stage += amount;
        this.increaseStat("Intelligence", amount);
        this.increaseStat("Strength", amount);
        this.increaseStat("Endurance", amount);
        this.increaseStat("Dexterity", amount);

    }

    public void increaseStat(String stat, int amount) {
        switch (stat) {
            case "Intelligence" -> {
                intelligence += amount;
                spell_effectiveness = pow(1.01, intelligence);
                if (intelligence > 20) {
                    mana_recovery_speed = pow(1.005, intelligence) - 1;
                }
                else {
                    mana_recovery_speed = 0.1;
                }
            }
            case "Strength" -> {
                strength += amount;
                baseDamage = 5 + strength;
                maxHP = 100 + 2.5 * strength + pow(1.007, endurance)*endurance;
                hp += 2.5 * amount;
                if (hp > maxHP) {
                    hp = maxHP;
                }
                def = strength*0.2 + endurance*0.5;
                melee_dmg_multiplier = pow(1.01, strength);
                crit_dmg_multiplier = pow(1.0025, strength);
            }
            case "Endurance" -> {
                endurance += amount;
                maxHP = 100 + 2.5 * strength + pow(1.01, endurance);
                hp += pow(1.01, endurance);
                if (hp > maxHP) {
                    hp = maxHP;
                }
                def = strength*0.2 + endurance*0.5;
                stamina += 3 * amount;
                maxStamina = 100 + 3 * endurance;

            }
            case "Dexterity" -> {
                dexterity += amount;
                combo_dmg_multiplier = pow(1.00004, dexterity);
                movement_speed = 2 + pow(1.005, dexterity);
                crit_dmg_multiplier = 2 + dexterity * 0.05;
                critrate = 1000.0*pow(0.98, dexterity);
            }
            default -> wisdom += amount;
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.comboTimer) {
            combo = 0;
        }
    }

    public void addToCombo() {
        if (combo == 0) {
            this.comboTimer.start();
        } else {
            this.comboTimer.setDelay(Game.COMBO_TIMER_BASE_VALUE / (2 * combo));
            this.comboTimer.restart();
        }
        combo += 1;
    }

}