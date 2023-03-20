package game.jfxadventuregame;

public class Area{
    protected int x, y, width, height;

    Area(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public boolean isIn (int xPos, int yPos){
        return xPos >= x && yPos >= y && xPos <= x + width && yPos <= y + height;
    }
}
