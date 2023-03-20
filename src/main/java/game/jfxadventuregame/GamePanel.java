package game.jfxadventuregame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.lang.Math.*;

public class GamePanel extends JPanel implements ActionListener, MouseListener {
    protected int deltaX;
    protected int deltaY;
    protected int screenScrollX, screenScrollY;
    protected ImageIcon background;
    public static double manaBarAnimationOffset;
    protected boolean pauseMenuOpened;
    protected Area continueButton;
    protected Area saveButton;
    protected Area loadButton;
    protected boolean saved;
    protected Area notAvailableArea;
    public int mousePressedTime;
    protected boolean[] isKeyPressed;
    protected Timer timer;
    protected boolean isMousePressed;
    protected Timer enemyCourseAdjust;

    GamePanel() {
        super();
        this.setPreferredSize(new Dimension(1280, 720));
        background = new ImageIcon("background.png");
        pauseMenuOpened = false;

        this.setLayout(null);

        continueButton = new Area(500,50,300,50);

        saveButton = new Area(500,150,300,50);

        loadButton = new Area(500,200,300,50);

        notAvailableArea = new Area(0,0,300,50);

        this.isKeyPressed = new boolean[]{false, false, false, false};

        this.timer = new Timer(Game.FRAME_TIME, this);
        this.timer.start();

        this.enemyCourseAdjust = new Timer(Game.ENEMY_COURSE_ADJUST_TIME, this);
        this.enemyCourseAdjust.start();

        this.addMouseListener(this);

        this.setVisible(true);
    }


    public void paintGame(Graphics g){


        Game.centerX = this.getWidth() >> 1; // byteshifting is faster than dividing by 2
        Game.centerY = this.getHeight() >> 1;
        deltaX = (int) (Game.player.x - Game.centerX); // the ingame coordinates of the top left corner
        deltaY = (int) (Game.player.y - Game.centerY);
        // background calculations
        screenScrollX = (deltaX % background.getIconWidth() > 0) ? (-(deltaX % background.getIconWidth())) : ((-(deltaX % background.getIconWidth())) - background.getIconWidth());
        screenScrollY = (deltaY % background.getIconHeight() > 0) ? (-(deltaY % background.getIconHeight())) : ((-(deltaY % background.getIconHeight())) - background.getIconHeight());

        Graphics2D g2D = (Graphics2D) g;



        // paint background
        g2D.drawImage(background.getImage(), screenScrollX, screenScrollY, null);
        g2D.drawImage(background.getImage(), screenScrollX + background.getIconWidth(), screenScrollY, null);
        g2D.drawImage(background.getImage(), screenScrollX, screenScrollY + background.getIconHeight(), null);
        g2D.drawImage(background.getImage(), screenScrollX + background.getIconWidth(), screenScrollY + background.getIconHeight(), null);

        // draw all items
        g2D.setPaint(Color.BLACK);
        for (int i = 0; i < Game.itemsLayingAround.size(); i++) {
            GameObject p = Game.itemsLayingAround.get(i);
            g2D.fillRect((int) (p.x - p.radius - deltaX - 5), (int) (p.y - p.radius - deltaY - 5), (int) (2 * p.radius + 10), (int) (2 * p.radius + 10));
            g2D.drawImage(Game.itemsLayingAround.get(i).image, (int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), null);
        }
        // draw all obstacles
        /*
        for (int i = 0; i < Game.obstacles.size(); i++) {
                GameObject p = Game.obstacles.get(i);
                g2D.drawImage(Game.obstacles.get(i).icon.getImage(), (int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), null);
        }*/

        // draw all projectiles
        for (int i = 0; i < Game.projectiles.size(); i++) {
            if (Game.projectiles.get(i).type != Spell.type_t.buff) {
                GameObject p = Game.projectiles.get(i);
                g2D.drawImage(Game.projectiles.get(i).image, (int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), null);
            }
        }
        // draw all enemies | textures still need to be replaced with images
        g2D.setPaint(Color.green);
        for (int i = 0; i < Game.enemies.size(); i++) {
            GameObject p = Game.enemies.get(i);
            g2D.fillOval((int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), (int) p.radius * 2, (int) p.radius * 2);
        }

        // draw player
        g2D.drawImage(Game.player.icon.getImage(), (int) (Game.centerX - Game.player.radius), (int) (Game.centerY - Game.player.radius), null);

        // user GUI:
        g2D.setStroke(new BasicStroke(10));
        // healthbar
        g2D.setPaint(Color.red);
        g2D.drawRect(this.getWidth() - 340, this.getHeight() - 80, (int) (250 * Game.player.hp / Game.player.maxHP), 20);
        g2D.setPaint(Color.white);
        g2D.drawRect(this.getWidth() - 350, this.getHeight() - 90, 270, 40);
        //staminabar
        g2D.setPaint(Color.orange);
        g2D.drawRect(this.getWidth() - 340, this.getHeight() - 140, 250 * Game.player.stamina / Game.player.maxStamina, 20);
        g2D.setPaint(Color.white);
        g2D.drawRect(this.getWidth() - 350, this.getHeight() - 150, 270, 40);
        // manabar
        if (manaBarAnimationOffset * Game.player.maxMana > Game.player.mana) {
            g2D.setPaint(Color.cyan);
            g2D.drawRect(this.getWidth() - 340, this.getHeight() - 200, (int) (250 * manaBarAnimationOffset), 20);
            manaBarAnimationOffset -= 0.001;
        } else {
            manaBarAnimationOffset = Game.player.mana / Game.player.maxMana;
        }
        g2D.setPaint(Color.blue);
        g2D.drawRect(this.getWidth() - 340, this.getHeight() - 200, (int) (250 * Game.player.mana / Game.player.maxMana), 20);

        g2D.setPaint(Color.white);
        g2D.drawRect(this.getWidth() - 350, this.getHeight() - 210, 270, 40);

        // hotBar
        g2D.setPaint(Color.BLACK);
        g2D.fillRect(this.getWidth() - 720, this.getHeight() - 165, 340, 120);
        g2D.setFont(new Font("Arial", Font.PLAIN, 20));
        for (int i = 0; i < 3; ++i) {
            if (Game.player.inventory.hotBar[i] == null) {
                g2D.setPaint(Color.GRAY);
                g2D.fillRect(this.getWidth() - 710 + i * 110, this.getHeight() - 155, 100, 100);
            } else {
                g2D.drawImage(Game.player.inventory.hotBar[i].icon.getImage(), this.getWidth() - 710 + i * 110, this.getHeight() - 155, null);
                g2D.setPaint(Color.white);
                g2D.drawString(String.format("%d", Game.player.inventory.hotBar[i].amount), this.getWidth() - 700 + i * 110, this.getHeight() - 135);

            }
        }

        // spell choosing circle
        if (mousePressedTime > 20) {
            float alpha = 7 * 0.1f;
            AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2D.setComposite(alcom);
            g2D.setStroke(new BasicStroke(105));
            int size = 0;
            for (int i = 0; i < 9; i++) {
                if (Game.player.spellInventory.items[i] != null && Game.player.spellInventory.items[i].attack != null) {
                    size++;
                }
            }
            for (int i = 0; i < size; i++) {
                g2D.setPaint(Color.DARK_GRAY);
                if (i == Game.player.selectedSpellInt) {
                    g2D.setPaint(Color.LIGHT_GRAY);
                }
                g2D.drawArc(Game.centerX - 120 + (int) (cos(PI / 2 + i * 2 * PI / size) * 80), Game.centerY - 120 - (int) (sin(PI / 2 + i * 2 * PI / size) * 80), 240, 240, (int) (90 + (i - 0.5) * 360 / size), 360 / size);
            }
            for (int j = 0, i = 0; j < 9; j++) {
                if (Game.player.spellInventory.items[j] != null) {
                    if (Game.player.spellInventory.items[j].attack != null) {
                        g2D.drawImage(Game.player.spellInventory.items[j].attack.image.getImage(), (int) (cos(PI / 2 + i * PI * 2 / size) * 200 + Game.centerX - 50), (int) (sin(-PI / 2 + i * 2 * PI / size) * 200 + Game.centerY - 50), null);
                        i++;
                    }
                }
            }
            alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
            g2D.setComposite(alcom);
        }



        //PauseMenu
        if(pauseMenuOpened){
            g2D.setPaint(Color.BLACK);

            float alpha = 6 * 0.1f;
            AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2D.setComposite(alcom);
            g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

            alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
            g2D.setComposite(alcom);


            g2D.setStroke(new BasicStroke(10));
            Font attributes = new Font("Arial", Font.BOLD, 30);


            g2D.drawRect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
            g2D.setPaint(Color.darkGray);
            g2D.fillRect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
            g2D.setFont(attributes);
            g2D.setPaint(Color.white);
            g2D.drawString("Continue", continueButton.x + continueButton.width/2 - 70, continueButton.y + 35);

            g2D.setPaint(Color.BLACK);
            g2D.drawRect(saveButton.x, saveButton.y, saveButton.width, saveButton.height);
            g2D.setPaint(Color.darkGray);
            g2D.fillRect(saveButton.x, saveButton.y, saveButton.width, saveButton.height);
            g2D.setFont(attributes);
            g2D.setPaint(Color.white);
            g2D.drawString("Save", saveButton.x + saveButton.width/2 - 35, saveButton.y + 35);

            g2D.setPaint(Color.BLACK);
            g2D.drawRect(loadButton.x, loadButton.y, loadButton.width, loadButton.height);
            g2D.setPaint(Color.darkGray);
            g2D.fillRect(loadButton.x, loadButton.y, loadButton.width, loadButton.height);
            g2D.setPaint(Color.white);
            g2D.drawString("Load", loadButton.x + loadButton.width/2 - 35, loadButton.y + 35);
            if(saved){
                g2D.setPaint(Color.BLACK);
                g2D.drawRect(notAvailableArea.x, notAvailableArea.y, notAvailableArea.width, notAvailableArea.height);
                g2D.setPaint(Color.darkGray);
                g2D.fillRect(notAvailableArea.x, notAvailableArea.y, notAvailableArea.width, notAvailableArea.height);
                
                g2D.setPaint(Color.green);
                g2D.drawString("Saved", notAvailableArea.x + notAvailableArea.width / 2 - (int) (2.5 * 17.5), notAvailableArea.y + 35);
                
            }
        }

        // some useful commands:
        //int[] xPoints = {150,250,350};
        //int[] yPoints = {300,150,300};
        //g2D.drawPolygon(xPoints, yPoints, 3);
        //g2D.fillPolygon(xPoints, yPoints, 3);


    }


    public void paint(Graphics g) {
        //super.paint(g);

        /*if((this.g == null) || (!this.g.equals(g))){
            System.out.println("Test");
            this.g = g;
        }//*/

        if (Game.player.inventory.opened) {
            Game.player.inventory.paint(g);
        } else if (Game.player.spellInventory.opened) {
            Game.player.spellInventory.paint(g);
        }else {
            paintGame(g);
        }

        /*
        Game.centerX = this.getWidth() >> 1; // byteshifting is faster than dividing by 2
        Game.centerY = this.getHeight() >> 1;
        deltaX = (int) (Game.player.x - Game.centerX); // the ingame coordinates of the top left corner
        deltaY = (int) (Game.player.y - Game.centerY);
        // background calculations
        screenscrollX = (deltaX % background.getIconWidth() > 0) ? (-(deltaX % background.getIconWidth())) : ((-(deltaX % background.getIconWidth())) - background.getIconWidth());
        screenscrollY = (deltaY % background.getIconHeight() > 0) ? (-(deltaY % background.getIconHeight())) : ((-(deltaY % background.getIconHeight())) - background.getIconHeight());

        Graphics2D g2D = (Graphics2D) g;

        //PauseMenu
        if(pauseMenuOpened){
            g2D.setPaint(Color.BLACK);
            float alpha = 6 * 0.1f;
            AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            if(!painted) {
                painted = true;
                g2D.setComposite(alcom);
                g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

                alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
                g2D.setComposite(alcom);
            }
            g2D.setStroke(new BasicStroke(10));
            Font attributes = new Font("Arial", Font.BOLD, 30);


            g2D.drawRect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
            g2D.setPaint(Color.darkGray);
            g2D.fillRect(continueButton.x, continueButton.y, continueButton.width, continueButton.height);
            g2D.setFont(attributes);
            g2D.setPaint(Color.white);
            g2D.drawString("Continue", continueButton.x + continueButton.width/2 - 70, continueButton.y + 35);

            g2D.setPaint(Color.BLACK);
            g2D.drawRect(saveButton.x, saveButton.y, saveButton.width, saveButton.height);
            g2D.setPaint(Color.darkGray);
            g2D.fillRect(saveButton.x, saveButton.y, saveButton.width, saveButton.height);
            g2D.setFont(attributes);
            g2D.setPaint(Color.white);
            g2D.drawString("Save", saveButton.x + saveButton.width/2 - 35, saveButton.y + 35);

            g2D.setPaint(Color.BLACK);
            g2D.drawRect(loadButton.x, loadButton.y, loadButton.width, loadButton.height);
            g2D.setPaint(Color.darkGray);
            g2D.fillRect(loadButton.x, loadButton.y, loadButton.width, loadButton.height);
            g2D.setPaint(Color.white);
            g2D.drawString("Load", loadButton.x + loadButton.width/2 - 35, loadButton.y + 35);
            if(notAvailable || saved){
                g2D.setPaint(Color.BLACK);
                g2D.drawRect(notAvailableArea.x, notAvailableArea.y, notAvailableArea.width, notAvailableArea.height);
                g2D.setPaint(Color.darkGray);
                g2D.fillRect(notAvailableArea.x, notAvailableArea.y, notAvailableArea.width, notAvailableArea.height);
                if(notAvailable) {
                    g2D.setPaint(Color.red);
                    g2D.drawString("Not available", notAvailableArea.x + notAvailableArea.width / 2 - (int) (6.5 * 17.5), notAvailableArea.y + 35);
                }else{
                    g2D.setPaint(Color.green);
                    g2D.drawString("Saved", notAvailableArea.x + notAvailableArea.width / 2 - (int) (2.5 * 17.5), notAvailableArea.y + 35);
                }
            }
            if(writing){
                loadInput.setVisible(true);
            }
            return;
        }

        // paint background
        g2D.drawImage(background.getImage(), screenscrollX, screenscrollY, null);
        g2D.drawImage(background.getImage(), screenscrollX + background.getIconWidth(), screenscrollY, null);
        g2D.drawImage(background.getImage(), screenscrollX, screenscrollY + background.getIconHeight(), null);
        g2D.drawImage(background.getImage(), screenscrollX + background.getIconWidth(), screenscrollY + background.getIconHeight(), null);

        // draw all items
        g2D.setPaint(Color.BLACK);
        for (int i = 0; i < Game.itemsLayingAround.size(); i++) {
            GameObject p = Game.itemsLayingAround.get(i);
            g2D.fillRect((int) (p.x - p.radius - deltaX - 5), (int) (p.y - p.radius - deltaY - 5), (int) (2 * p.radius + 10), (int) (2 * p.radius + 10));
            g2D.drawImage(Game.itemsLayingAround.get(i).image, (int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), null);
        }
        // draw all obstacles
        /*
        for (int i = 0; i < Game.obstacles.size(); i++) {
                GameObject p = Game.obstacles.get(i);
                g2D.drawImage(Game.obstacles.get(i).icon.getImage(), (int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), null);
        }

        // draw all projectiles
        for (int i = 0; i < Game.projectiles.size(); i++) {
            if (Game.projectiles.get(i).type != Spell.type_t.buff) {
                GameObject p = Game.projectiles.get(i);
                g2D.drawImage(Game.projectiles.get(i).image, (int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), null);
            }
        }
        // draw all enemies | textures still need to be replaced with images
        g2D.setPaint(Color.green);
        for (int i = 0; i < Game.enemies.size(); i++) {
            GameObject p = Game.enemies.get(i);
            g2D.fillOval((int) (p.x - p.radius - deltaX), (int) (p.y - p.radius - deltaY), (int) p.radius * 2, (int) p.radius * 2);
        }

        // draw player
        g2D.drawImage(Game.player.icon.getImage(), (int) (Game.centerX - Game.player.radius), (int) (Game.centerY - Game.player.radius), null);

        // user GUI:
        g2D.setStroke(new BasicStroke(10));
        // healthbar
        g2D.setPaint(Color.red);
        g2D.drawRect(this.getWidth() - 340, this.getHeight() - 80, (int) (250 * Game.player.hp / Game.player.maxHP), 20);
        g2D.setPaint(Color.white);
        g2D.drawRect(this.getWidth() - 350, this.getHeight() - 90, 270, 40);
        //staminabar
        g2D.setPaint(Color.orange);
        g2D.drawRect(this.getWidth() - 340, this.getHeight() - 140, 250 * Game.player.stamina / Game.player.maxStamina, 20);
        g2D.setPaint(Color.white);
        g2D.drawRect(this.getWidth() - 350, this.getHeight() - 150, 270, 40);
        // manabar
        if (manabarAnimationOffset * Game.player.maxMana > Game.player.mana) {
            g2D.setPaint(Color.cyan);
            g2D.drawRect(this.getWidth() - 340, this.getHeight() - 200, (int) (250 * manabarAnimationOffset), 20);
            manabarAnimationOffset -= 0.001;
        } else {
            manabarAnimationOffset = Game.player.mana / Game.player.maxMana;
        }
        g2D.setPaint(Color.blue);
        g2D.drawRect(this.getWidth() - 340, this.getHeight() - 200, (int) (250 * Game.player.mana / Game.player.maxMana), 20);

        g2D.setPaint(Color.white);
        g2D.drawRect(this.getWidth() - 350, this.getHeight() - 210, 270, 40);

        // hotBar
        g2D.setPaint(Color.BLACK);
        g2D.fillRect(this.getWidth() - 720, this.getHeight() - 165, 340, 120);
        g2D.setFont(new Font("Arial", Font.PLAIN, 20));
        for (int i = 0; i < 3; ++i) {
            if (Game.player.inventory.hotBar[i] == null) {
                g2D.setPaint(Color.GRAY);
                g2D.fillRect(this.getWidth() - 710 + i * 110, this.getHeight() - 155, 100, 100);
            } else {
                g2D.drawImage(Game.player.inventory.hotBar[i].icon.getImage(), this.getWidth() - 710 + i * 110, this.getHeight() - 155, null);
                g2D.setPaint(Color.white);
                g2D.drawString(String.format("%d", Game.player.inventory.hotBar[i].amount), this.getWidth() - 700 + i * 110, this.getHeight() - 135);

            }
        }

        // spell choosing circle
        if (GameScene.mousePressedTime > 20) {
            float alpha = 7 * 0.1f;
            AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
            g2D.setComposite(alcom);
            g2D.setStroke(new BasicStroke(105));
            int size = 0;
            for (int i = 0; i < 9; i++) {
                if (Game.player.spellInventory.items[i] != null && Game.player.spellInventory.items[i].attack != null) {
                    size++;
                }
            }
            for (int i = 0; i < size; i++) {
                g2D.setPaint(Color.DARK_GRAY);
                if (i == Game.player.selectedSpellInt) {
                    g2D.setPaint(Color.LIGHT_GRAY);
                }
                g2D.drawArc(Game.centerX - 120 + (int) (cos(PI / 2 + i * 2 * PI / size) * 80), Game.centerY - 120 - (int) (sin(PI / 2 + i * 2 * PI / size) * 80), 240, 240, (int) (90 + (i - 0.5) * 360 / size), 360 / size);
            }
            for (int j = 0, i = 0; j < 9; j++) {
                if (Game.player.spellInventory.items[j] != null) {
                    if (Game.player.spellInventory.items[j].attack != null) {
                            g2D.drawImage(Game.player.spellInventory.items[j].attack.image.getImage(), (int) (cos(PI / 2 + i * PI * 2 / size) * 200 + Game.centerX - 50), (int) (sin(-PI / 2 + i * 2 * PI / size) * 200 + Game.centerY - 50), null);
                            i++;
                    }
                }
            }
            alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
            g2D.setComposite(alcom);
        }


        // some useful commands:
        //g2D.drawImage(image, 0, 0, null);

        //g2D.setPaint(Color.pink);
        //g2D.drawRect(0, 0, 100, 200);
        //g2D.fillRect(0, 0, 100, 200);

        //g2D.setPaint(Color.orange);
        //g2D.drawOval(0, 0, 100, 100);
        //g2D.fillOval(0, 0, 100, 100);
        //g2D.fillArc(0, 0, 100, 100, 180, 180);

        //int[] xPoints = {150,250,350};
        //int[] yPoints = {300,150,300};
        //g2D.drawPolygon(xPoints, yPoints, 3);
        //g2D.fillPolygon(xPoints, yPoints, 3);

        //g2D.setFont(new Font("Ink Free",Font.BOLD,50));
        //g2D.drawString("U R A WINNER! :D", 50, 50);
        //*/
    }

    public void keyPressed(int keyCode) {
        if(pauseMenuOpened){
            if(keyCode == 27){//esc
                pauseMenuOpened = false;
                repaint();
                saved = false;
                timer.restart();
            }
            return;
        }
        //keyPressed(e);
        switch (keyCode) {
            case 87 -> {
                if (!isKeyPressed[0]) {
                    Game.player.y_movement -= Game.player.movement_speed;
                    isKeyPressed[0] = true;
                } //w
            }
            case 65 -> {
                if (!isKeyPressed[1]) {
                    Game.player.x_movement -= Game.player.movement_speed;
                    isKeyPressed[1] = true;
                }//a
            }
            case 83 -> {
                if (!isKeyPressed[2]) {
                    Game.player.y_movement += Game.player.movement_speed;
                    isKeyPressed[2] = true;
                }//s
            }
            case 68 -> {
                if (!isKeyPressed[3]) {
                    Game.player.x_movement += Game.player.movement_speed;
                    isKeyPressed[3] = true;
                }//d
            }
            case 81 -> { //q
                if (Game.player.inventory.opened) {
                    Game.player.inventory.dropItem();
                    repaint();
                } else if (Game.player.spellInventory.opened) {
                    Game.player.spellInventory.dropItem();
                    repaint();
                }
            }
            case 73 -> { //i
                if (Game.player.inventory.opened) {
                    Game.player.inventory.showItemStats = Game.player.inventory.tempItem;
                } else if (Game.player.spellInventory.opened) {
                    Game.player.spellInventory.showItemStats = Game.player.spellInventory.tempItem;
                }
                repaint();
            }
            case 27 -> {//esc
                if(Game.player.inventory.opened){
                    Game.player.inventory.close();
                    repaint();
                    pauseMenuOpened = true;
                    repaint();
                }else if(Game.player.spellInventory.opened){
                    Game.player.spellInventory.close();
                    repaint();
                    pauseMenuOpened = true;
                    repaint();
                }else{
                    timer.stop();
                    //repaint();
                    pauseMenuOpened = true;
                    repaint();
                }
            }
        }

    }

    public void keyReleased(int keyCode) {
        //keyReleased(e);
        if(!pauseMenuOpened) {
            switch (keyCode) {
                case 87 -> {
                    Game.player.y_movement += Game.player.movement_speed;
                    isKeyPressed[0] = false;
                }
                case 65 -> {
                    Game.player.x_movement += Game.player.movement_speed;
                    isKeyPressed[1] = false;
                }
                case 83 -> {
                    Game.player.y_movement -= Game.player.movement_speed;
                    isKeyPressed[2] = false;
                }
                case 68 -> {
                    Game.player.x_movement -= Game.player.movement_speed;
                    isKeyPressed[3] = false;
                }
                case 69 -> {//e
                    if (Game.player.inventory.opened) {
                        Game.player.inventory.close();
                        timer.restart();
                    } else if (!Game.player.spellInventory.opened) {
                        timer.stop();
                        Game.player.inventory.open();
                        repaint();
                    }
                }
                case 82 -> {    //r
                    if (Game.player.spellInventory.opened) {
                        Game.player.spellInventory.close();
                        timer.restart();
                    } else if (!Game.player.inventory.opened) {
                        timer.stop();
                        Game.player.spellInventory.open();
                        repaint();
                    }
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.timer) {
            // Main game loop
            if (Game.player.stamina > 0) {
                if (Game.player.x_movement != 0 && Game.player.y_movement != 0) {
                    Game.player.x += Game.player.x_movement * 0.71;
                    Game.player.y += Game.player.y_movement * 0.71;
                    Game.player.stamina -= 2;
                } else if (Game.player.x_movement != 0 || Game.player.y_movement != 0) {
                    Game.player.x += Game.player.x_movement;
                    Game.player.y += Game.player.y_movement;
                    Game.player.stamina -= 2;
                }
            }
            Game.player.refresh();
            Game.collisions_and_movements();


            //EnemySpawning | later let them spawn in random structures
            for (int i = 0; i < Game.random.nextInt(10); ++i) {
                if (Game.random.nextInt(500) == 13) {
                    switch (Game.random.nextInt(4)) {
                        case 0 -> //north
                                Game.enemies.add(Game.enemies.size(), new Enemy("hostile", Game.random.nextInt(this.getWidth()) + Game.player.x - this.getWidth() / 2, Game.player.y - this.getHeight() / 2, Game.random.nextInt(20), new Item[]{Item.random_weapon(), Item.random_weapon()}));
                        case 1 -> //east
                                Game.enemies.add(Game.enemies.size(), new Enemy("hostile", this.getWidth() / 2 + Game.player.x, Game.random.nextInt(this.getHeight() + 1) + Game.player.y - this.getHeight() / 2, Game.random.nextInt(20), new Item[]{Item.random_weapon(), Item.random_weapon()}));
                        case 2 -> //south
                                Game.enemies.add(Game.enemies.size(), new Enemy("hostile", Game.random.nextInt(this.getWidth()) + Game.player.x - this.getWidth() / 2, this.getHeight() / 2 + Game.player.y, Game.random.nextInt(20), new Item[]{Item.random_weapon(), Item.random_weapon()}));
                        case 3 -> //west
                                Game.enemies.add(Game.enemies.size(), new Enemy("hostile", Game.player.x - this.getWidth() / 2, Game.random.nextInt(this.getHeight() + 1) + Game.player.y - this.getHeight() / 2, Game.random.nextInt(20), new Item[]{Item.random_weapon(), Item.random_weapon()}));
                    }
                }
            }
            if (isMousePressed) {
                mousePressedTime++;
            }
            repaint();

        } else if (e.getSource() == this.enemyCourseAdjust) {
            // separate slower loop for some calculations
            for (Enemy en : Game.enemies) {
                en.calculateDistanceToPlayer();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(pauseMenuOpened){
            int mouseX = e.getX();
            int mouseY = e.getY();
            if(saveButton.isIn(mouseX, mouseY)){
                if(Game.save(this.getFocusCycleRootAncestor())){
                    saved = true;
                }
                repaint();
                return;
            }
            if(loadButton.isIn(mouseX, mouseY)){
                Game.load(this.getFocusCycleRootAncestor());
                pauseMenuOpened = false;
                repaint();
                saved = false;
                this.timer.restart();
                return;
            }
            if(continueButton.isIn(mouseX, mouseY)){
                pauseMenuOpened = false;
                repaint();
                saved = false;
                this.timer.restart();
                return;
            }
        }
        for (int k = 0; k < 2; k++) {
            Inventory inv;
            if (k == 0) {
                inv = Game.player.inventory;
            } else {
                inv = Game.player.spellInventory;
            }
            if (inv.opened) {
                int mouseX = e.getX();
                int mouseY = e.getY();

                if (k == 0) {
                    if (Game.player.pointsAvailable > 0) {
                        if (inv.levelUps[0].isIn(mouseX , mouseY)) {
                            Game.player.increaseStat("Intelligence", 1);
                        } else if (inv.levelUps[1].isIn(mouseX, mouseY )) {
                            Game.player.increaseStat("Strength", 1);
                        } else if (inv.levelUps[2].isIn(mouseX, mouseY)) {
                            Game.player.increaseStat("Endurance", 1);
                        } else if (inv.levelUps[3].isIn(mouseX, mouseY)) {
                            Game.player.increaseStat("Dexterity", 1);
                        } else if (inv.levelUps[4].isIn(mouseX, mouseY)) {
                            Game.player.increaseStat("Wisdom", 1);
                        }
                    }
                }
                for (int i = 0; i < inv.items.length; ++i) {
                    if (inv.itemSelection[i].isIn(mouseX, mouseY)) {
                        if (e.getButton() == 1) {
                            if (inv.items[i] != null && inv.tempItem != null && inv.items[i].equals(inv.tempItem)) {
                                inv.items[i].amount += inv.tempItem.amount;
                                inv.tempItem = null;
                            } else {
                                Item temp = inv.items[i];
                                inv.items[i] = inv.tempItem;
                                inv.tempItem = temp;
                            }
                        } else if (e.getButton() == 3 && inv.items[i] != null) {
                            if (inv.tempItem == null) {
                                if (!(inv.items[i] instanceof Armour)) {
                                    //inv.tempItem = new Item(inv.items[i].image, inv.items[i].name, 1, inv.items[i].description, inv.items[i].attack, inv.items[i].consumable);
                                    inv.tempItem = new Item(inv.items[i].imageValues, inv.items[i].source, inv.items[i].name, 1, inv.items[i].description, inv.items[i].attack, inv.items[i].consumable);
                                } else {
                                    Armour temp = (Armour) inv.items[i];
                                    //inv.tempItem = new Armour(temp.image, temp.name, temp.description, temp.hpBuff, temp.defenceBuff, temp.attack);
                                    inv.tempItem = new Armour(temp.imageValues, temp.source, temp.name, temp.description, temp.hpBuff, temp.defenceBuff, temp.attack);
                                }
                                inv.items[i].amount--;
                            } else if (inv.items[i].equals(inv.tempItem)) {
                                inv.items[i].amount--;
                                inv.tempItem.amount++;
                            }
                            if (inv.items[i].amount < 1) {
                                inv.items[i] = null;
                            }
                        } else if (e.getButton() == 2) {
                            inv.showItemStats = inv.items[i];
                        }
                    }

                }
                for (int i = 0; i < inv.hotBar.length; ++i) {
                    if (inv.hotBarSelection[i].isIn(mouseX, mouseY)) {
                        if (e.getButton() == 1) {
                            if (inv.hotBar[i] != null && inv.tempItem != null && inv.hotBar[i].equals(inv.tempItem)) {
                                inv.hotBar[i].amount += inv.tempItem.amount;
                                inv.tempItem = null;
                            } else {
                                Item temp = inv.hotBar[i];
                                inv.hotBar[i] = inv.tempItem;
                                inv.tempItem = temp;
                            }
                        } else if (e.getButton() == 3 && inv.hotBar[i] != null) {
                            if (inv.tempItem == null) {
                                if (!(inv.hotBar[i] instanceof Armour)) {
                                    //inv.tempItem = new Item(inv.hotBar[i].image, inv.hotBar[i].name, 1, inv.hotBar[i].description, inv.hotBar[i].attack, inv.hotBar[i].consumable);
                                    inv.tempItem = new Item(inv.hotBar[i].imageValues, inv.hotBar[i].source, inv.hotBar[i].name, 1, inv.hotBar[i].description, inv.hotBar[i].attack, inv.hotBar[i].consumable);
                                } else {
                                    Armour temp = (Armour) inv.hotBar[i];
                                    //inv.tempItem = new Armour(temp.image, temp.name, temp.description, temp.hpBuff, temp.defenceBuff, temp.attack);
                                    inv.tempItem = new Armour(temp.imageValues, temp.source, temp.name, temp.description, temp.hpBuff, temp.defenceBuff, temp.attack);
                                }
                                inv.hotBar[i].amount--;
                            } else if (inv.tempItem.equals(inv.hotBar[i])) {
                                inv.hotBar[i].amount--;
                                inv.tempItem.amount++;
                            }
                            if (inv.hotBar[i].amount < 1) {
                                inv.hotBar[i] = null;
                            }
                        } else if (e.getButton() == 2) {
                            inv.showItemStats = inv.hotBar[i];
                        }
                    }

                }
                if (inv.armourSelection.isIn(mouseX, mouseY)) {
                    if (e.getButton() == 2) {
                        inv.showItemStats = inv.armourSlot;
                    } else if (inv.tempItem instanceof Armour || inv.tempItem == null) {
                        if (inv.armourSlot != null) {
                            Game.player.maxHP -= ((Armour) inv.armourSlot).hpBuff;
                            Game.player.def -= ((Armour) inv.armourSlot).defenceBuff;
                        }
                        Item temp = inv.armourSlot;
                        inv.armourSlot = inv.tempItem;
                        inv.tempItem = temp;
                        if (inv.armourSlot != null) {
                            Game.player.maxHP += ((Armour) inv.armourSlot).hpBuff;
                            Game.player.def += ((Armour) Game.player.inventory.armourSlot).defenceBuff;
                        }
                        if (Game.player.hp > Game.player.maxHP) {
                            Game.player.hp = Game.player.maxHP;
                        }
                    }
                }
                repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (!Game.player.inventory.opened && !Game.player.spellInventory.opened && !pauseMenuOpened) {
            switch (e.getButton()) {
                case 1: // lmb
                    if (Game.player.inventory.hotBar[0] != null && Game.player.inventory.hotBar[0].attack != null) {
                        Game.player.inventory.hotBar[0].attack.summonProjectile(Game.player, Game.centerX - e.getX(), Game.centerY - e.getY(), true);
                    } else {
                        if (Game.spells[0].summonProjectile(Game.player, Game.centerX - e.getX(), Game.centerY - e.getY(), false)) {
                            Game.projectiles.get(Game.projectiles.size() - 1).damage = Game.player.baseDamage;
                        }
                    }
                    break;
                case 2: // mouse_wheel
                    break;
                case 3: // rmb
                    isMousePressed = true;
                    break;
                case 4: // undo (browser)
                    if (Game.player.inventory.hotBar[2] != null && Game.player.inventory.hotBar[2].attack != null) {
                        Game.player.inventory.hotBar[2].attack.summonProjectile(Game.player, Game.centerX - e.getX(), Game.centerY - e.getY(), true);
                    } else {
                        if (Game.spells[0].summonProjectile(Game.player, Game.centerX - e.getX(), Game.centerY - e.getY(), false)) {
                            Game.projectiles.get(Game.projectiles.size() - 1).damage = Game.player.baseDamage;
                        }

                    }
                    break;
                case 5: // redo (browser)
                    if (Game.player.inventory.hotBar[1] != null && Game.player.inventory.hotBar[1].attack != null) {
                        Game.player.inventory.hotBar[1].attack.summonProjectile(Game.player, Game.centerX - e.getX(), Game.centerY - e.getY(), true);
                    } else {
                        if (Game.spells[0].summonProjectile(Game.player, Game.centerX - e.getX(), Game.centerY - e.getY(), false)) {
                            Game.projectiles.get(Game.projectiles.size() - 1).damage = Game.player.baseDamage;
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(pauseMenuOpened || Game.player.inventory.opened || Game.player.spellInventory.opened){
            return;
        }
        switch (e.getButton()) {
            case 1: // lmb
                break;
            case 2: // mouse_wheel
                break;
            case 3: // rmb
                isMousePressed = false;
                if (mousePressedTime < 20) {
                    if (!Game.player.inventory.opened && !Game.player.spellInventory.opened) {
                        Game.player.attack(e.getX(), e.getY());
                    }
                } else {
                    // select spell
                    //(Game.centerX - 120 + (int) (cos(PI / 2 + i * 2 * PI / Game.player.spells.size()) * 80), Game.centerY - 120 - (int) (sin(PI / 2 + i * 2 * PI / Game.player.spells.size()) * 80), 240, 240, (int) (90 + (i - 0.5) * 360 / Game.player.spells.size()), 360 / Game.player.spells.size());
                    int size = 0;
                    for (int i = 0; i < 9; i++) {
                        if (Game.player.spellInventory.items[i] != null) {
                            if (Game.player.spellInventory.items[i].attack != null) {
                                size++;
                            }
                        }
                    }
                    for (int i = 0; i < size; i++) {
                        double temp_X = Game.centerX + (int) (cos(PI / 2 + i * 2 * PI / size) * 80);
                        double temp_Y = Game.centerY - (int) (sin(PI / 2 + i * 2 * PI / size) * 80);
                        double distance = sqrt(pow(e.getX() - temp_X, 2) + pow(e.getY() - temp_Y, 2));
                        double borderAngle = PI / 2 + (i - 0.5) * 2 * PI / size;
                        if (borderAngle > 2 * PI) {
                            borderAngle -= 2 * PI;
                        }
                        double angleToMouse;
                        if (e.getX() - temp_X == 0) {
                            if (e.getY() - temp_Y > 0) {
                                angleToMouse = 1.5 * PI;
                            } else {
                                angleToMouse = 0.5 * PI;
                            }
                        } else {
                            angleToMouse = atan((temp_Y - e.getY()) / (e.getX() - temp_X)) + ((temp_X - e.getX() > 0) ? PI : 0);
                            if (angleToMouse < 0) {
                                angleToMouse += 2 * PI;
                            }
                        }
                        if (distance > 68 && distance < 173 && ((angleToMouse > borderAngle && angleToMouse < borderAngle + 2 * PI / size) || (angleToMouse > borderAngle - 2 * PI && angleToMouse < borderAngle - 2 * PI + 2 * PI / size))) {
                            Game.player.selectedSpellInt = i;
                            for (int j = 0; j < 9; j++) {
                                if (Game.player.spellInventory.items[j] != null && Game.player.spellInventory.items[j].attack != null) {
                                    i--;
                                }
                                if (i == -1) {
                                    Game.player.selectedSpell = Game.player.spellInventory.items[j].attack;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
                mousePressedTime = 0;
                break;
            case 4: // undo (browser)
                break;
            case 5: // redo (browser)
                break;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
