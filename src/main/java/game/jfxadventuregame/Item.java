package game.jfxadventuregame;

import javax.swing.*;
import java.awt.*;


public class Item extends GameObject {

    transient protected Image image;
    protected int []imageValues;
    protected String source;
    protected String name;
    protected String description;
    public Spell attack;
    int amount;
    boolean consumable;
    Item(int []imageValues, String source, String name, int amount, String description, Spell attack, boolean consumable) {
        this.source = source;
        this.imageValues = imageValues;
        switch(source){
            case "weapons_sprite_sheet" -> this.image = Game.weapons_sprite_sheet.getSubimage(imageValues[0], imageValues[1], imageValues[2], imageValues[3]).getScaledInstance(30, 30, Image.SCALE_FAST);
            case "wands_and_books_sprite_sheet" -> this.image = Game.wands_and_books_sprite_sheet.getSubimage(imageValues[0], imageValues[1], imageValues[2], imageValues[3]).getScaledInstance(30, 30, Image.SCALE_FAST);
            case "armour_sprite_sheet" -> this.image = Game.armour_sprite_sheet.getSubimage(imageValues[0], imageValues[1], imageValues[2], imageValues[3]).getScaledInstance(30, 30, Image.SCALE_FAST);
        }
        //this.image = image.getScaledInstance(30, 30, Image.SCALE_FAST);
        this.icon = new ImageIcon();
        this.icon.setImage(image.getScaledInstance(100, 100, Image.SCALE_FAST));
        this.name = name;
        this.description = description;
        this.attack = attack;
        this.radius = 15;
        this.amount = amount;
        this.consumable = consumable;
    }

    static public Item random_weapon() {
        Spell attack;
        //Image image;
        int []imageValues = new int[4];
        String source = "weapons_sprite_sheet";
        String name;
        imageValues[2] = 43;
        imageValues[3]= 43;

        int [] spellImageValues = new int[4];
        String spellImageSource = "old_sprite_sheet";
        spellImageValues[2] = 32;
        spellImageValues[3] = 32;
        if (Game.random.nextInt(2) == 0) {
            name = "Basic sword";
            imageValues[0] = 246 + 43;
            imageValues[1] = 24;
            //image = Game.weapons_sprite_sheet.getSubimage(246 + 43, 24, 43, 43);
            spellImageValues[0] = 32 * 5;
            spellImageValues[1] = 32;
            //attack = new Spell(new ImageIcon(Game.old_sprite_sheet.getSubimage(32 * 5, 32, 32, 32)), "Sword swing", Game.random.nextInt(20) + 5, 20, 10, 0, 80, 2, Spell.type_t.physical, null);
            attack = new Spell(spellImageValues, spellImageSource, "Sword swing", Game.random.nextInt(20) + 5, 20, 10, 0, 80, 2, Spell.type_t.physical, null);
        } else {
            name = "Basic wand";

            imageValues[0] = 246 + 43 * 2;
            imageValues[1] = 24 + 43 * 9;
            //image = Game.weapons_sprite_sheet.getSubimage(246 + 43 * 2, 24 + 43 * 9, 43, 43);
            spellImageValues[0] = 32;
            spellImageValues[1] = 0;
            //attack = new Spell(new ImageIcon(Game.old_sprite_sheet.getSubimage(32, 0, 32, 32)), "Mana Bolt", Game.random.nextInt(20) + 5, 20, 50, 8, 20, 2, Spell.type_t.projectile, null);
            attack = new Spell(spellImageValues, spellImageSource, "Mana Bolt", Game.random.nextInt(20) + 5, 20, 50, 8, 20, 2, Spell.type_t.projectile, null);
        }
        return new Item(imageValues, source, name, 1, "A weapon", attack, false);
    }

    static public Item random_spell_book() {
        Spell attack;
        //Image image;
        int []imageValues = new int[4];
        String source = "wands_and_books_sprite_sheet";
        String name;
        String description;
        int type = Game.random.nextInt(3);

        imageValues[2] = 43;
        imageValues[3]= 43;

        int [] spellImageValues = new int[4];
        String spellImageSource = "old_sprite_sheet";
        spellImageValues[2] = 32;
        spellImageValues[3] = 32;

        if (type == 0) {
            name = "Spell book";
            description = "A guide to cast Fireball";
            imageValues[0] = 246;
            imageValues[1] = 24 + 43 * 5;

            //image = Game.wands_and_books_sprite_sheet.getSubimage(246, 24+43*5, 43, 43);

            spellImageValues[0] = 0;
            spellImageValues[1] = 0;
            //attack = new Spell(new ImageIcon(Game.old_sprite_sheet.getSubimage(0, 0, 32, 32)), "fireball", Game.random.nextInt(10) + 10, 10, 50, 6, 30, 2, Spell.type_t.projectile, new Spell(new ImageIcon(Game.old_sprite_sheet.getSubimage(0, 0, 32, 32)), "Explosion", 20, 0, 10, 0, 100, 5, Spell.type_t.projectile, null));
            attack = new Spell(spellImageValues, spellImageSource, "fireball", Game.random.nextInt(10) + 10, 10, 50, 6, 30, 2, Spell.type_t.projectile, new Spell(new int[]{0,0,32,32}, "old_sprite_sheet", "Explosion", 20, 0, 10, 0, 100, 5, Spell.type_t.projectile, null));
        } else if (type == 1){
            name = "Spell book";
            description = "A guide to cast Mana Bolt";
            imageValues[0] = 246 + 43 * 3;
            imageValues[1] = 24 + 43 * 5;
            //image = Game.wands_and_books_sprite_sheet.getSubimage(246+43*3, 24+43*5, 43, 43);

            spellImageValues[0] = 32;
            spellImageValues[1] = 0;
            //attack = new Spell(new ImageIcon(Game.old_sprite_sheet.getSubimage(32, 0, 32, 32)), "Mana Bolt", Game.random.nextInt(10) + 5, 5, 50, 8, 20, 1, Spell.type_t.projectile, null);
            attack = new Spell(spellImageValues, spellImageSource, "Mana Bolt", Game.random.nextInt(10) + 5, 5, 50, 8, 20, 1, Spell.type_t.projectile, null);
        } else {
            name = "Spell scroll";
            description = "You can let mana flow through this to summon a Lava pool. consumable";
            imageValues[0] = 246 + 43 * 5;
            imageValues[1] = 24 + 43 * 5;
            //image = Game.wands_and_books_sprite_sheet.getSubimage(246+43*5, 24+43*5, 43, 43);

            spellImageValues[0] = 0;
            spellImageValues[1] = 0;
            spellImageSource = "lavapool.png";
            //attack = new Spell(new ImageIcon("lavapool.png"), "Lava Pool", 10, 5, 200, 0, 100, 5, Spell.type_t.projectile, null);
            attack = new Spell(spellImageValues, spellImageSource, "Lava Pool", 10, 5, 200, 0, 100, 5, Spell.type_t.projectile, null);
        }
        return new Item(imageValues, source, name, 1, description, attack, false);
    }
    public boolean equals(Item item) {

        if (this.name.equals(item.name) &&
                this.description.equals(item.description) &&
                ((this.attack == null && item.attack == null) ||
                        (this.attack.type == item.attack.type &&
                                this.attack.name.equals(item.attack.name) &&
                                this.attack.radius == item.attack.radius &&
                                this.attack.movement_speed == item.attack.movement_speed &&
                                this.attack.mana_cost == item.attack.mana_cost &&
                                this.attack.piercing == item.attack.piercing &&
                                this.attack.duration == item.attack.duration &&
                                this.attack.damage_health == item.attack.damage_health))) {
            return true;
        }
        return false;
    }

    public void load(){
        switch(source){
            case "weapons_sprite_sheet" -> this.image = Game.weapons_sprite_sheet.getSubimage(imageValues[0], imageValues[1], imageValues[2], imageValues[3]).getScaledInstance(30, 30, Image.SCALE_FAST);
            case "wands_and_books_sprite_sheet" -> this.image = Game.wands_and_books_sprite_sheet.getSubimage(imageValues[0], imageValues[1], imageValues[2], imageValues[3]).getScaledInstance(30, 30, Image.SCALE_FAST);
            case "armour_sprite_sheet" -> this.image = Game.armour_sprite_sheet.getSubimage(imageValues[0], imageValues[1], imageValues[2], imageValues[3]).getScaledInstance(30, 30, Image.SCALE_FAST);
        }
        this.icon = new ImageIcon();
        this.icon.setImage(image.getScaledInstance(100, 100, Image.SCALE_FAST));
    }
}
