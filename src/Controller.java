import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
/**
 * Created by wit on 7/14/2016.
 */
public class Controller {
    //instantiate the instance var first, some can be converted to local var but ill leave them here
    private Model gameModel;
    private View gamePanel;
    private JFrame mainFrame;
    private TimeThread timeThread;
    private Container controlContainer;
    private JPanel infoPanel;
    private GridBagLayout controlLayout;
    private GridBagLayout gameLayout;
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
    private JMenuItem changeCustomLevel;
    private Toolkit tk;
    private Dimension dim;
    private boolean mouseIsPressed;

    public Controller(){
        //set mouseispressed, create gameModel, create gamePanel, create timeThread
        mouseIsPressed = false;
        gameModel = new Model();
        gamePanel = new View(gameModel.getBoard());
        timeThread = new TimeThread("Timer");
    }

    public void setUpFrame(){
        //This is gotten from a youtube video when I was trying the centralized the frame
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

    //set the gamePanel into the controlContainer, also add 2 listeners
    public void setGamePanel(){
        gameLayout = new GridBagLayout();
        gameConstraint = new GridBagConstraints();
        gamePanel.setLayout(gameLayout);
        gameConstraint.gridx = 0;
        gameConstraint.gridy = 1;
        gameConstraint.ipady = 40*gameModel.getGridY();
        gameConstraint.ipadx = 40*gameModel.getGridX();
        gamePanel.addMouseListener(new PanelListener());
        gamePanel.addMouseMotionListener(new mouseMotion());
        controlContainer.add(gamePanel,gameConstraint);
    }

    //setting up menus and its options
    public void setUpMenu(){
        //create menu and menut bar, also paint the menuBar
        menuBar = new JMenuBar();
        menuBar.setBorderPainted(true);
        menu = new JMenu("Change Level");

        //create level one
        changeLevel1 = new JMenuItem();
        changeLevel1.setText("Level 1");
        changeLevel1.addActionListener(new ChangeLevelListener(1));

        //create level two
        changeLevel2 = new JMenuItem();
        changeLevel2.setText("Level 2");
        changeLevel2.addActionListener(new ChangeLevelListener(2));

        //create level 3
        changeLevel3 = new JMenuItem();
        changeLevel3.setText("Level 3");
        changeLevel3.addActionListener(new ChangeLevelListener(3));

        //create custom level
        changeCustomLevel = new JMenuItem();
        changeCustomLevel.setText("Custom Level");
        changeCustomLevel.addActionListener(new ChangeCustomLevelListener());

        //add all of them into menu and then add menu into menubar
        menu.add(changeLevel1);
        menu.add(changeLevel2);
        menu.add(changeLevel3);
        menu.add(changeCustomLevel);
        menuBar.add(menu);
    }

    //Centering the main frame code edited from a youtube vid
    public void centerTheFrame(){
        //Get mid location according to its current width and height
        int midX =  dim.width/2 - mainFrame.getWidth()/2;
        int midY = dim.height/2 - mainFrame.getHeight()/2;
        //Set some screen properties
        mainFrame.setLocation(midX,midY);
    }

    //for centerring other frame
    public void centerTheFrame(int w, int h, JFrame k){
        //Get mid location according to its current width and height
        int midX =  dim.width/2 - w/2;
        int midY = dim.height/2 - h/2;
        //Set some screen properties
        k.setLocation(midX,midY);
    }

    //resizing frame
    public void resizeFrame(){
        if(gameModel.getGridX()*45 > 450){
            mainFrame.setSize(gameModel.getGridX() * 45 + 25, 150 + gameModel.getGridY()*45);
        }
        else{
            mainFrame.setSize(450, 150 + gameModel.getGridY()   *45);
        }
    }

    //create a new game
    public void newGame(){
        //reset the cursor in case in the hint mode
        mainFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        //call the gameModel to reset everything
        gameModel.resetGameState();
        System.out.println("Resetting.....  ");
        //call new board in gameModel
        gameModel.newBoard();
        System.out.println("Done");
        //reset the markersNo
        markersNo.setText("Markers = " + gameModel.getNoMarkersAvail());
        //Stop the timeThread and reset the field
        timeThread.stop();
        timeField.setText("Time = " + 0);
        //repaint the board since the board is reset
        gamePanel.repaint();
    }

    //We have to convert the position we click in the gamePanel to the legit one that the board use
    public int[] coordToBoardLocation(int coordX, int coordY){
        int[] pos = new int[2];
        pos[0] = coordX / gamePanel.getDefaultIconSize();
        pos[1] = coordY / gamePanel.getDefaultIconSize();
        return pos;

    }

    //mouseMotion to add into the gamePanel to detect
    //this class is for dragging of mouse when pressing
    private class mouseMotion implements MouseMotionListener{
        @Override
        public void mouseDragged(MouseEvent e) {
            //keep resetting grid back to the unshown icon and reset it
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

    //Listen to mouse clicks in the gamePanel
    private class PanelListener implements MouseListener{
        public void mouseClicked(MouseEvent e){}
        public void mousePressed(MouseEvent e){
            int[] pos = coordToBoardLocation(e.getX(),e.getY());
            //
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
    public void newGame(int x, int y, int noBombs){
        gameModel.changeLevel(x,y,noBombs);
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

    //for hint mode, if click anywhere in mainframe reset the hint mode
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
    //for hint button, clicking on this change the cursor and set the hint mode to true
    private class HintListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            if(!gameModel.getHintMode()){
                gameModel.setHintMode(true);
                mainFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
        }
    }

    //For the menu
    //This is for the predefined levels
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

    //Creating another frame to handle the customlevel
    //This also disable the mainFrame in order to make the game unplayable while setting
    private class ChangeCustomLevelListener implements ActionListener{
        JLabel mainLabel;
        JFrame changeLevelFrame;
        JLabel widthLabel;
        JTextField widthField;
        JLabel heightLabel;
        JTextField heightField;
        JLabel noBombsLabel;
        JTextField noBombsField;
        JButton enterButton;

        public void actionPerformed(ActionEvent e){
            //disable the mainframe
            mainFrame.setEnabled(false);
            //this following lines set new frame, add windowlistener and make the close button do nothing
            changeLevelFrame = new JFrame();
            changeLevelFrame.addWindowListener(new changeLevelWindowListener());
            changeLevelFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            //Customized the frame
            changeLevelFrame.setName("Custom Level");
            changeLevelFrame.setSize(new Dimension(200,150));
            changeLevelFrame.setLayout(new GridBagLayout());
            GridBagConstraints changeLevelConstraint = new GridBagConstraints();

            mainLabel = new JLabel("Custom Level");
            changeLevelConstraint.gridy = 0;
            changeLevelConstraint.gridx = 0;
            changeLevelFrame.add(mainLabel,changeLevelConstraint);

            widthLabel = new JLabel("X:");
            changeLevelConstraint.gridy = 1;
            changeLevelConstraint.gridx = 0;
            changeLevelFrame.add(widthLabel,changeLevelConstraint);

            widthField = new JTextField(5);
            changeLevelConstraint.gridy = 1;
            changeLevelConstraint.gridx = 1;
            changeLevelFrame.add(widthField,changeLevelConstraint);

            heightLabel = new JLabel("Y:");
            changeLevelConstraint.gridy = 2;
            changeLevelConstraint.gridx = 0;
            changeLevelFrame.add(heightLabel,changeLevelConstraint);

            heightField = new JTextField(5);
            changeLevelConstraint.gridy = 2;
            changeLevelConstraint.gridx = 1;
            changeLevelFrame.add(heightField,changeLevelConstraint);

            noBombsLabel = new JLabel("Bombs:");
            changeLevelConstraint.gridy = 3;
            changeLevelConstraint.gridx = 0;
            changeLevelFrame.add(noBombsLabel,changeLevelConstraint);

            noBombsField = new JTextField(5);
            changeLevelConstraint.gridy = 3;
            changeLevelConstraint.gridx = 1;
            changeLevelFrame.add(noBombsField,changeLevelConstraint);

            enterButton = new JButton();
            enterButton.setText("Done");
            enterButton.addActionListener(new EnterButtonListener());
            changeLevelConstraint.gridy = 4;
            changeLevelConstraint.gridx = 1;
            changeLevelFrame.add(enterButton,changeLevelConstraint);

            centerTheFrame(changeLevelFrame.getWidth(),changeLevelFrame.getHeight(),changeLevelFrame);
            changeLevelFrame.setVisible(true);

        }
        //thank you stack overflow for telling me this class, best class ever
        private class changeLevelWindowListener implements WindowListener {
            //Only care when window is closing
            public void windowClosing(WindowEvent e) {
                //set the visibility to false before disposing it then set mainframe to be enabled, thats the purpose of this class
                changeLevelFrame.setVisible(false);
                changeLevelFrame.dispose();
                mainFrame.setEnabled(true);
            }
            public void windowClosed(WindowEvent e) {}
            public void windowActivated(WindowEvent e) {}
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}
        }

        //This is where things gets crappy
        //my enterbutton listener cant do anything to the changeCustomLevelframe if it cant access to the content
        //therefore i have to make this thing a class within class within a class
        private class EnterButtonListener implements ActionListener{
            public void actionPerformed(ActionEvent e){
                if(heightField.getText().length() > 0 && widthField.getText().length() > 0 && noBombsField.getText().length() > 0){
                    int x = Integer.valueOf(widthField.getText());
                    int y = Integer.valueOf(heightField.getText());
                    int noBombs = Integer.valueOf(noBombsField.getText());
                    //Placing the restriction on the custom levels that the user can put
                    if(x > 3 && y > 3 && noBombs < x*y -9){
                        try{
                            //remove the gamePanel first
                            controlContainer.remove(gamePanel);
                            //set the gameboard and call new game
                            newGame(x,y,noBombs);
                            //get new gamePanel
                            gamePanel = new View(gameModel.getBoard());
                            //put gamepanel in the properplace
                            setGamePanel();
                            //resize the frame
                            resizeFrame();
                            //center it
                            centerTheFrame();

                            //then make the mainframe usuable againa and make the change levelFrame not visible and dispose it
                            mainFrame.setEnabled(true);
                            changeLevelFrame.setVisible(false);
                            changeLevelFrame.dispose();
                        }catch (Exception exp){
                            System.out.println("The value input is not valid");
                        }
                    }
                    else{
                        //pop up to show error
                        JOptionPane.showMessageDialog(changeCustomLevel, "Invalid Inputs");
                    }

                }

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
            //each run just reset all 4 time except time start
            timeStart = 0;
            timeCurrent = 0;
            timePassed = 0;
            timePrevious = 0;
            //time start assigned to System.nantime
            timeStart = System.nanoTime();
            //Main timer loop
            while (gameStarted){
                //timecurrent is given nanotime
                timeCurrent = System.nanoTime();
                //TimePassed is timecurrent - timestart
                timePassed = (int) ((timeCurrent - timeStart)/1000000000.0);
                //you have to set the timefield each run
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
