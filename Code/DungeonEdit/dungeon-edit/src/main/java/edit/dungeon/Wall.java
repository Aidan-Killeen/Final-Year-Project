package edit.dungeon;

import java.awt.Graphics;

public class Wall extends LevelElement
{
    private int x, y, width, height;

    public Wall(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    @Override
    public void paint(Graphics g, Level l) 
    {
        int xAdj = x*Level.sizeOfMap+App.SCREEN_WIDTH/2-(Level.w/2)*Level.sizeOfMap;
        int yAdj = y*Level.sizeOfMap+100*App.SCREEN_HEIGHT/1920;

        g.drawImage(l.getWallVertImg(), xAdj, yAdj + (Level.sizeOfMap*1/3), width, height *2/3, null);
        g.drawImage(l.getWallTopImg(), xAdj, yAdj - (Level.sizeOfMap*2/3), width, height, null);

    }
}
