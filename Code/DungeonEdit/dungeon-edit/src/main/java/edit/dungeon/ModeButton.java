package edit.dungeon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.*;

public class ModeButton
{
    private int x;
    private int y;

    private char mode;

    BufferedImage icon;

    public ModeButton(int pos, char output, BufferedImage img)
    {
        this.x = 5;
        this.y = pos*Level.sizeOfMap;
        this.mode = output;
        this.icon = img;
    }

    public boolean clicked(int x, int y)
    {
        if(x > this.x && x < this.x + Level.sizeOfMap && y > this.y && y < this.y + Level.sizeOfMap)
        {
            return true;
        }
        else
        {
            return false;
        }
        
    }

    public char mode()
    {
        return this.mode;
    }
    public void paint (Graphics g, char mode)
    {
        if (this.mode == mode)
        {
            //highlights if selected
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, Level.sizeOfMap, Level.sizeOfMap);
        }

        g.drawImage(icon, x +2, y +2, Level.sizeOfMap -4, Level.sizeOfMap -4, null);
        
    }
}