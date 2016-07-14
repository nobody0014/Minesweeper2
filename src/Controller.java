import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;
import javax.swing.*;
/**
 * Created by wit on 7/14/2016.
 */
public class Controller {
    private Model gameModel;
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




    public Controller(){
        gameModel = new Model();
        boardPainter = new View();
    }
    public void setUpFrame(){
        int x = gameModel.getGridX();
        int y = gameModel.getGridY();
        tk =  Toolkit.getDefaultToolkit();
        dim = tk.getScreenSize();

        mainFrame = new JFrame("Main");


        //Call resize frame to set size for you
        resizeFrame();

        //Set up menuBar and add its item
        setUpMenu();
        mainFrame.setJMenuBar(menuBar);


        //Set some screen properties
        centerTheFrame();
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);


        controlContainer = mainFrame.getContentPane();
        controlContainer.setSize(mainFrame.getWidth(),mainFrame.getHeight());
        controlLayout = new GridBagLayout();
        controlContainer.setLayout(controlLayout);

        //Creating info panel to keep all the stuff on top of the frame
        infoPanel = new JPanel();
        infoConstraint = new GridBagConstraints();
        infoConstraint.gridy = 0;
        infoConstraint.fill = GridBagConstraints.HORIZONTAL;
        infoConstraint.anchor = GridBagConstraints.PAGE_START;
        controlContainer.add(infoPanel,infoConstraint);

        //this constraint is used for all the components in the infopanel
        GridBagConstraints innerInfor = new GridBagConstraints();

        //Creating and setting the field that has the number of markers
        markersNo = new JTextField();
        markersNo.setPreferredSize(new Dimension(80,30));
        markersNo.setLayout(new GridBagLayout());
        markersNo.setText("Markers = " + gameModel.getNoMarkersAvail());
        innerInfor.gridx = 0;
        infoPanel.add(markersNo,innerInfor);

        //Creating the new game button
        newGameB = new JButton();
        newGameB.addActionListener(new NewGameListener());
        newGameB.setPreferredSize(new Dimension(50,50));
        newGameB.setText("NG");
        newGameB.setLayout(new GridBagLayout());
        innerInfor.gridx = 1;
        infoPanel.add(newGameB,innerInfor);

        //Creating the field to show how much time has elapsed since the game started
        timeField = new JTextField();
        timeField.setPreferredSize(new Dimension(80,30));
        timeField.setLayout(new GridBagLayout());
        timeField.setText("Time = " + 0);
        innerInfor.gridx = 2;
        infoPanel.add(timeField,innerInfor);

        infoPanel.setVisible(true);

        //The actual game area creation
        gamePanel = new JPanel();
        gameLayout = new GridBagLayout();
        gameConstraint = new GridBagConstraints();
        gamePanel.setLayout(gameLayout);
        gameConstraint.gridy = 1;
        gameConstraint.anchor = GridBagConstraints.CENTER;
        gameConstraint.fill = GridBagConstraints.BOTH;
//        gameConstraint.ipady = GC.getGridY()*45;
        controlContainer.add(gamePanel,gameConstraint);

        //Only the game and info panel are needed to be added into controlcontainer

        //Make the actual game grid

        mainFrame.setVisible(true);

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
        if(gameModel.getGridX()*45 > 450){
            mainFrame.setSize(gameModel.getGridX() * 45 + 25, 150 + gameModel.getGridY()*45);
        }
        else{
            mainFrame.setSize(450, 150 + gameModel.getGridY()   *45);
        }
    }



    public void newGame(){
        Model.firstClick = false;
        Model.gameOver = false;
        System.out.println("Resetting.....  ");
        gameModel = new Model(gameModel.getGridX(),gameModel.getGridY(),gameModel.getNumberOfBombs());
        gamePanel.removeAll();
        gamePanel.setVisible(false);
//        gameProcessor.makeBoard();
        controlContainer.add(gamePanel,gameConstraint);
        gamePanel.setVisible(true);
        System.out.println("Done");
        markersNo.setText("Markers = " + gameModel.getNoMarkersAvail());
        timeThread.stop();
        timeField.setText("Time = " + 0);
    }



    public void makeGrid(){

    }
    public void makeGrid(int[][] board){

    }


    private class FirstButtonClickListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(!Model.firstClick){
                int[] pos = (int[]) e.getSource();
                System.out.println("Set up new board");
                gameModel.setUpBoard(pos);
                System.out.println("Done");
                System.out.println("Put in the UI");
                makeGrid(gameModel.getBoard());
                System.out.println("Done");
                Model.firstClick = true;
                System.out.println(gameModel.boardString());
                System.out.println("Revealing the opening moves");
                System.out.println("Getting area to reveal");
                Set<int[]> someArea = gameModel.getAreaToReveal(pos, new HashSet<>());
                someArea.add(pos);
                System.out.println("Done");
                for (int[] i: someArea){
                    System.out.println(Arrays.toString(i));
                    gameModel.revealedArea.add(i);
//                    gameModel[i[0]][i[1]].reveal();
                }
                System.out.println("Done");
                timeThread.start();
            }
        }
    }
    //Overloaded method for changing level
    public void newGame(int level){
        gameModel.changeLevel(level);
        newGame();
    }

    //For the new game button, reset the board
    private class NewGameListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(Model.firstClick){
                newGame();
            }
        }
    }


    //For the menu
    private class ChangeLevelListener implements ActionListener{
        private int level;
        public ChangeLevelListener(int level){
            this.level = level;
        }
        public void actionPerformed(ActionEvent e){
            if(gameModel.getLevel() != level){
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
