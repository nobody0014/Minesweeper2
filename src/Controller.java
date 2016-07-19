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
    private View gamePanel;
    private JFrame mainFrame;
    private TimeThread timeThread;
    private Container controlContainer;
    private JPanel infoPanel;
    private GridBagLayout controlLayout;
    private GridBagLayout gameLayout;
    private GridBagConstraints gridContraints;
    private GridBagConstraints infoConstraint;
    private GridBagConstraints gameConstraint;
    private JMenuBar menuBar;
    private JMenu menu;
    private JButton newGameB;
    private JButton hintB;
    private JTextField markersNo;
    private JTextField timeField;
    private JMenuItem changeLevel1;
    private JMenuItem changeLevel2;
    private JMenuItem changeLevel3;
    private Toolkit tk;
    private Dimension dim;
    private boolean mouseIsPressed;




    public Controller(){
        mouseIsPressed = false;
        gameModel = new Model();
        gamePanel = new View(gameModel.getBoard());
        timeThread = new TimeThread("Timer");
    }
    public void setUpFrame(){
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
        infoConstraint.anchor = GridBagConstraints.LINE_START;
        controlContainer.add(infoPanel,infoConstraint);

        //this constraint is used for all the components in the infopanel
        GridBagConstraints innerInfor = new GridBagConstraints();

        //Creating and setting the field that has the number of markers
        markersNo = new JTextField();
        markersNo.setPreferredSize(new Dimension(85,30));
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

        hintB = new JButton();
        innerInfor.anchor = GridBagConstraints.FIRST_LINE_END;
        hintB.setText("Hint");
        hintB.addActionListener(new HintListener());
        infoPanel.add(hintB,innerInfor);


        infoPanel.setVisible(true);

        //The actual game area creation
        setGamePanel();


        //Make the actual game grid
        mainFrame.addMouseListener(new MainFrameListener());
        mainFrame.setVisible(true);

    }
    public void setGamePanel(){
        gameLayout = new GridBagLayout();
        gameConstraint = new GridBagConstraints();
        gamePanel.setLayout(gameLayout);
        gameConstraint.gridx = 0;
        gameConstraint.gridy = 1;
        gameConstraint.ipady = 40*gameModel.getGridY();
        gameConstraint.ipadx = 40*gameModel.getGridX();
        gameConstraint.fill = GridBagConstraints.CENTER;
        gameConstraint.fill = GridBagConstraints.BOTH;
        gamePanel.addMouseListener(new PanelListener());
        gamePanel.addMouseMotionListener(new mouseMotion());
        controlContainer.add(gamePanel,gameConstraint);
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
        mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        gameModel.resetGameState();
        System.out.println("Resetting.....  ");
        gameModel.newBoard();
        System.out.println("Done");
        markersNo.setText("Markers = " + gameModel.getNoMarkersAvail());
        timeThread.stop();
        timeField.setText("Time = " + 0);
        gamePanel.repaint();
    }


    public int[] coordToBoardLocation(int coordX, int coordY){
        int[] pos = new int[2];
        pos[0] = coordX / gamePanel.getDefaultIconSize();
        pos[1] = coordY / gamePanel.getDefaultIconSize();
        return pos;

    }

    private class mouseMotion implements MouseMotionListener{
        @Override
        public void mouseDragged(MouseEvent e) {
            if(mouseIsPressed){
                gameModel.resetGridPressed();
                int[] pos = coordToBoardLocation(e.getX(),e.getY());
                gameModel.gridPressed(pos[0], pos[1]);
                gamePanel.repaint();
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {}
    }

    private class PanelListener implements MouseListener{
        public void mouseClicked(MouseEvent e){}
        public void mousePressed(MouseEvent e){
            int[] pos = coordToBoardLocation(e.getX(),e.getY());
            if(!gameModel.getGameOver()){
                if(SwingUtilities.isLeftMouseButton(e)){
                    mouseIsPressed = true;
                    gameModel.gridPressed(pos[0],pos[1]);
                }
                else if(SwingUtilities.isRightMouseButton(e)){
                    gameModel.toggleMarking(pos[0],pos[1]);
                    markersNo.setText("Markers = " + gameModel.getNoMarkersAvail());
                }
                gamePanel.repaint();
            }
        }
        public void mouseReleased(MouseEvent e) {
            int[] pos = coordToBoardLocation(e.getX(),e.getY());
            if(SwingUtilities.isLeftMouseButton(e)){
                if(!gameModel.getFirstClick()){
                    gameModel.setUpBoard(pos);
                    timeThread.start();
                    gameModel.setFirstClick(true);
                }
                else if(!gameModel.getGameOver()){
                    gameModel.reveal(pos[0],pos[1]);
                }
                if(gameModel.getNumbersLeft() == 0){
                    gameModel.setGameOver(true);
                    gameModel.markAllBombs();
                }
                if(gameModel.getGameOver()){
                    timeThread.stop();
                }
                if(gameModel.getHintMode()){
                    gameModel.setHintMode(false);
                    mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                mouseIsPressed = false;
            }
            gamePanel.repaint();
        }
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
    }
    //Overloaded method for changing level
    public void newGame(int level){
        gameModel.changeLevel(level);
        newGame();
    }

    //For the new game button, reset the board
    private class NewGameListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(gameModel.getFirstClick()){
                newGame();
            }
        }
    }

    private class MainFrameListener implements MouseListener{
        public void mouseClicked(MouseEvent e){}
        public void mousePressed(MouseEvent e){}
        public void mouseReleased(MouseEvent e) {
            if(SwingUtilities.isLeftMouseButton(e)){
                if(gameModel.getHintMode()){
                    gameModel.setHintMode(false);
                    mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }
        public void mouseEntered(MouseEvent e){}
        public void mouseExited(MouseEvent e){}
    }
    private class HintListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(!gameModel.getHintMode()){
                gameModel.setHintMode(true);
                mainFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                controlContainer.remove(gamePanel);
                newGame(level);
                gamePanel = new View(gameModel.getBoard());
                setGamePanel();
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
            gameStarted = true;
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
