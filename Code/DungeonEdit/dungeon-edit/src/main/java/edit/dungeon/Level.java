package edit.dungeon;

import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;


public class Level 
{
    private BufferedImage imgFloor1, imgFloor2, imgWallVert, imgWallTop, imgGoal, imgPlayer;
    public static int sizeOfMap = 75*App.SCREEN_WIDTH/1080;
    public static final int h  = 21, w = 12;

    boolean newLevel = false;

    private String levelName;
    private String floor1Name, floor2Name, wallVertName, wallTopName, goalName, playerName;

    private ArrayList<FloorTile> arrFloor = new ArrayList<>();

    private ArrayList<LevelElement> levelContents = new ArrayList<>(); 

    public Level (String levelName)
    {
        int[] posX = {};
        int[] posY = {};
        int goalX = 0;
        int goalY = 0;
        this.levelName = levelName;

        try
        {
            //read level data from file
            String fileName = "Levels.json";

            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            String json = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(json);
            JSONArray levels = obj.getJSONArray("Levels");
            JSONObject currentLevel = null;
            JSONObject defaultLevel = null;

            //finds default level data and potentially finds data for the inputted name
            for (int i = 0; i < levels.length() && currentLevel == null; i++)
            {
                JSONObject temp = levels.getJSONObject(i);
                String tempName = temp.getString("LevelName");
                if(tempName.equals(levelName))
                    currentLevel = temp;
                else if(tempName.equals("Default"))
                    defaultLevel = temp;
            }

            //creates a new Level if there is no level with that name
            if (currentLevel == null)
            {
                currentLevel = defaultLevel;
                newLevel = true;
            }
                
            //find all file locations
            floor1Name = currentLevel.getString("Floor1PNG");
            floor2Name = currentLevel.getString("Floor2PNG");

            wallVertName = currentLevel.getString("WallVertPNG");
            wallTopName = currentLevel.getString("WallTopPNG");
            goalName = currentLevel.getString("GoalPNG");
            playerName = currentLevel.getString("PlayerPNG");

            JSONArray wallCoords = currentLevel.getJSONArray("WallCoords");
            
            //creating floor tiles
            imgFloor1 = ImageIO.read(classloader.getResourceAsStream(floor1Name));
            imgFloor2 = ImageIO.read(classloader.getResourceAsStream(floor2Name));

            for (int i = 0; i < h; i++)
            {
                for (int j = 0; j < w; j++)
                {
                    if((i+j) % 2 == 0)
                    {
                        arrFloor.add(new FloorTile(imgFloor1, j, i, 
                                sizeOfMap, sizeOfMap));
                    }
                    else
                    {
                        arrFloor.add(new FloorTile(imgFloor2, j, i,
                                sizeOfMap, sizeOfMap));
                    }
                    
                }
            }

            //creating wall tiles
            imgWallVert = ImageIO.read(classloader.getResourceAsStream(wallVertName));
            imgWallTop = ImageIO.read(classloader.getResourceAsStream(wallTopName));

            //wall positions

            posX = new int[wallCoords.length()];
            posY = new int[wallCoords.length()];
            for (int i = 0; i < wallCoords.length(); i++)
            {
                JSONObject temp = wallCoords.getJSONObject(i);
                posX[i] = temp.getInt("x");
                posY[i] = temp.getInt("y");
            }
            //creating goal
            imgGoal = ImageIO.read(classloader.getResourceAsStream(goalName));
            goalX = currentLevel.getJSONObject("GoalPos").getInt("x");
            goalY = currentLevel.getJSONObject("GoalPos").getInt("y");

            //creating player
            imgPlayer = ImageIO.read(classloader.getResourceAsStream(playerName));
            JSONObject characterPos = currentLevel.getJSONObject("CharacterStarPos");
            int charX = characterPos.getInt("x");
            int charY = characterPos.getInt("y");

            //adding all level elements to levelContents Array
            for(int i = 0; i < w*h; i++)
            {
                boolean added = false;
                for(int j = 0; j <posX.length; j++)
                {
                    if (posX[j]+posY[j]*w == i)
                    {
                        levelContents.add(new Wall(posX[j], posY[j], sizeOfMap, sizeOfMap));
                        added = true;
                    }
                }
                if (goalX+goalY*w==i)
                {
                    levelContents.add(new Goal(goalX, goalY, sizeOfMap, sizeOfMap));
                }
                else if (charX+charY*w == i)
                {
                    levelContents.add(new Player(charX, charY, sizeOfMap, sizeOfMap));
                }
                else if (!added)
                {
                    levelContents.add(new LevelElement());
                }
            }
            

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        
        

    }

    public Level()
    {
        //creating default level
        //should only load if no level.json exists
        try
        {
            //basic file names for defualt
            this.levelName = "Default";
            floor1Name = "floor/tomb1.png";
            floor2Name = "floor/pedestal_full.png";
            wallVertName = "wall/brick_gray1.png";
            wallTopName = "floor/rect_gray0.png";
            goalName = "gateways/dngn_enter.png";
            playerName = "players/player0.png";

            //coordinates for goal and player
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            int goalX = 3;
            int goalY = 2;
            imgGoal = ImageIO.read(classloader.getResourceAsStream(goalName));

            int charX = 6;
            int charY = 10;
            
            //adds goals and player to level contents
            for(int i = 0; i < w*h; i++)
            {
                if (goalX+goalY*w==i)
                {
                    levelContents.add(new Goal(goalX, goalY, sizeOfMap, sizeOfMap));
                }
                else if (charX+charY*w == i)
                {
                    levelContents.add(new Player(charX, charY, sizeOfMap, sizeOfMap));
                }
                else
                {
                    levelContents.add(new LevelElement());
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        

    }

    public void paint (Graphics g)
    {
        //paint floor
        for (int i = 0; i < arrFloor.size(); i++)
        {
            arrFloor.get(i).paint(g);
        }

        //paints player, goal and wall tiles
        for (int i = 0; i < levelContents.size(); i++)
        {
            levelContents.get(i).paint(g, this);
        }
    }

    public void setWallTile(int x, int y)
    {
        //valid coord check
        if (x < w && y < h && x >= 0 && y >= 0)
        {
            //toggles wall tiles existance
            int index = x + y*w;
            if(levelContents.get(index).getClass() == LevelElement.class)
            {
                levelContents.set(x + y*w, new Wall(x, y, sizeOfMap,sizeOfMap));
            }
            else if (levelContents.get(index).getClass() == Wall.class)
            {
                levelContents.set(x + y*w, new LevelElement());
            }

        }
    }

    public void setPlayerTile(int x, int y)
    {
        //valid coord check
        if (x < w && y < h && x >= 0 && y >= 0)
        {
            int index = x + y*w;
            if(levelContents.get(index).getClass() == LevelElement.class)
            {
                //changes position if tile is blank
                for(int i = 0; i <levelContents.size(); i++)
                {
                    if (levelContents.get(i).getClass() == Player.class)
                    {
                        levelContents.set(i, new LevelElement());
                    }
                }
                levelContents.set(x + y*w, new Player(x, y, sizeOfMap, sizeOfMap));
            }   
            else if (levelContents.get(index).getClass() == Wall.class)
            {
                //sets tile as blank if wall tile
                levelContents.set(x + y*w, new LevelElement());
            }
        }
    }

    public void setGoalTile(int x, int y)
    {
        //valid coord check
        if (x < w && y < h && x >= 0 && y >= 0)
        {
            int index = x + y*w;
            if(levelContents.get(index).getClass() == LevelElement.class)
            {
                //changes position if tile is blank
                for(int i = 0; i <levelContents.size(); i++)
                {
                    if (levelContents.get(i).getClass() == Goal.class)
                    {
                        levelContents.set(i, new LevelElement());
                    }
                }
                levelContents.set(x + y*w, new Goal(x, y, sizeOfMap, sizeOfMap));
            }  
            else if (levelContents.get(index).getClass() == Wall.class)
            {
                //sets tile as blank if wall tile
                levelContents.set(x + y*w, new LevelElement());
            }
                

        }
    }

    public String getName()
    {
        return this.levelName;
    }

    public BufferedImage playImg()
    {
        //finds sub part of player image used in program
        int imgHeight = imgPlayer.getHeight();
        int imgWidth = imgPlayer.getWidth()/16;
        return imgPlayer.getSubimage(imgWidth*4, 0, imgWidth, imgHeight);

    }

    public void save()
    {
        try
        {
            //load default file
            String fileName = "Levels.json";

            String inJson = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(inJson);
            JSONArray levels = obj.getJSONArray("Levels");

            JSONObject currentLevel = levelJSON();
            JSONArray outputLevels = new JSONArray();

            if(!newLevel)
            {
                //if not a new level, replaces it's old entry with the changed one
                for(int i = 0; i < levels.length(); i++)
                {
                    JSONObject temp = levels.getJSONObject(i);
                    if(temp.getString("LevelName").equals(currentLevel.getString("LevelName")))
                        outputLevels.put(currentLevel);
                    else
                        outputLevels.put(temp);
                }
            }
            else
            {
                //if a new level, adds entry to end of list
                outputLevels = levels.put(currentLevel);
                newLevel = false;
            }

            //writes output levels to file
            try (FileWriter file = new FileWriter(fileName)) 
            {
                JSONObject output = new JSONObject()
                    .put("Levels", outputLevels);
                file.write(output.toString(4));
            } 
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        
    }
    public void delete()
    {
        try
        {
            //load default file
            String fileName = "Levels.json";

            String inJson = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(inJson);
            JSONArray levels = obj.getJSONArray("Levels");

            JSONObject currentLevel = levelJSON();
            JSONArray outputLevels = new JSONArray();

            //adds all levels except the deleted one to output
            for(int i = 0; i < levels.length(); i++)
            {
                JSONObject temp = levels.getJSONObject(i);
                if(!temp.getString("LevelName").equals(currentLevel.getString("LevelName")))
                    outputLevels.put(temp);
            }

            //writes output levels to file
            try (FileWriter file = new FileWriter(fileName)) 
            {
                JSONObject output = new JSONObject()
                    .put("Levels", outputLevels);
                file.write(output.toString(4));
            } 
            catch(Exception e)
            {
                e.printStackTrace();
            }
            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void saveNew()
    {
        try
        {
            //load default file
            String fileName = "Levels.json";

            JSONObject currentLevel = levelJSON();
            JSONArray outputLevels = new JSONArray();

            outputLevels.put(currentLevel);

            
            try (FileWriter file = new FileWriter(fileName)) 
            {
                JSONObject output = new JSONObject()
                    .put("Levels", outputLevels);
                file.write(output.toString(4));
            } catch(Exception e){
                System.out.println(e);
    
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private JSONObject levelJSON()
    {
        //creates json format of level
        try
        {
            JSONArray wallCoords = new JSONArray();
            JSONObject charPos = new JSONObject();
            JSONObject goalPos = new JSONObject();

            //finds wall coords, player coords, and goal coords
            for(int i = 0; i < levelContents.size(); i++)
            {
                LevelElement element = levelContents.get(i);
                if(element.getClass() == Wall.class)
                {
                    JSONObject temp = new JSONObject()
                        .put("x", element.getX())
                        .put("y", element.getY());
                    wallCoords.put(temp);
                }
                else if(element.getClass() == Player.class)
                {
                    charPos.put("x", element.getX())
                        .put("y", element.getY());
                }
                else if(element.getClass() == Goal.class)
                {
                    goalPos.put("x", element.getX())
                        .put("y", element.getY());
                }
            }
            return new JSONObject()
                .put("LevelName", levelName)
                .put("Floor1PNG", floor1Name)
                .put("Floor2PNG", floor2Name)
                .put("WallVertPNG", wallVertName)
                .put("WallTopPNG", wallTopName)
                .put("GoalPNG", goalName)
                .put("PlayerPNG", playerName)
                .put("WallCoords", wallCoords)
                .put("CharacterStarPos", charPos)
                .put("GoalPos", goalPos);
            
        }
        catch(JSONException e)
        {
            return null;
        }
        
    }

    
    //getters
    public BufferedImage getFloor1Img()
    {
        return imgFloor1;
    }
    public BufferedImage getFloor2Img()
    {
        return imgFloor2;
    }
    public BufferedImage getGoalImg()
    {
        return imgGoal;
    }
    public BufferedImage getWallTopImg()
    {
        return imgWallTop;
    }
    public BufferedImage getWallVertImg()
    {
        return imgWallVert;
    }
    public BufferedImage getPlayerImg()
    {
        return imgPlayer;
    }
    //setters
    public void setFloor1Img(String input)
    {
        try
        {
            this.floor1Name = input;
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            this.imgFloor1 = ImageIO.read(classloader.getResourceAsStream(input));
        }
        catch(Exception e)
        {
            System.out.println("Error setting image");
        }
        
    }
    public void setFloor2Img(String input)
    {
        try
        {
            this.floor2Name = input;
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            this.imgFloor2 = ImageIO.read(classloader.getResourceAsStream(input));
        }
        catch(Exception e)
        {
            System.out.println("Error setting image");
        }
    }
    public void setGoalImg(String input)
    {
        try
        {
            this.goalName = input;
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            this.imgGoal = ImageIO.read(classloader.getResourceAsStream(input));
        }
        catch(Exception e)
        {
            System.out.println("Error setting image");
        }
    }
    public void setWallTopImg(String input)
    {
        try
        {
            this.wallTopName = input;
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            this.imgWallTop = ImageIO.read(classloader.getResourceAsStream(input));
        }
        catch(Exception e)
        {
            System.out.println("Error setting image");
        }
    }
    public void setWallVertImg(String input)
    {
        try
        {
            this.wallVertName = input;
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            this.imgWallVert = ImageIO.read(classloader.getResourceAsStream(input));
        }
        catch(Exception e)
        {
            System.out.println("Error setting image");
        }
    }
    public void setPlayerImg(String input)
    {
        try
        {
            
            this.playerName = input;
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            this.imgPlayer = ImageIO.read(classloader.getResourceAsStream(input));
            
        }
        catch(Exception e)
        {
            System.out.println("Error setting image");
        }
    }

}
