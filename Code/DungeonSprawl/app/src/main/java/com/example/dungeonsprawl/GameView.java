package com.example.dungeonsprawl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class GameView extends View
{
    private Bitmap bmFloor1, bmFloor2, bmWallVert, bmWallTop, bmGoal, bmPlayer;
    public static int sizeOfMap = 75*Constants.SCREEN_WIDTH/1080;
    private final int h  = 21, w = 12;
    private ArrayList<FloorTile> arrFloor = new ArrayList<>();
    private ArrayList<Wall> arrWall = new ArrayList<>();
    private Goal goal;
    private PlayerModel player;
    private boolean move;
    private float mx, my;
    private Handler handler;
    private Runnable r;
    private boolean firstWin = true;

    public static Button menuButton = null;
    public static TextView successText = null;

    public GameView(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);

        int[] posX = {};
        int[] posY = {};
        int tileCharStart = 0;
        int goalX = 0;
        int goalY = 0;
        InputStream is = null;

        String levelName = Menu.levelToLoad;
        try {

            String data = "Levels.json";
            is = this.getResources().getAssets().open(data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            String json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            JSONArray levels = obj.getJSONArray("Levels");
            JSONObject currentLevel = null;
            for (int i = 0; i < levels.length() && currentLevel == null; i++)
            {
                JSONObject temp = levels.getJSONObject(i);
                String tempName = temp.getString("LevelName");
                if(tempName.equals(levelName))
                    currentLevel = temp;
            }

            String floor1Name = currentLevel.getString("Floor1PNG");

            String floor2Name = currentLevel.getString("Floor2PNG");
            String wallVertName = currentLevel.getString("WallVertPNG");
            String wallTopName = currentLevel.getString("WallTopPNG");
            String goalName = currentLevel.getString("GoalPNG");
            String playerName = currentLevel.getString("PlayerPNG");

            JSONArray wallCoords = currentLevel.getJSONArray("WallCoords");

            //wall positions

            posX = new int[wallCoords.length()];//{0,0,1};
            posY = new int[wallCoords.length()];//{1,2,3}
            for (int i = 0; i < wallCoords.length(); i++)
            {
                JSONObject temp = wallCoords.getJSONObject(i);
                posX[i] = temp.getInt("x");
                posY[i] = temp.getInt("y");
            }

            //character pos
            JSONObject characterPos = currentLevel.getJSONObject("CharacterStarPos");
            tileCharStart = characterPos.getInt("x")
                    + w * characterPos.getInt("y");

            //goal pos
            goalX = currentLevel.getJSONObject("GoalPos").getInt("x");
            goalY = currentLevel.getJSONObject("GoalPos").getInt("y");

            //load images
            is = this.getResources().getAssets().open(floor1Name);
            bmFloor1 = BitmapFactory.decodeStream(is);//9:21
            bmFloor1 = Bitmap.createScaledBitmap(bmFloor1, sizeOfMap, sizeOfMap, true);

            is = this.getResources().getAssets().open(floor2Name);
            bmFloor2 = BitmapFactory.decodeStream(is);
            bmFloor2 = Bitmap.createScaledBitmap(bmFloor2, sizeOfMap, sizeOfMap, true);

            is = this.getResources().getAssets().open(wallVertName);
            bmWallVert = BitmapFactory.decodeStream(is);
            bmWallVert = Bitmap.createScaledBitmap(bmWallVert, sizeOfMap, sizeOfMap * 2/3, true);

            is = this.getResources().getAssets().open(wallTopName);
            bmWallTop = BitmapFactory.decodeStream(is);
            bmWallTop = Bitmap.createScaledBitmap(bmWallTop, sizeOfMap, sizeOfMap, true);

            is = this.getResources().getAssets().open(goalName);
            bmGoal = BitmapFactory.decodeStream(is);
            bmGoal = Bitmap.createScaledBitmap(bmGoal, sizeOfMap, sizeOfMap, true);

            is = this.getResources().getAssets().open(playerName);
            bmPlayer = BitmapFactory.decodeStream(is);
            is.close();

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }

        //create floor tiles
        for (int i = 0; i < h; i++)
        {
            for(int j = 0; j < w; j++)
            {
                if((i+j) % 2 == 0)
                {
                    arrFloor.add(new FloorTile(bmFloor1,
                            j*sizeOfMap+Constants.SCREEN_WIDTH/2-(w/2)*sizeOfMap,
                            i*sizeOfMap+100*Constants.SCREEN_HEIGHT/1920, sizeOfMap, sizeOfMap));
                }
                else
                {
                    arrFloor.add(new FloorTile(bmFloor2,
                            j*sizeOfMap+Constants.SCREEN_WIDTH/2-(w/2)*sizeOfMap,
                            i*sizeOfMap+100*Constants.SCREEN_HEIGHT/1920, sizeOfMap, sizeOfMap));
                }
            }
        }

        //create walls
        for (int i = 0; i < posX.length; i++)
        {
            arrWall.add(new Wall(bmWallVert, bmWallTop,
                    posX[i]*sizeOfMap+Constants.SCREEN_WIDTH/2-(w/2)*sizeOfMap,
                    posY[i]*sizeOfMap+100*Constants.SCREEN_HEIGHT/1920,
                    sizeOfMap, sizeOfMap));
        }

        //create goal
        goal = new Goal(bmGoal,
                goalX*sizeOfMap+Constants.SCREEN_WIDTH/2-(w/2)*sizeOfMap,
                goalY*sizeOfMap+100*Constants.SCREEN_HEIGHT/1920,
                sizeOfMap, sizeOfMap);

        //create player
        player = new PlayerModel(bmPlayer, sizeOfMap,
                arrFloor.get(tileCharStart).getX(), arrFloor.get(tileCharStart).getY());
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        int a = event.getActionMasked();
        switch (a)
        {
            case MotionEvent.ACTION_MOVE:{
                if(!move)
                {
                    //start of motion
                    mx = event.getX();
                    my = event.getY();
                    move = true;
                }
                else
                {
                    //end of motion
                    if(mx - event.getX() > 100*Constants.SCREEN_WIDTH/1080)
                    {
                        mx = event.getX();
                        my = event.getY();
                        player.setMove_left();
                    }
                    else if(event.getX() - mx > 100*Constants.SCREEN_WIDTH/1080)
                    {
                        mx = event.getX();
                        my = event.getY();
                        player.setMove_right();
                    }
                    else if(my - event.getY() > 100*Constants.SCREEN_WIDTH/1080)
                    {
                        mx = event.getX();
                        my = event.getY();
                        player.setMove_up();
                    }
                    else if(event.getY() - my > 100*Constants.SCREEN_WIDTH/1080)
                    {
                        mx = event.getX();
                        my = event.getY();
                        player.setMove_down();
                    }
                }
                break;
            }
            case MotionEvent.ACTION_UP:{
                //release touch
                mx = 0;
                my = 0;
                move = false;
                break;
            }
        }
        return true;
    }
    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        canvas.drawColor(0xFFFFFFFF);

        //player movement code
        boolean canLeft = false;
        boolean canRight = false;
        boolean canUp = false;
        boolean canDown = false;

        //draw floor tiles, and find potential tiles player can move to
        for(int i = 0; i < arrFloor.size(); i++)
        {
            canvas.drawBitmap(arrFloor.get(i).getBm(), arrFloor.get(i).getX(),
                    arrFloor.get(i).getY(), null);

            //can only move to tiles within boundaries
            if(arrFloor.get(i).getrBody().intersect(player.getrLeft()))
                canLeft = true;
            if(arrFloor.get(i).getrBody().intersect(player.getrRight()))
                canRight = true;
            if(arrFloor.get(i).getrBody().intersect(player.getrTop()))
                canUp = true;
            if(arrFloor.get(i).getrBody().intersect(player.getrBottom()))
                canDown = true;
        }

        //wall movement rules
        for(int i = 0; i < arrWall.size(); i++)
        {
            //cannot move into a wall
            if(arrWall.get(i).getrBody().intersect(player.getrLeft()))
                canLeft = false;
            if(arrWall.get(i).getrBody().intersect(player.getrRight()))
                canRight = false;
            if(arrWall.get(i).getrBody().intersect(player.getrTop()))
                canUp = false;
            if(arrWall.get(i).getrBody().intersect(player.getrBottom()))
                canDown = false;

        }
        //draw goal
        canvas.drawBitmap(goal.getBm(), goal.getX(),goal.getY(), null);

        if(!player.getrBody().intersect(goal.getrBody()))
            player.update(canLeft, canRight, canUp, canDown);
        else if (firstWin)
        {
            //sets menu button to visable if game is won
            firstWin = false;
            menuButton.setVisibility(View.VISIBLE);
            successText.setVisibility(View.VISIBLE);
        }
        player.draw(canvas);

        //draw walls
        for(int i = 0; i < arrWall.size(); i++)
        {
            canvas.drawBitmap(arrWall.get(i).getVert(), arrWall.get(i).getX(),
                    arrWall.get(i).getY() + (sizeOfMap*1/3), null);
            canvas.drawBitmap(arrWall.get(i).getTop(), arrWall.get(i).getX(),
                    arrWall.get(i).getY() - (sizeOfMap*2/3), null);

        }

        //movement timing
        handler.postDelayed(r, 200);
    }
}
