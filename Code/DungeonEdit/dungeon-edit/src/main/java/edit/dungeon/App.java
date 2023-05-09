package edit.dungeon;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.imageio.*;

import java.util.ArrayList;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.apache.commons.io.IOUtils;
import java.io.IOException;




public class App extends JPanel implements ActionListener  //, ItemListener //implements MouseListener
{
    public static final int SCREEN_WIDTH = 500;
    public static final int SCREEN_HEIGHT = 850;
    public Level level;
    public char mode ='w';
    public ArrayList <String> levelList = new ArrayList<>();

    private ArrayList<ModeButton> arrButtons = new ArrayList<>();

    private JFrame jf;
    private JMenuBar menuBar;
    private JMenu levelOptions;
    private ButtonGroup group;
    private String groupStart = "Default";

    private JFrame newLevelEnter = null;
    private JFrame traitEdit = null;
    private JTextField textField = null;

    JPopupMenu popup;
    

    App()
    {
        //Error handling - does JSON exist
        String fileName = "Levels.json";
        File jsonFile = new File(fileName);

        boolean fileExists = jsonFile.exists();

        //System.out.println("Json exists: " + fileExists);
        if(!fileExists)
        {
            Level temp = new Level();
            temp.saveNew();
        }
        

        //opens default level
        this.level = new Level("Default");

        //mouse controls
        arrButtons.add(new ModeButton(2, 'w', this.level.getWallVertImg()));
        arrButtons.add(new ModeButton(3, 'g', this.level.getGoalImg()));
        arrButtons.add(new ModeButton(4, 'p', this.level.playImg()));

        addMouseListener(new MouseAdapter() {


            public void mousePressed(MouseEvent e) {
                //System.out.println("Coord clicked: " + e.getX() + ", " + e.getY());


                boolean buttonPress = false;
                for(int i = 0; i <arrButtons.size() && !buttonPress; i++)
                {
                    ModeButton temp = arrButtons.get(i);
                    buttonPress = temp.clicked(e.getX(), e.getY());
                    if(buttonPress)
                    {
                        mode = temp.mode();
                    }
                }

                if(!buttonPress)
                {
                    int xGrid = (e.getX()-App.SCREEN_WIDTH/2+(Level.w/2)*Level.sizeOfMap)/Level.sizeOfMap;
                    int yGrid = (e.getY()-100*App.SCREEN_HEIGHT/1920)/Level.sizeOfMap;

                    //System.out.println("Translated coord: " + xGrid + ", " + yGrid);
                    switch (mode)
                    {
                        case 'w':
                            level.setWallTile(xGrid, yGrid);
                        break;
                        case 'p':
                            level.setPlayerTile(xGrid, yGrid);
                        break;
                        case 'g':
                            level.setGoalTile(xGrid, yGrid);
                        break;
                        default:
                        break;
                    }

                    level.save();
                }

                
                
                revalidate();
                repaint();


            }
        });

        createMenus();

        jf = new JFrame(level.getName());
        jf.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        
        jf.setContentPane(this);
        
        jf.setVisible(true);
        jf.setJMenuBar(menuBar);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }



    public static void main( String[] args ) throws IOException
    {
        new App();         
    }

    @Override
    public void paintComponent(Graphics g) 
    {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        level.paint(g);
        for (int i = 0; i < arrButtons.size(); i++)
        {
            arrButtons.get(i).paint(g, mode);
        }
        
    }

    public void actionPerformed(ActionEvent e)
    {
        //menu navigation actions
        for(int i = 0; i <levelList.size(); i++)
        {
            String levelName = levelList.get(i);
            if(levelName.equals(e.getActionCommand()))
            {
                updateSelectedLevel(levelName);

                jf.setTitle(levelName);
            }
        }  
    }

    public void updateSelectedLevel(String newLevel)
    {
        this.level = new Level(newLevel);

        arrButtons = new ArrayList<>();

        arrButtons.add(new ModeButton(2, 'w', this.level.getWallVertImg()));
        arrButtons.add(new ModeButton(3, 'g', this.level.getGoalImg()));
        arrButtons.add(new ModeButton(4, 'p', this.level.playImg()));

        revalidate();
        repaint();

    }
    private void createMenus()
    {
        menuBar = new JMenuBar();

        //levels JMenu
        levelOptions = new JMenu("Levels");
        levelOptions.setMnemonic(KeyEvent.VK_L);
        levelOptions.getAccessibleContext().setAccessibleDescription("Select which level to edit");

        levelList = new ArrayList<>();
        try
        {
            //creates list of level names
            String fileName = "Levels.json";

            String json = new String(Files.readAllBytes(Paths.get(fileName)), StandardCharsets.UTF_8);

            JSONObject obj = new JSONObject(json);
            JSONArray levels = obj.getJSONArray("Levels");
            JSONObject currentLevel = null;
            for (int i = 0; i < levels.length() && currentLevel == null; i++)
            {
                JSONObject temp = levels.getJSONObject(i);
                String tempName = temp.getString("LevelName");
                levelList.add(tempName);
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
            

        group = new ButtonGroup();

        //creates buttons in Levels Jmenu for each level name
        for(int i = 0; i < levelList.size(); i++)
        {
            String tempName = levelList.get(i);
            JRadioButtonMenuItem temp = new JRadioButtonMenuItem(tempName);
            temp.setActionCommand(tempName);
            temp.addActionListener(this);
            if(tempName.equals(groupStart))
                temp.setSelected(true);
            levelOptions.add(temp);
            group.add(temp);
        }

        levelOptions.addSeparator();

        //create and delete option in Levels JMenu
        JMenuItem createLevel = new JMenuItem("New");
        createLevel.setMnemonic(KeyEvent.VK_N);
        createLevel.setActionCommand("createLevel");
        createLevel.addActionListener(levelEditOptions);
        levelOptions.add(createLevel);

        JMenuItem deleteLevel = new JMenuItem("Delete");
        deleteLevel.setMnemonic(KeyEvent.VK_D);
        deleteLevel.setActionCommand("deleteLevel");
        deleteLevel.addActionListener(levelEditOptions);
        levelOptions.add(deleteLevel);


        menuBar.add(levelOptions);

        //Edit JMenu
        JMenu editOptions = new JMenu("Edit");
        editOptions.setMnemonic(KeyEvent.VK_E);
        editOptions.getAccessibleContext().setAccessibleDescription("Choose a part of the level to change the graphics of");

        JMenuItem wallTop = new JMenuItem("Wall - top");
        wallTop.setActionCommand("wallTop");
        wallTop.addActionListener(levelEditOptions);
        editOptions.add(wallTop);

        JMenuItem wallSide = new JMenuItem("Wall - side");
        wallSide.setActionCommand("wallVert");
        wallSide.addActionListener(levelEditOptions);
        editOptions.add(wallSide);

        JMenuItem floor1 = new JMenuItem("Floor tile #1");
        floor1.setActionCommand("floor1");
        floor1.addActionListener(levelEditOptions);
        editOptions.add(floor1);

        JMenuItem floor2 = new JMenuItem("Floor tile #2");
        floor2.setActionCommand("floor2");
        floor2.addActionListener(levelEditOptions);
        editOptions.add(floor2);

        JMenuItem goal = new JMenuItem("Goal");
        goal.setActionCommand("goal");
        goal.addActionListener(levelEditOptions);
        editOptions.add(goal);

        JMenuItem playerModel = new JMenuItem("Player");
        playerModel.setActionCommand("player");
        playerModel.addActionListener(levelEditOptions);
        editOptions.add(playerModel);

        menuBar.add(editOptions);

    }

    //action listener for 
    private ActionListener levelEditOptions = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if("createLevel".equals(e.getActionCommand()))
            {
                newLevelEnter = new JFrame("Enter new level name:");
                newLevelEnter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                newLevelEnter.setSize(SCREEN_WIDTH/3*2, SCREEN_HEIGHT/8);

                textField = new JTextField(20);
                newLevelEnter.add(textField);
                //textField.addActionListener(this);

                ActionListener newLevelListener = new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        String text = textField.getText();
                        boolean valid = true;
                        for(int i = 0; i < levelList.size() && valid; i++)
                        {
                            valid = !text.equals(levelList.get(i));
                        }
                        valid = valid && !text.equals("");
                        if(valid)
                        {
                            level = new Level(text);
                            level.save();

                            groupStart = text;
                            createMenus();
                            updateSelectedLevel(text);
                            jf.setJMenuBar(menuBar);
                            jf.setTitle(text);
                        }
                        newLevelEnter.dispose();
                    }
                };
                textField.addActionListener(newLevelListener);

                newLevelEnter.setVisible(true);

            }
            else if("deleteLevel".equals(e.getActionCommand()))
            {
                if(!level.getName().equals("Default"))
                {
                    level.delete();
                    level = new Level("Default");

                    groupStart = "Default";
                    createMenus();
                    updateSelectedLevel("Default");
                    jf.setJMenuBar(menuBar);
                    jf.setTitle("Default");
                }
            }
            else if("wallTop".equals(e.getActionCommand())||"floor1".equals(e.getActionCommand()) || "floor2".equals(e.getActionCommand()))
            {
                String location = "floor";
                String type = e.getActionCommand();
                editImages(location, type);            
            }
            else if("wallVert".equals(e.getActionCommand()))
            {
                String location = "wall";
                String type = "wallVert";
                editImages(location, type);            
            }
            else if("goal".equals(e.getActionCommand()))
            {
                String location = "gateways";
                String type = "goal";
                editImages(location, type);  
            }
            else if("player".equals(e.getActionCommand()))
            {
                String location = "players";
                String type = "player";
                editImages(location, type);  
                
            }
        }
    };

    //action listener that changes what action it does based on what menu it is created for
    private ActionListener varAL(String mode) 
    {
        //all action commands invoked will be file names/locations
        final String mode2 = mode;
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(mode2.equals("wallTop"))
                {
                    level.setWallTopImg(e.getActionCommand());
                }
                else if(mode2.equals("wallVert"))
                {
                    level.setWallVertImg(e.getActionCommand());
                }
                else if(mode2.equals("goal"))
                {
                    level.setGoalImg(e.getActionCommand());
                }
                else if(mode2.equals("floor1"))
                {
                    level.setFloor1Img(e.getActionCommand());
                }
                else if(mode2.equals("floor2"))
                {
                    level.setFloor2Img(e.getActionCommand());
                }
                else if(mode2.equals("player"))
                {
                    level.setPlayerImg(e.getActionCommand());
                }

                level.save();
                updateSelectedLevel(level.getName());
                
            }
        };
    }

    //create JPanel for editing parts of Levels.json
    private void editImages(String location, String type)
    {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        if(traitEdit != null)
            traitEdit.dispose();
        traitEdit = new JFrame("Select a new " + type + ":");
        traitEdit.setSize(SCREEN_WIDTH+SCREEN_WIDTH/4, SCREEN_HEIGHT/2);
        traitEdit.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try(
            final InputStream is = classloader.getResourceAsStream(location);
            final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            final BufferedReader br = new BufferedReader(isr)) 
        {
            String s = null;

            ActionListener l = varAL(type);
            JPanel panel = new JPanel(new GridLayout(0,3));


            while ((s=br.readLine()) != null)
            {
                if(s.contains(".png"))
                {
                    //invokes action commands using file names
                    Icon itemImg = new ImageIcon(ImageIO.read(classloader.getResourceAsStream(location + "/" + s)));
                    JMenuItem item = new JMenuItem(s, itemImg);
                    item.setActionCommand(location + "/" + s);
                    item.addActionListener(l);

                    panel.add(item);

                }
                
            }
            
            JScrollPane scrollPane = new JScrollPane(panel);
            scrollPane.getVerticalScrollBar().setUnitIncrement(16);
            traitEdit.add(scrollPane);
            traitEdit.setVisible(true);
        }
        catch (Exception e3)
        {
            System.out.println(e3);
        }  
    }
}

    

