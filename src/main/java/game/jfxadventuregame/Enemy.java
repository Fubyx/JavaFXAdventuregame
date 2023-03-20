package game.jfxadventuregame;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;

import static java.lang.Math.PI;

public class Enemy extends Character {

    transient private ArrayList<Double> range_of_spells;
    protected double distanceToPlayer;
    protected double angleToPlayer;
    protected Item[] items;

    public Enemy(String faction, double x, double y, int stage, Item[] items) {
        super(faction);
        //insert Image
        this.radius = 25;
        this.x = x;
        this.y = y;
        this.calculateDistanceToPlayer();

        levelUp(stage);

        this.items = items;
        for (int i = 0; i < items.length; i++) {
            if (items[i] instanceof Armour) {
                this.maxHP += ((Armour) items[i]).hpBuff;
                this.def += ((Armour) items[i]).defenceBuff;
            } else {
                if (items[i].attack != null) {
                    this.spells.add(this.spells.size(), items[i].attack);
                }
            }
        }
        this.hp = maxHP;

        this.range_of_spells = new ArrayList<>();
        for (int i = 0; i < spells.size(); i++) {
            this.range_of_spells.add(this.spells.get(i).duration * this.spells.get(i).movement_speed + this.spells.get(i).radius + this.radius);
        }

    }

    public void calculateDistanceToPlayer() {
        double deltaX = Game.player.x - this.x;
        double deltaY = Game.player.y - this.y;
        this.distanceToPlayer = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        this.angleToPlayer = Math.atan(deltaY / deltaX) + ((deltaX > 0) ? 0 : PI);
    }

    @Override
    public String toString() {
        return "Enemy{" +
                "\n, distanceToPlayer=" + distanceToPlayer +
                "\n, angleToPlayer=" + angleToPlayer +
                "\n, stage=" + stage +
                "\n, intelligence=" + intelligence +
                "\n, strength=" + strength +
                "\n, dexterity=" + dexterity +
                "\n, wisdom=" + wisdom +
                "\n, endurance=" + endurance +
                "\n, def=" + def +
                "\n, stamina=" + stamina +
                "\n, maxStamina=" + maxStamina +
                "\n, baseDamage=" + baseDamage +
                "\n, spell_effectiveness=" + spell_effectiveness +
                "\n, mana_recovery_speed=" + mana_recovery_speed +
                "\n, melee_dmg_multiplier=" + melee_dmg_multiplier +
                "\n, movement_speed=" + movement_speed +
                "\n, combo_dmg_multiplier=" + combo_dmg_multiplier +
                "\n, crit_dmg_multiplier=" + crit_dmg_multiplier +
                "\n, hp=" + hp +
                "\n, maxHP=" + maxHP +
                "\n, spells=" + spells +
                "\n, comboTimer=" + comboTimer +
                "\n, combo=" + combo +
                "\n, image=" + icon +
                "\n, x=" + x +
                "\n, y=" + y +
                "\n, radius=" + radius +
                "}\n\n\n";
    }


    public void moveTowardsPlayer() {
        if (distanceToPlayer > 739) {
            return;
        }
        if (distanceToPlayer < range_of_spells.stream().max(Comparator.naturalOrder()).get() * 0.7 && distanceToPlayer > range_of_spells.stream().min(Comparator.naturalOrder()).get() * 2) {
            this.x -= Math.cos(angleToPlayer) * movement_speed;
            this.y -= Math.sin(angleToPlayer) * movement_speed;

        } else {
            this.x += Math.cos(angleToPlayer) * movement_speed;
            this.y += Math.sin(angleToPlayer) * movement_speed;
        }

        //Collision with Player


        for (int i = 0; i < spells.size(); i++) {
            if (distanceToPlayer < range_of_spells.get(i) && ((spells.get(i).type == Spell.type_t.physical && stamina < 0) || (spells.get(i).type != Spell.type_t.physical && mana_recovery_speed < 0))) {
                attack(i);
            }
        }
        this.mana_recovery_speed--;
        this.stamina--;

        Game.collisionCheck(this, Game.player, true);
    }

    public void attack(int index) {
        this.addToCombo();
        this.spells.get(index).summonProjectile(this, this.x - Game.player.x, this.y - Game.player.y, false);

    }

    public void load(){
        radius = 25;
        stage = (intelligence + endurance + strength + dexterity + wisdom)/4;
        levelUp(0);
        this.comboTimer = new Timer(Game.COMBO_TIMER_BASE_VALUE, this);
        this.combo = 0;
        this.range_of_spells = new ArrayList<>();
        for (int i = 0; i < spells.size(); i++) {
            this.range_of_spells.add(this.spells.get(i).duration * this.spells.get(i).movement_speed + this.spells.get(i).radius + this.radius);
        }
        for (Item item : items) {
            item.load();
        }
    }
}
