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
        for(int i = 0; i < board.length; i++){
            for (int j = 0; j < board[i].length; j++){
                int k = board[j][i];
                Image image = new ImageIcon("asset/" +String.valueOf(k) + ".png").getImage() ;
                g.drawImage(image, j * defaultIconSize, i *defaultIconSize, defaultIconSize, defaultIconSize,this);
            }
        }
    }


    public int getDefaultIconSize(){
        return defaultIconSize;
    }
}
