package game.jfxadventuregame;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.pow;

public class Player extends Character {
    transient protected double comprehension_speed; // now Wisdom
    //affects advancing/leveling speed
    transient protected double x_movement, y_movement;
    transient protected double mana, maxMana;

    //different requirements to trigger critical hit
    transient protected double item_stat_multiplier, mastery_multiplier; //Wisdom
    //like elements as mage, skills as warrior and crafting blueprint of tools and items as an alchemist
    public Spell selectedSpell;
    public int selectedSpellInt;
    protected int pointsAvailable;
    protected Inventory inventory;
    protected double experience;
    protected Inventory spellInventory;

    public Player(String faction) {
        super(faction);
        this.x_movement = 0;
        this.y_movement = 0;

        this.inventory = new Inventory();
        this.spellInventory = new Inventory();

        this.pickUpItem(Item.random_spell_book());
        this.pickUpItem(Item.random_spell_book());
        this.pickUpItem(Item.random_spell_book());
        this.pickUpItem(Item.random_weapon());
        this.pickUpItem(Item.random_weapon());

        //this.inventory.addItem(new Armour(Game.armour_sprite_sheet.getSubimage(246, 24 + 43, 43, 43), "basic armour", "Test", 100, 20, null));
        this.inventory.addItem(new Armour(new int[]{246, 24 + 43, 43, 43}, "armour_sprite_sheet", "basic armour", "Test", 100, 20, null));
        //this.inventory.addItem(new Item(Game.weapons_sprite_sheet.getSubimage(246 + 43 * 3, 24 + 43 * 9, 43, 43), "fire staff", 1, "a weapon", Game.spells[3], false));
        this.inventory.addItem(new Item(new int[]{246 + 43 * 3, 24 + 43 * 9, 43, 43}, "weapons_sprite_sheet", "fire staff", 1, "a weapon", Game.spells[3], false));
        this.radius = 20;
        this.icon = new ImageIcon(Game.old_sprite_sheet.getSubimage(32, 32, 32, 32).getScaledInstance((int) (2 * this.radius), (int) (2 * this.radius), Image.SCALE_FAST));
        this.experience = 0;

        this.spells.add(this.spells.size(), Game.spells[1]);
        this.spells.add(this.spells.size(), Game.spells[2]);
        this.spells.add(this.spells.size(), Game.spells[3]);
        this.spells.add(this.spells.size(), Game.spells[4]);

        this.levelUp(20);
        this.increaseStat("Intelligence", 0);
        this.increaseStat("Strength", 0);
        this.increaseStat("Endurance", 0);
        this.increaseStat("Dexterity", 0);
        this.increaseStat("Wisdom", 0);
        this.hp = maxHP;


        GameObject test = new GameObject();
        test.x = 100;
        test.y = 0;
        test.radius = 100;
        test.icon = new ImageIcon(Game.old_sprite_sheet.getSubimage(0, 32, 32, 32).getScaledInstance(200, 200, Image.SCALE_FAST));
        Game.obstacles.add(Game.obstacles.size(), test);
    }

    public void levelUp(int amount) {
        this.stage += amount;
        this.pointsAvailable += 4 * amount;
    }

    @Override
    public void increaseStat(String stat, int amount) {
        super.increaseStat(stat, amount);
        switch (stat) {
            case "Intelligence" -> maxMana = pow(1.05, intelligence) + intelligence + 10;

            case "Strength", "Endurance" -> {
                if (this.inventory.armourSlot != null) {
                    this.maxHP += ((Armour) this.inventory.armourSlot).hpBuff;
                    this.def += ((Armour) this.inventory.armourSlot).defenceBuff;
                }
            }
            case "Wisdom" -> {
                item_stat_multiplier = pow(1.015, wisdom);
                mastery_multiplier = wisdom * 0.02 + 1;
                comprehension_speed = pow(1.02, wisdom);
            }
        }
        this.pointsAvailable -= amount;
    }



    public void attack(int mouse_X, int mouse_Y) {
        if (this.selectedSpell != null) {
            this.addToCombo();
            this.selectedSpell.summonProjectile(this, Game.centerX - mouse_X, Game.centerY - mouse_Y, false);
        }
    }

    public void pickUpItem(Item item) {
        if (item.name.equals("Spell book") || item.name.equals("Spell scroll")) {
            spellInventory.addItem(item);
        } else {
            inventory.addItem(item);
        }
    }

    public void refresh() {
        this.mana += this.mana_recovery_speed;
        if (this.mana > this.maxMana) {
            this.mana = this.maxMana;
        }
        this.stamina += maxStamina / 100;
        if (this.stamina > this.maxStamina) {
            this.stamina = this.maxStamina;
        }
        //System.out.println(this.stamina);
    }

    public void load(Player tempPlayer){
        x = tempPlayer.x;
        y = tempPlayer.y;
        hp = tempPlayer.hp;
        intelligence = tempPlayer.intelligence;
        strength = tempPlayer.strength;
        endurance = tempPlayer.endurance;
        dexterity = tempPlayer.dexterity;
        wisdom = tempPlayer.wisdom;
        faction = tempPlayer.faction;
        pointsAvailable = tempPlayer.pointsAvailable;
        spells = tempPlayer.spells;
        selectedSpell = tempPlayer.selectedSpell;
        selectedSpellInt = tempPlayer.selectedSpellInt;
        experience = tempPlayer.experience;
        inventory = tempPlayer.inventory;
        spellInventory = tempPlayer.spellInventory;
        this.radius = 20;
        this.icon = new ImageIcon(Game.old_sprite_sheet.getSubimage(32, 32, 32, 32).getScaledInstance((int) (2 * this.radius), (int) (2 * this.radius), Image.SCALE_FAST));
        stage = (intelligence + endurance + strength + dexterity + wisdom + pointsAvailable)/4;
        levelUp(0);
        inventory.load();
        spellInventory.load();
        this.comboTimer = new Timer(Game.COMBO_TIMER_BASE_VALUE, this);
        this.combo = 0;
    }
}
