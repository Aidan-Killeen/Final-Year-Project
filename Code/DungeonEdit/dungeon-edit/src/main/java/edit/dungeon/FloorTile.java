package edit.dungeon;

import java.awt.*;

import java.awt.image.*;

public class FloorTile
{
    BufferedImage img;
    private int x, y, width, height;

    public FloorTile (BufferedImage img, int x, int y, int width, int height)
    {
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void paint(Graphics g)
    {
        g.drawImage(this.img, x*Level.sizeOfMap+App.SCREEN_WIDTH/2-(Level.w/2)*Level.sizeOfMap,
             y*Level.sizeOfMap+100*App.SCREEN_HEIGHT/1920, width, height, null);
    }

    
}
