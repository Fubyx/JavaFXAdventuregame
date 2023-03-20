package game.jfxadventuregame;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Random;

import static java.lang.Math.*;

public class Spell implements Serializable {
    protected ImageIcon image;

    protected int []imageValues;
    protected String source;
    protected String name;
    protected double damage_health, mana_cost, duration, movement_speed, radius;
    protected Spell subSpell; // spell that spawns after the projectile of this spell gets removed
    protected int piercing;

    protected enum type_t {
        projectile, physical, aoe, obstacle, buff
    }

    protected type_t type;

    public Spell(int []imageValues, String source, String name, double damage_health, double mana_cost, double duration, double movement_speed, double radius, int piercing, type_t type, Spell subSpell) {
        this.source = source;
        this.imageValues = imageValues;
        switch(source){
            case "old_sprite_sheet" -> this.image = new ImageIcon(Game.old_sprite_sheet.getSubimage(imageValues[0], imageValues[1], imageValues[2], imageValues[3]).getScaledInstance(30, 30, Image.SCALE_REPLICATE));
            case "lavapool.png" -> this.image = new ImageIcon("lavapool.png");
        }
        this.image = new ImageIcon(image.getImage().getScaledInstance(100, 100, Image.SCALE_REPLICATE));
        this.name = name;
        this.damage_health = damage_health;
        this.mana_cost = mana_cost;
        this.subSpell = subSpell;
        this.duration = duration;
        this.movement_speed = movement_speed;
        this.radius = radius;
        this.piercing = piercing;
        this.type = type;
    }

    public boolean summonProjectile(GameObject o, double delta_X, double delta_Y, boolean fromItem) {
        double damage = 1;
        double effectiveness = 1;
        String faction = "";
        if (o instanceof Character) {
            if (o instanceof Player) {
                if (type == type_t.physical) {
                    if (this.mana_cost <= Game.player.stamina) {
                        Game.player.stamina -= this.mana_cost;
                        damage *= Game.player.melee_dmg_multiplier;
                    } else {
                        return false;
                    }
                } else if (Game.player.mana + Game.player.stamina * 0.1 - this.mana_cost >= 0) {
                    damage *= Game.player.spell_effectiveness;
                    if (Game.player.mana - mana_cost < 0) {
                        double cost = mana_cost - Game.player.mana;
                        Game.player.mana = 0;
                        Game.player.stamina -= cost * 10;
                    } else {
                        Game.player.mana -= mana_cost;
                    }
                } else {
                    return false;
                }
                if (fromItem) {
                    damage *= Game.player.item_stat_multiplier;
                }
            } else { // if instanceof Enemy
                if (this.type == type_t.physical) {
                    damage *= ((Character) o).melee_dmg_multiplier;
                    ((Character) o).stamina = (int) this.mana_cost * 10;
                } else {
                    damage *= ((Character) o).spell_effectiveness;
                    ((Character) o).mana_recovery_speed = this.mana_cost * 10;
                }
            }
            damage *= ((new Random().nextInt((int) ((Character) o).critrate) == 0) ? ((Character) o).crit_dmg_multiplier : 1);
            effectiveness *= pow(((Character) o).spell_effectiveness, 0.3);
            faction = ((Character) o).faction;
        } else if (o instanceof Projectile) {
            faction = ((Projectile)o).faction;
        }
        double angle;
        if (delta_X == 0) {
            if (delta_Y > 0) {
                angle = 1.5 * PI;
            } else {
                angle = 0.5 * PI;
            }
        } else {
            angle = Math.atan(delta_Y / delta_X) + ((delta_X > 0) ? PI : 0);
        }
        double x = o.x + cos(angle) * (this.radius - o.radius);
        double y = o.y + sin(angle) * (this.radius - o.radius);

        //Projectile proj = new Projectile(x, y, angle, this.movement_speed * effectiveness, this.damage_health * damage, this.duration * effectiveness, this.piercing, this.radius, this.type, this.image.getImage().getScaledInstance((int) radius * 2, (int) radius * 2, Image.SCALE_FAST), this, faction);
        Projectile proj = new Projectile(x, y, angle, this.movement_speed * effectiveness, this.damage_health * damage, this.duration * effectiveness, this.piercing, this.radius, this.type, new double []{this.imageValues[0],this.imageValues[1],this.imageValues[2],this.imageValues[3],radius * 2, radius * 2}, this.source,  this, faction);
        Game.projectiles.add(Game.projectiles.size(), proj);
        return true;
    }

    public void summonSubProjectile(Projectile p) {
        if (subSpell != null) {
            this.subSpell.summonProjectile(p, -cos(p.angle), -sin(p.angle), false);
        }
        //Projectile proj = new Projectile(x, y, angle, this.subSpell.movement_speed, this.subSpell.damage_health, this.subSpell.duration, this.subSpell.piercing, this.subSpell.radius, type, this.subSpell.image.getImage().getScaledInstance((int) this.subSpell.radius * 2, (int) this.subSpell.radius * 2, Image.SCALE_FAST), this.subSpell, faction);
        //Game.projectiles.add(Game.projectiles.size(), proj);
    }

}

