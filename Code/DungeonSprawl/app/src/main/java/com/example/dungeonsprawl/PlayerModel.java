package com.example.dungeonsprawl;

//import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class PlayerModel
{

    private Bitmap bm, bm_current, bm_walk_up, bm_walk_down, bm_walk_left, bm_walk_right;
    private int x, y;
    private boolean move_up, move_down, move_left, move_right;



    public PlayerModel(Bitmap bm, int sizeOfMap, int x, int y)
    {
        this.bm = bm;
        this.x = x;
        this.y = y;

        int height = bm.getHeight();
        int width = bm.getWidth()/16;

        //divide image
        bm_walk_up = Bitmap.createBitmap(bm, width*2, 0, width, height );
        bm_walk_up = Bitmap.createScaledBitmap(bm_walk_up, sizeOfMap, sizeOfMap, true);
        bm_walk_down = Bitmap.createBitmap(bm, 0, 0, width, height );
        bm_walk_down = Bitmap.createScaledBitmap(bm_walk_down, sizeOfMap, sizeOfMap, true);
        bm_walk_left = Bitmap.createBitmap(bm, width*6, 0, width, height );
        bm_walk_left = Bitmap.createScaledBitmap(bm_walk_left, sizeOfMap, sizeOfMap, true);
        bm_walk_right = Bitmap.createBitmap(bm, width*4, 0, width, height );
        bm_walk_right = Bitmap.createScaledBitmap(bm_walk_right, sizeOfMap, sizeOfMap, true);
        bm_current = bm_walk_right;
        setMove_right();
    }

    public void draw(Canvas canvas)
    {
        canvas.drawBitmap(this.bm_current, this.x, this.y, null);
    }

    public void update(boolean canLeft, boolean canRight, boolean canUp, boolean canDown)
    {
        if(move_right)
        {
            if (canRight)
                setX(getX()+GameView.sizeOfMap);
            bm_current = bm_walk_right;
        }
        else if (move_left)
        {
            if (canLeft)
                setX(getX()-GameView.sizeOfMap);
            bm_current = bm_walk_left;
        }
        else if (move_up)
        {
            if (canUp)
                setY(getY()-GameView.sizeOfMap);
            bm_current = bm_walk_up;
        }
        else if (move_down)
        {
            if (canDown)
                setY(getY()+GameView.sizeOfMap);
            bm_current = bm_walk_down;
        }

    }

    public Bitmap getBm()
    {
        return bm;
    }

    public void setBm(Bitmap bm)
    {
        this.bm = bm;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }


    public Rect getrBody()
    {
        return new Rect (this.x, this.y, this.x+GameView.sizeOfMap, this.y+GameView.sizeOfMap);
    }


    public Rect getrTop()
    {
        return new Rect (this.x, this.y-10*Constants.SCREEN_HEIGHT/1920, this.x+GameView.sizeOfMap, this.y);
    }


    public Rect getrBottom()
    {
        return new Rect (this.x, this.y+GameView.sizeOfMap, this.x+GameView.sizeOfMap,
                this.y+GameView.sizeOfMap+10*Constants.SCREEN_HEIGHT/1920);
    }


    public Rect getrLeft()
    {
        return new Rect (this.x-10*Constants.SCREEN_WIDTH/1080, this.y,
                this.x, this.y+GameView.sizeOfMap);
    }


    public Rect getrRight()
    {
        return new Rect (this.x+GameView.sizeOfMap, this.y,
                this.x+GameView.sizeOfMap+10*Constants.SCREEN_WIDTH/1080,
                this.y+GameView.sizeOfMap);
    }


    public boolean isMove_up()
    {
        return move_up;
    }

    public void setMove_up()
    {
        setNoMove();
        this.move_up = true;
    }

    public boolean isMove_down()
    {
        return move_down;
    }

    public void setMove_down()
    {
        setNoMove();
        this.move_down = true;
    }

    public boolean isMove_left()
    {
        return move_left;
    }

    public void setMove_left()
    {
        setNoMove();
        this.move_left = true;
    }

    public boolean isMove_right()
    {
        return move_right;
    }

    public void setMove_right()
    {
        setNoMove();
        this.move_right = true;
    }

    public void setNoMove()
    {
        this.move_down = false;
        this.move_right = false;
        this.move_up = false;
        this.move_left = false;
    }

}
