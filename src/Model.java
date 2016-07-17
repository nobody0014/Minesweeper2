import java.util.HashSet;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.geom.*;
import javax.swing.*;
/**
 * Created by wit on 7/14/2016.
 */
public class Model {
    public static boolean firstClick = false;
    public static boolean gameOver = false;
    public HashSet<int[]> revealedArea;
    private HashSet<int[]> bombPos;
    private HashSet<int[]> noBombArea;
    private HashSet<int[]> positionFilled;
    private int[][] board;
    private int level;
    private int x;
    private int y;
    private int noBombs;
    private int noMarkersAvail;
    private int noBombsMarked;
    public Model(){
        level = 1;
        x = 9;
        y = 9;
        noBombs = 10;
        noMarkersAvail = 10;
        noBombsMarked = 0;
        board = new int[x][y];
        bombPos = new HashSet<>();
        positionFilled = new HashSet<>();
        revealedArea = new HashSet<>();
        newBoard();
    }
    public Model(int x, int y, int bombs){
        level = 0; //Custom make level become 0
        this.x = x;
        this.y = y;
        noBombs = bombs;
        noMarkersAvail = bombs;
        noBombsMarked = 0;
        board = new int[x][y];
        bombPos = new HashSet<>();
        positionFilled = new HashSet<>();
        revealedArea = new HashSet<>();
        newBoard();
    }

    public void newBoard(){
        for(int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                board[j][i] = 20;
            }
        }
    }

    public void changeLevel(int lvl){
        //0 is the Height
        //1 is the Width
        //2 is the number of bombs
        if(lvl == 1){
            level = lvl;
            x = 9;
            y = 9;
            noBombs = 10;
            noMarkersAvail = 10;
        }
        else if(lvl == 2){
            level = lvl;
            x = 16;
            y = 16;
            noBombs = 40;
            noMarkersAvail = 40;
        }
        else if(lvl == 3){
            level = lvl;
            x = 30;
            y = 16;
            noBombs = 99;
            noMarkersAvail = 99;
        }
        board = new int[x][y];
    }
    public void changeLevel(int x, int y, int bombs){
        this.x = x;
        this.y = y;
        noBombs = bombs;
        noMarkersAvail = bombs;
        board = new int[x][y];
    }

    public void gridPressed(int x, int y){

        if(board[x][y] >= 80 && board[x][y] <= 89 ){
            board[x][y] = board[x][y]%80 + 130;
        }
        else if(board[x][y] == 20){
            int[] firstClickPos = new int[2];
            firstClickPos[0] = x;
            firstClickPos[1] = y;
            setUpBoard(firstClickPos);
        }
        board[x][y] = board[x][y]%80 + 130;
    }


    public void setUpBoard(int[] firstClickPos){
        if(!firstClick){
            System.out.println("Setting up no bombs area");
            setNoBombArea(firstClickPos);
            System.out.println("Put in bombs");
            setBombs();
            System.out.println("Complete");
            System.out.println("Put in Numbers and Empties");
            setNumber();
            System.out.println("Complete");
        }
        else{
            System.out.println("Game has already started");
        }
    }
    private void setNoBombArea(int[] pos){
        noBombArea = getAllDirection(pos);
        // I do not want any bomb to be beside the point where the user start clicking because it might result in
        // first click death, so i create an area to ward that off
        noBombArea.add(pos);
    }
    private void setBombs(){
        //create a Random object
        Random rand = new Random();
        int posX;
        int posY;
        while (bombPos.size() < noBombs){
            //Since we are using objects, we have to create new objects for every coordinates
            int[] coor = new int[2];
            posX = rand.nextInt(x-1);
            posY = rand.nextInt(y-1);
            coor[0] = posX;
            coor[1] = posY;
            contains(bombPos,coor);
            if(!contains(bombPos,coor) && !contains(noBombArea,coor)){
                bombPos.add(coor);
                board[posX][posY] = 89;
                positionFilled.add(coor);
            }
        }
    }
    //Use this after obtaining the bombs coordinates
    private void setNumber(){
        ArrayList<int[]> numPos = new ArrayList<>();
        for(int[] i: bombPos){
            HashSet<int[]> areaSet = getAllDirection(i);
            for (int[] j: areaSet){
                if(checkValidPos(j)){
                    numPos.add(j);
                }
            }
        }
        System.out.println("Done");

        for (int[] i : numPos){
            positionFilled.add(i);
            if(board[i[0]][i[1]]  == 20){
                board[i[0]][i[1]] = countCoord(numPos,i) + 80;
                System.out.println(board[i[0]][i[1]] + " " +countCoord(numPos,i));
            }
        }
        for(int i = 0; i < x; i++){
            for (int j = 0; j < y; j++){
                if(board[i][j] == 20){
                    board[i][j] = 80;
                }
            }
        }
    }

    private boolean contains(Set<int[]> aSet, int[] i){
        boolean checker = false;
        for(int[] someArray: aSet){
            checker = evaluate(someArray,i);
            if(checker){
                break;
            }
        }
        return checker;
    }
    private boolean contains(ArrayList<int[]> aList, int[] i){
        boolean checker = false;
        for(int[] someArray: aList){
            checker = evaluate(someArray,i);
            if(checker){
                break;
            }
        }
        return checker;
    }

    //Evaluating Coordinate if they are the same or not (true if same) (false if not same)
    private boolean evaluate(int[] i, int[] j){
        boolean check =  (i[0] == j[0] && i[1] == j[1]);
        if(i[0] == j[0] && i[1] == j[1]){
            return true;
        }
        return false;
    }
    private boolean checkValidPos(int pos[]){
        if(pos[0] < 0 || pos[0] >= x || pos[1] < 0 || pos[1] >= y){
            return false;
        }
        return true;
    }
    private int countCoord(ArrayList<int[]> coords, int[] i){
        int count = 0;
        for(int[] j: coords){
            if(evaluate(i,j)){
                count++;
            }
        }
        return count;
    }
    private HashSet<int[]> getAllDirection(int[] pos){
        //Due to the structure of how objects work, we have to crete new one everytime
        //Otherwise we would be changing the value of the same object over and over again
        int[] t = new int[2];
        int[] tR = new int[2];
        int[] tL = new int[2];
        int[] l = new int[2];
        int[] r = new int[2];
        int[] b = new int[2];
        int[] bR = new int[2];
        int[] bL = new int[2];

        //We use the int so that we would not mess with the actual object in bombPos
        int posX = pos[0];
        int posY = pos[1];
        t[0] = posX;
        t[1] = posY - 1;
        tR[0] = posX + 1;
        tR[1] = posY - 1;
        tL[0] = posX - 1;
        tL[1] = posY - 1;
        l[0] = posX - 1;
        l[1] = posY;
        r[0] = posX + 1;
        r[1] = posY;
        b[0] = posX;
        b[1] = posY + 1;
        bR[0] = posX + 1;
        bR[1] = posY + 1;
        bL[0] = posX - 1;
        bL[1] = posY + 1;
        HashSet<int[]> allPos = new HashSet<>();
        allPos.add(t);
        allPos.add(tR);
        allPos.add(tL);
        allPos.add(b);
        allPos.add(bR);
        allPos.add(bL);
        allPos.add(l);
        allPos.add(r);
        return  allPos;
    }



    public Set<int[]> getAreaToReveal(int [] pos, Set<int[]> area){
        if(!checkValidPos(pos)){
            return area;
        }
        else if(board[pos[0]][pos[1]] == 0 ){
            HashSet<int[]> directions = getAllDirection(pos);
            area.add(pos);
            for (int[] i: directions){
                if(checkValidPos(i)){
                    if(board[i[0]][i[1]] == 99 || contains(revealedArea,i) || contains(area,i)){
                        continue;
                    }
                    area.add(i);
                    Set<int[]> results = getAreaToReveal(i,area);
                    for (int[] each: results ){
                        area.add(each);
                    }
                }
            }
            return area;
        }
        else {
            area.add(pos);
            return area;
        }
    }
    //Some getter methods for only a few necessary variables
    public void addNoBombsMarked(){noBombsMarked++;}
    public void minusNoBombsMarked(){noBombsMarked--;}
    public void addNoMarkersAvail(){ noMarkersAvail++;}
    public void minusNoMarkersAvail(){ noMarkersAvail--;}
    public void setLevel(int lvl){
        if(lvl > 3 || lvl < 1){
            System.out.println("Invalid preset level");
        }
        else{
            changeLevel(lvl);
        }
    }


    //Getter methods start here
    //Get the board back but not the string part.
    public int[][] getBoard(){
        return board;
    }

    //Get Height
    public int getGridY(){
        return y;
    }

    //Get Width
    public int  getGridX(){
        return x;
    }

    //Get NumberOfBombs
    public int getNumberOfBombs(){
        return noBombs;
    }

    //Get no of bombs that are marked
    public int getNoBombsMarked(){ return noBombsMarked;}

    //Get no of markers that are available
    public int getNoMarkersAvail(){ return noMarkersAvail;}

    //Get level
    public int getLevel(){ return level;}

    //Get the state of the board (in string form).
    public String boardString() {
        String boardString = "[";
        for (int i = 0; i < y; i++) {
            boardString += "[";
            for (int j = 0; j < x; j++) {
                boardString += board[j][i] + ",";
            }
            boardString += "]";
            if (i != x - 1) {
                boardString += "\n";
            }
        }
        boardString += "]";
        return boardString;

    }

}
