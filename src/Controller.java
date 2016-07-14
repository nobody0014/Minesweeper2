import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;
import javax.swing.*;
/**
 * Created by wit on 7/14/2016.
 */
public class Controller {
    private Model gameProcessor;
    private View boardPainter;
    private JFrame mainFrame;
    private TimeThread timeThread;
    private Container controlContainer;
    private JPanel gamePanel;
    private JPanel infoPanel;
    private GridBagLayout controlLayout;
    private GridBagLayout gameLayout;
    private GridBagConstraints gridContraints;
    private GridBagConstraints infoConstraint;
    private GridBagConstraints gameConstraint;
    private JMenuBar menuBar;
    private JMenu menu;
    private JButton newGameB;
    private JTextField markersNo;
    private JTextField timeField;
    private JMenuItem changeLevel1;
    private JMenuItem changeLevel2;
    private JMenuItem changeLevel3;
    private Toolkit tk;
    private Dimension dim;





    public void setUpFrame(){

    }
    public void setUpMenu(){
        menuBar = new JMenuBar();
        menuBar.setBorderPainted(true);
        menu = new JMenu("Change Level");

        //Changing to level one
        changeLevel1 = new JMenuItem();
        changeLevel1.setText("Level 1");
        changeLevel1.addActionListener(new ChangeLevelListener(1));

        changeLevel2 = new JMenuItem();
        changeLevel2.setText("Level 2");
        changeLevel2.addActionListener(new ChangeLevelListener(2));

        changeLevel3 = new JMenuItem();
        changeLevel3.setText("Level 3");
        changeLevel3.addActionListener(new ChangeLevelListener(3));
        PopupMenu customMenu = new PopupMenu();

        menu.add(changeLevel1);
        menu.add(changeLevel2);
        menu.add(changeLevel3);
//        menu.add(customMenu);
        menuBar.add(menu);
    }

    public void centerTheFrame(){
        //Get mid location according to its current width and height
        int midX =  dim.width/2 - mainFrame.getWidth()/2;
        int midY = dim.height/2 - mainFrame.getHeight()/2;
        //Set some screen properties
        mainFrame.setLocation(midX,midY);
    }

    public void resizeFrame(){
        if(x*45 > 450){
            mainFrame.setSize(x * 45 + 25, 150 + y*45);
        }
        else{
            mainFrame.setSize(450, 150 +y       *45);
        }
    }



    public void newGame(){
        Model.firstClick = false;
        Model.gameOver = false;
        System.out.println("Resetting.....  ");
        gameProcessor = new Model(gameProcessor.getGridX(),gameProcessor.getGridY(),gameProcessor.getNumberOfBombs());
        gamePanel.removeAll();
        gamePanel.setVisible(false);
        gameProcessor.makeBoard();
        controlContainer.add(gamePanel,gameConstraint);
        gamePanel.setVisible(true);
        System.out.println("Done");
        markersNo.setText("Markers = " + gameProcessor.getNoMarkersAvail());
        timeThread.stop();
        timeField.setText("Time = " + 0);
    }

    //Overloaded method for changing level
    public void newGame(int level){
        gameProcessor.changeLevel(level);
        newGame();
    }


    //For the menu
    private class ChangeLevelListener implements ActionListener{
        private int level;
        public ChangeLevelListener(int level){
            this.level = level;
        }
        public void actionPerformed(ActionEvent e){
            if(gameProcessor.getLevel() != level){
                newGame(level);
                resizeFrame();
                centerTheFrame();
            }
        }
    }

    //This is the class that create another thread and let it count the time
    private class TimeThread implements Runnable {
        Thread t;
        double timeStart;
        double timePrevious;
        double timeCurrent;
        int timePassed;
        boolean gameStarted;
        String name;

        //Abit of contructor
        public TimeThread(String name){
            gameStarted = false;
            this.name = name;
        }
        //Main running loop
        public void run(){
            timeStart = 0;
            timeCurrent = 0;
            timePassed = 0;
            timePrevious = 0;
            timeStart = System.nanoTime();
            while (gameStarted){
                timeCurrent = System.nanoTime();
                timePassed = (int) ((timeCurrent - timeStart)/1000000000.0);
                timeField.setText("Time = " + timePassed);
                try{
                    Thread.sleep(500);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        //Always start the class with start() because it will create a new thread and properly start it
        public void start(){
            toggleGameStarted();
            t = new Thread(this,name);
            t.start();
        }

        //Getter methods
        public int getTime(){
            return timePassed;
        }
        public boolean getGameStarted(){
            return gameStarted;
        }
        //Setter methods
        //Use this to toggle from outside
        public void stop(){
            toggleGameStarted();
        }
        //A setter that is used to toggle gameStarted
        public void toggleGameStarted(){
            if(gameStarted){
                gameStarted = false;
            }
            else{
                gameStarted = true;
            }
        }

    }
}
