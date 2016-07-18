import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by wit on 7/14/2016.
 */
public class View extends JPanel {
    int defaultIconSize;
    int[][] board; //this board is just use to paint stuff
    View(int[][] board){
        defaultIconSize = 40;
        this.board = board;
    }
    public void paintComponent(Graphics g){
        for(int i = 0; i < board[0].length; i++){
            for (int j = 0; j < board.length; j++){
                int k = board[j][i];
                Image image = new ImageIcon("asset/" +String.valueOf(k) + ".png").getImage() ;
                g.drawImage(image, j * defaultIconSize, i *defaultIconSize, defaultIconSize, defaultIconSize,this);
            }
        }
    }


    public int getDefaultIconSize(){
        return defaultIconSize;
    }
    public String boardString() {
        String boardString = "[";
        for (int i = 0; i < board.length; i++) {
            boardString += "[";
            for (int j = 0; j < board[i].length; j++) {
                boardString += board[j][i] + ",";
            }
            boardString += "]";
            if (i != board.length-1) {
                boardString += "\n";
            }
        }
        boardString += "]";
        return boardString;

    }
}
