package game.jfxadventuregame;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.Serializable;

import static java.lang.Math.*;

public class Inventory extends JPanel implements MouseInputListener, Serializable {
    transient protected int x, y;

    protected Item[] items;
    protected Item[] hotBar;
    protected Item armourSlot;
    public Item showItemStats;
    protected Item tempItem;

    transient protected boolean opened;
    transient private boolean painted;

    transient protected Area[] levelUps;

    transient protected Area[] itemSelection;
    protected int selectedItem; //0-8 = ItemSelection 9-11 = HotBar 12 = amourSlot

    transient protected Area[] hotBarSelection;
    transient protected Area armourSelection;

    Inventory() {
        this.setPreferredSize(new Dimension(1280, 720));
        this.x = 200;
        this.y = 200;

        this.opened = false;

        items = new Item[9];
        hotBar = new Item[3];
        armourSlot = null;
        //addItem(new Item(Game.weapons_sprite_sheet.getSubimage(246, 24, 43, 43), "Sword", 1, "one of the most basic Weapons", Game.spells[4]));


        //addItem(new Item(new ImageIcon("img.png").getImage(), "Oha", 2, "Test", null));
        //addItem(new Item(new ImageIcon("img.png").getImage(), "Oha", 6, "Test", null));


        levelUps = new Area[5];

        levelUps[0] = new Area(this.x + 596, this.y, 20, 20); //Intelligence
        levelUps[1] = new Area(this.x + 596, this.y + 65, 20, 20); //Strength
        levelUps[2] = new Area(this.x + 596, this.y + 145, 20, 20); //Endurance
        levelUps[3] = new Area(this.x + 596, this.y + 180, 20, 20); //Dexterity
        levelUps[4] = new Area(this.x + 596, this.y + 245, 20, 20); //Wisdom

        itemSelection = new Area[9];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                itemSelection[i * 3 + j] = new Area(this.x + j * 110, this.y + i * 110, 100, 100);
            }
        }

        hotBarSelection = new Area[3];
        for (int i = 0; i < 3; ++i) {
            hotBarSelection[i] = new Area(this.x + 110 * i, this.y + 370, 100, 100);
        }

        armourSelection = new Area(this.x + 330 + 50, this.y + 370, 100, 100);

    }

    public void dropItem(/*Item item*/) {
        if (tempItem != null) {
            double random_angle = PI * Game.random.nextInt(100)/50;
            tempItem.x = Game.player.x + cos(random_angle) * (tempItem.radius + Game.player.radius + 30);
            tempItem.y = Game.player.y + sin(random_angle) * (tempItem.radius + Game.player.radius + 30);
            Game.itemsLayingAround.add(tempItem);
            tempItem = null;
        }
                /* //Previous Version, to remove after Testing
                if(item instanceof Armour){
                        armourSlot = new Item(new ImageIcon("img.png").getImage(), "Test", "Test");
                }else if(selectedItem >= 0 && selectedItem < 9){
                        for(int i = 0; i < 9; ++i){
                                if(items[i].name.equals(item.name)){
                                        items[i] = new Item(new ImageIcon("img.png").getImage(), "Test", "Test");
                                        break;
                                }
                        }
                }else if(selectedItem < 12){
                        for(int i = 0; i < 3; ++i){
                                if(hotBar[i].name.equals(item.name)){
                                        hotBar[i] = new Item(new ImageIcon("img.png").getImage(), "Test", "Test");
                                        break;
                                }
                        }
                }
                selectedItem = 13;
                */
    }

    public boolean addItem(Item item) {
        for (int i = 0; i < 3; ++i) {
            if (hotBar[i] != null) {
                if (hotBar[i].equals(item)) {
                    hotBar[i].amount += item.amount;
                    return true;
                }
            }
        }

        for (int i = 0; i < 9; ++i) {
            if (items[i] != null) {
                if (items[i].equals(item)) {
                    items[i].amount += item.amount;
                    return true;
                }
            }
        }
        for (int i = 0; i < 3; ++i) {
            if (hotBar[i] == null) {
                hotBar[i] = item;
                return true;
            }
        }

        for (int i = 0; i < 9; ++i) {
            if (items[i] == null) {
                items[i] = item;
                return true;
            }
        }
        return false;
    }

    public void paint(Graphics g) {
        // Due to the transferring of the graphics object from GameFrame the position gets offset so that
        // 0 0 in GamePanel are 7 and 30 here
        // The above is only the case for the Version with the JFrame, the SwingNode/JFX-Version solves this issue
        Graphics2D g2D = (Graphics2D) g;
        int windowWidth = (int) Math.round(g.getClip().getBounds().getWidth());
        int windowHeight = (int) Math.round(g.getClip().getBounds().getHeight());

        // greyed out background
        g2D.setPaint(Color.BLACK);
        float alpha = 6 * 0.1f;
        AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

        if (!painted) {
            painted = true;
            g2D.setComposite(alcom);
            g2D.fillRect(0, 0, windowWidth, windowHeight);

            alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
            g2D.setComposite(alcom);
        }

        Font attributes = new Font("Arial", Font.PLAIN, 20);
        Font specifics = new Font("Arial", Font.PLAIN, 15);
        //Draw Inventory outline/background
        g2D.fillRect(190, 190, 920, 350);


        int xDist = this.x + 330;

        if (showItemStats == null) {
            if (this.equals(Game.player.inventory)) {

                //Insert Player image
                g2D.drawImage(Game.player.icon.getImage().getScaledInstance(250, 250, Image.SCALE_FAST), xDist, this.y, null);


                //BaseStats I guess
                // maybe round these to 2 decimal points


                g2D.setFont(specifics);
                g2D.setPaint(Color.red);
                g2D.drawString("HP: " + (int) Game.player.hp + "/" + (int) Game.player.maxHP, xDist, this.y + 271);
                g2D.setPaint(Color.green);
                g2D.drawString("Defence: " + (int) Game.player.def, xDist, this.y + 286);
                g2D.setPaint(Color.orange);
                g2D.drawString("Stamina: " + Game.player.stamina + "/" + Game.player.maxStamina, xDist, this.y + 301);
                g2D.setPaint(Color.blue);
                g2D.drawString("Mana: " + (int) Game.player.mana + "/" + (int) Game.player.maxMana, xDist, this.y + 316);

                xDist += 266;
                //LevelUpButtons
                if (Game.player.pointsAvailable < 1) {
                    alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                    g2D.setComposite(alcom);
                }
                g2D.setPaint(Color.cyan);
                g2D.fillRect(this.levelUps[0].x, this.levelUps[0].y, this.levelUps[0].width, this.levelUps[0].height);
                g2D.fillRect(this.levelUps[1].x, this.levelUps[1].y, this.levelUps[1].width, this.levelUps[1].height);
                g2D.fillRect(this.levelUps[2].x, this.levelUps[2].y, this.levelUps[2].width, this.levelUps[2].height);
                g2D.fillRect(this.levelUps[3].x, this.levelUps[3].y, this.levelUps[3].width, this.levelUps[3].height);
                g2D.fillRect(this.levelUps[4].x, this.levelUps[4].y, this.levelUps[4].width, this.levelUps[4].height);

                if (Game.player.pointsAvailable < 1) {
                    alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
                    g2D.setComposite(alcom);
                }

                xDist += 25;
                //Attributes
                g2D.setFont(attributes);
                g2D.setPaint(Color.decode("0x0032CEF3"));
                g2D.drawString("Intelligence: " + Game.player.intelligence, xDist, this.y + attributes.getSize());
                g2D.drawString("Strength: " + Game.player.strength, xDist, this.y + attributes.getSize() * 2 + specifics.getSize() * 3);
                g2D.drawString("Endurance: " + Game.player.endurance, xDist, this.y + attributes.getSize() * 3 + specifics.getSize() * 7);
                g2D.drawString("Dexterity: " + Game.player.dexterity, xDist, this.y + attributes.getSize() * 4 + specifics.getSize() * 8);
                g2D.drawString("Wisdom: " + Game.player.wisdom, xDist, this.y + attributes.getSize() * 5 + specifics.getSize() * 11);

                //Specific Stats
                xDist += 20;
                g2D.setFont(specifics);
                g2D.setPaint(Color.decode("0x0025A1BE"));
                //Intelligence
                g2D.drawString("Mana Recovery Speed (per Tick): " + String.format("%.2f", Game.player.mana_recovery_speed), xDist, this.y + attributes.getSize() + specifics.getSize());
                g2D.drawString("Spell Effectiveness: " + String.format("%.2f", Game.player.spell_effectiveness) + "x", xDist, this.y + attributes.getSize() + specifics.getSize() * 2);
                g2D.drawString("Boosts: Mana", xDist, this.y + attributes.getSize() + specifics.getSize() * 3);
                //Strength
                g2D.drawString("Base Damage: " + String.format("%.2f", Game.player.baseDamage), xDist, this.y + attributes.getSize() * 2 + specifics.getSize() * 4);
                g2D.drawString("Melee Damage Multiplier: " + String.format("%.2f", Game.player.melee_dmg_multiplier) + "x", xDist, this.y + attributes.getSize() * 2 + specifics.getSize() * 5);
                g2D.drawString("Critical Hit Damage Multiplier: " + String.format("%.2f", Game.player.crit_dmg_multiplier) + "x", xDist, this.y + attributes.getSize() * 2 + specifics.getSize() * 6);
                g2D.drawString("Boosts: HP, Defence", xDist, this.y + attributes.getSize() * 2 + specifics.getSize() * 7);
                //Endurance
                g2D.drawString("Boosts: HP, Defence, Stamina", xDist, this.y + attributes.getSize() * 3 + specifics.getSize() * 8);
                //Dexterity
                g2D.drawString("Movement Speed: " + String.format("%.2f", Game.player.movement_speed), xDist, this.y + attributes.getSize() * 4 + specifics.getSize() * 9);
                g2D.drawString("Critical Damage Multiplier: " + String.format("%.2f", Game.player.crit_dmg_multiplier) + "x", xDist, this.y + attributes.getSize() * 4 + specifics.getSize() * 10);

                g2D.drawString("Critical Chance: " + String.format("%.2f", 100 / Game.player.critrate) + "%", xDist, this.y + attributes.getSize() * 4 + specifics.getSize() * 11);
                //Wisdom
                g2D.drawString("Item Stat Multiplier: " + String.format("%.2f", Game.player.item_stat_multiplier) + "x", xDist, this.y + attributes.getSize() * 5 + specifics.getSize() * 12);
                g2D.drawString("Mastery Multiplier: " + String.format("%.2f", Game.player.mastery_multiplier) + "x", xDist, this.y + attributes.getSize() * 5 + specifics.getSize() * 13);
                g2D.drawString("Comprehension Speed: " + String.format("%.2f", Game.player.comprehension_speed), xDist, this.y + attributes.getSize() * 5 + specifics.getSize() * 14);

                //Points Available
                g2D.setFont(attributes);
                g2D.setPaint(Color.decode("0x000F00FF"));
                g2D.drawString("Points available: " + Game.player.pointsAvailable, this.x + 330 + 266, this.y + attributes.getSize() * 5 + specifics.getSize() * 15 + 5);
            }
        } else {

            //Insert Item image
            g2D.drawImage(showItemStats.image.getScaledInstance(250, 250, Image.SCALE_FAST), xDist, this.y, null);

            g2D.setFont(new Font("Arial", Font.PLAIN, 40));
            g2D.setPaint(Color.decode("0x0032CEF3"));
            g2D.drawString(this.showItemStats.name, xDist, this.y + 291);

            g2D.setFont(attributes);
            g2D.setPaint(Color.white);
            g2D.drawString("Amount: " + this.showItemStats.amount, xDist, this.y + 321);

            xDist += 266;

            //xDist += 25;

            //Attributes of the item
            g2D.drawString(this.showItemStats.description, xDist, this.y + attributes.getSize());

            int yDist = this.y + 3 * attributes.getSize();
            if (this.showItemStats.attack != null) {
                Spell attack = this.showItemStats.attack;
                for (int j = 1; attack != null; j++) {
                    g2D.setFont(attributes);
                    g2D.setPaint(Color.white);
                    g2D.drawString("Attack " + j + ": " + attack.name, xDist, yDist);
                    yDist += attributes.getSize();
                    //Specific Stats
                    xDist += 20;
                    g2D.setFont(specifics);
                    g2D.setPaint(Color.decode("0x0025A1BE"));
                    if (attack.type == Spell.type_t.projectile || attack.type == Spell.type_t.physical) {
                        g2D.drawString("Damage: " + String.format("%.2f", attack.damage_health), xDist, yDist);
                        yDist += specifics.getSize();
                        if (attack.type == Spell.type_t.projectile) {
                            g2D.drawString("Mana cost: " + String.format("%.2f", attack.mana_cost), xDist, yDist);
                        } else {
                            g2D.drawString("Stamina cost: " + String.format("%.2f", attack.mana_cost), xDist, yDist);
                        }
                        yDist += specifics.getSize();
                        g2D.drawString("Speed: " + String.format("%.2f", attack.movement_speed), xDist, yDist);
                        yDist += specifics.getSize();
                        g2D.drawString("size: " + String.format("%.2f", attack.radius), xDist, yDist);
                        yDist += specifics.getSize();
                        g2D.drawString("Pierce: " + String.format("%d", attack.piercing), xDist, yDist);
                        yDist += specifics.getSize();
                        g2D.drawString("Duration: " + String.format("%.2f", attack.duration), xDist, yDist);
                        yDist += attributes.getSize();
                        attack = attack.subSpell;
                    }
                    xDist -= 20;
                }
            }
            if (this.showItemStats instanceof Armour) {
                g2D.setFont(attributes);
                g2D.setPaint(Color.white);
                g2D.drawString("Armour values: ", xDist, yDist);
                yDist += attributes.getSize();
                xDist += 20;
                g2D.setFont(specifics);
                g2D.setPaint(Color.decode("0x0025A1BE"));
                g2D.drawString("Extra Health: " + String.format("%.2f", ((Armour) this.showItemStats).hpBuff), xDist, yDist);
                yDist += specifics.getSize();
                g2D.drawString("Defence: " + String.format("%.2f", ((Armour) this.showItemStats).defenceBuff), xDist, yDist);

            }
        }
        g2D.setFont(attributes);
        // inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                if (items[i * 3 + j] == null) {
                    g2D.setPaint(Color.GRAY);
                    g2D.fillRect(this.x + j * 110, this.y + i * 110, 100, 100);
                } else {
                    g2D.drawImage(items[i * 3 + j].icon.getImage(), this.x + j * 110, this.y + i * 110, null);
                    g2D.setPaint(Color.white);
                    g2D.drawString(String.format("%d", items[i * 3 + j].amount), this.x + 10 + j * 110, this.y + 20 + i * 110);

                }
            }
        }

        //HotBar
        g2D.setPaint(Color.BLACK);
        g2D.fillRect(this.x, this.y + 360, 340, 120);
        for (int i = 0; i < 3; ++i) {
            if (hotBar[i] == null) {
                g2D.setPaint(Color.GRAY);
                g2D.fillRect(this.x + 10 + i * 110, this.y + 370, 100, 100);
            } else {
                g2D.drawImage(hotBar[i].icon.getImage(), this.x + 10 + i * 110, this.y + 370, null);
                g2D.setPaint(Color.white);
                g2D.drawString(String.format("%d", hotBar[i].amount), this.x + 20 + i * 110, this.y + 390);

            }
        }
        if (this.equals(Game.player.inventory)) {
            //ArmourSlot
            g2D.setPaint(Color.BLACK);
            g2D.fillRect(armourSelection.x - 10, armourSelection.y - 10, armourSelection.width + 20, armourSelection.height + 20);
            if (armourSlot == null) {
                g2D.setPaint(Color.GRAY);
                g2D.fillRect(armourSelection.x, armourSelection.y, armourSelection.width, armourSelection.height);
            } else {
                g2D.drawImage(armourSlot.icon.getImage(), armourSelection.x, armourSelection.y, null);
                g2D.setPaint(Color.white);
                g2D.drawString(String.format("%d", armourSlot.amount), armourSelection.x + 10, armourSelection.y + 20);

            }
        }
        // tempSlot
        g2D.setPaint(Color.BLACK);
        g2D.fillRect(armourSelection.x - 10 + 400, armourSelection.y - 10, armourSelection.width + 20, armourSelection.height + 20);
        if (tempItem == null) {
            g2D.setPaint(Color.GRAY);
            g2D.fillRect(armourSelection.x + 400, armourSelection.y, armourSelection.width, armourSelection.height);
        } else {
            g2D.drawImage(tempItem.icon.getImage(), armourSelection.x + 400, armourSelection.y, null);
            g2D.setPaint(Color.white);
            g2D.drawString(String.format("%d", tempItem.amount), armourSelection.x + 410, armourSelection.y + 20);

        }
    }


    public void open() {
        this.opened = true;
    }

    public void close() {
        this.opened = false;
        this.painted = false;
        this.showItemStats = null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void load(){
        for(int i = 0; i < 9; ++i){
            if(items[i] != null) {
                items[i].load();
            }
            if(i < 3 && hotBar[i] != null){
                hotBar[i].load();
            }
        }
        if(armourSlot != null) {
            armourSlot.load();
        }
        if(tempItem != null) {
            tempItem.load();
        }

        this.setPreferredSize(new Dimension(1280, 720));
        this.x = 200;
        this.y = 200;

        this.opened = false;

        levelUps = new Area[5];

        levelUps[0] = new Area(this.x + 596, this.y, 20, 20); //Intelligence
        levelUps[1] = new Area(this.x + 596, this.y + 65, 20, 20); //Strength
        levelUps[2] = new Area(this.x + 596, this.y + 145, 20, 20); //Endurance
        levelUps[3] = new Area(this.x + 596, this.y + 180, 20, 20); //Dexterity
        levelUps[4] = new Area(this.x + 596, this.y + 245, 20, 20); //Wisdom

        itemSelection = new Area[9];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                itemSelection[i * 3 + j] = new Area(this.x + j * 110, this.y + i * 110, 100, 100);
            }
        }

        hotBarSelection = new Area[3];
        for (int i = 0; i < 3; ++i) {
            hotBarSelection[i] = new Area(this.x + 110 * i, this.y + 370, 100, 100);
        }

        armourSelection = new Area(this.x + 330 + 50, this.y + 370, 100, 100);

    }
}
