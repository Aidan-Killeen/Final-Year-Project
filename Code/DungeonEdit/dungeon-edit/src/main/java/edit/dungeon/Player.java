package edit.dungeon;

import java.awt.Graphics;

public class Player extends LevelElement
{
    private int x, y, width, height;

    public Player(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String print()
    {
        return ("Player: (" +x + ", " + y + ")");
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
        g.drawImage(l.playImg(), x*Level.sizeOfMap+App.SCREEN_WIDTH/2-(Level.w/2)*Level.sizeOfMap,
            y*Level.sizeOfMap+100*App.SCREEN_HEIGHT/1920, width, height, null);

    }
}
