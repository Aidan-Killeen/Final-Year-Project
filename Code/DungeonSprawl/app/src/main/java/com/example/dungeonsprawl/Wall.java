package com.example.dungeonsprawl;

import android.graphics.Bitmap;
import android.graphics.Rect;

public class Wall
{
    private Bitmap vert, top;
    private int x, y, width, height;

    public Wall(Bitmap vert, Bitmap top, int x, int y, int width, int height)
    {
        this.vert = vert;
        this.top = top;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Bitmap getVert() {
        return vert;
    }

    public void setVert(Bitmap vert) {
        this.vert = vert;
    }

    public Bitmap getTop() { return top; }

    public void setTop(Bitmap top) { this.top = top; }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Rect getrBody()
    {
        return new Rect (this.x, this.y, this.x+GameView.sizeOfMap, this.y+GameView.sizeOfMap);
    }
}
