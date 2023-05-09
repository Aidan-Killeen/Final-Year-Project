package edit.dungeon;

import java.awt.Graphics;

public class Goal extends LevelElement
{
    private int x, y, width, height;

    public Goal(int x, int y, int width, int height)
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

        g.drawImage(l.getGoalImg(), x*Level.sizeOfMap+App.SCREEN_WIDTH/2-(Level.w/2)*Level.sizeOfMap,
            y*Level.sizeOfMap+100*App.SCREEN_HEIGHT/1920, width, height, null);

    }
}
