package game.jfxadventuregame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static java.lang.Math.*;

public class Projectile extends GameObject {

    protected double movement_speed;
    transient protected Image image;
    protected double []imageValues;
    protected String source;
    protected double damage;
    protected double durationLeft;
    protected int piercing;

    protected ArrayList<Character> enemiesHit;
    protected String faction;

    protected Spell spell;
    protected double angle;
    Spell.type_t type;

    Projectile(double x, double y, double angle, double movement_speed, double damage, double durationLeft, int piercing, double radius, Spell.type_t type, double[]imageValues, String source, Spell spell, String faction) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.movement_speed = movement_speed;
        this.damage = damage;
        this.durationLeft = durationLeft;
        this.piercing = piercing;
        this.radius = radius;
        this.type = type;

        this.source = source;
        this.imageValues = imageValues;
        switch(source){
            case "old_sprite_sheet" -> this.image = new ImageIcon(Game.old_sprite_sheet.getSubimage((int )imageValues[0], (int) imageValues[1], (int) imageValues[2], (int) imageValues[3])).getImage().getScaledInstance(30, 30, Image.SCALE_REPLICATE).getScaledInstance((int) imageValues[4], (int) imageValues[5], Image.SCALE_FAST);
            case "lavapool.png" -> this.image = new ImageIcon("lavapool.png").getImage();
        }

        this.spell = spell;

        // change
        this.faction = faction;
        this.enemiesHit = new ArrayList<>();

    }

    public boolean move() {
        this.x = this.x + cos(angle) * movement_speed;
        this.y = this.y + sin(angle) * movement_speed;
        --durationLeft;

        // true = yes, the projectile should still exist
        if (this.piercing > 0) {
            if (this.faction.equals("hostile")) {
                if (this.enemiesHit.size() == 0) {
                    if (Game.collisionCheck(this, Game.player, false)) {
                        this.enemiesHit.add(Game.player);
                        double damageDealt = this.damage * 0.2 * (6 - (pow(6, Game.player.def / this.damage)));
                        if (damageDealt > 0) {
                            Game.player.hp -= damageDealt;
                        }
                        this.piercing--;
                        this.spell.summonSubProjectile(this); // faction = "hostile"
                    }
                }
            } else {
                for (int i = 0; i < Game.enemies.size(); ++i) {
                    boolean check = false;
                    for (int j = 0; j < this.enemiesHit.size(); j++) {
                        if (Game.enemies.get(i).equals(this.enemiesHit.get(j))) {
                            check = true;
                            break;
                        }
                    }
                    if (!check && Game.collisionCheck(this, Game.enemies.get(i), false)) {
                        enemiesHit.add(Game.enemies.get(i));
                        double damageDealt = this.damage * 0.2 * (6 - (pow(6, Game.enemies.get(i).def / this.damage)));
                        if (damageDealt > 0) {
                            Game.enemies.get(i).hp -= damageDealt;
                        }
                        this.piercing--;
                        this.spell.summonSubProjectile(this); // faction = "friendly"
                    }
                }
            }
        } else if (this.movement_speed != 0) {
            return false;
        }
        if (durationLeft < 1) {
            this.spell.summonSubProjectile(this);
            return false;
        }
        return true;
    }

    public void load(){
        switch(source){
            case "old_sprite_sheet" -> this.image = new ImageIcon(Game.old_sprite_sheet.getSubimage((int )imageValues[0], (int) imageValues[1], (int) imageValues[2], (int) imageValues[3])).getImage().getScaledInstance(30, 30, Image.SCALE_REPLICATE).getScaledInstance((int) imageValues[4], (int) imageValues[5], Image.SCALE_FAST);
            case "lavapool.png" -> this.image = new ImageIcon("lavapool.png").getImage();
        }
    }
}
