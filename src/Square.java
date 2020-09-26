import javax.swing.*;
import java.awt.*;

/**
 * A square on the Othello GUI Each square should contain information about
 * which square it is and what is on it
 *
 * @author Jacob Caggese
 * @date 4/18/18
 * @course Concepts in Artificial Intelligence
 *
 */
public class Square extends Component {
    private int width;
    private Color boardColor = new Color(39, 118, 46);
    private Color player = boardColor;
    private int i, j;

    public Square(int i, int j) {
        width = 50;
        setPreferredSize(new Dimension(50, 50));
        this.i = i;
        this.j = j;
    }

    public void setWhite() {
        player = Color.WHITE;
    }

    public void setBlack() {
        player = Color.BLACK;
    }

    public void resetColor() {player = boardColor;}

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void paint(Graphics g) {
        // cleans the screen
        super.paint(g);

        // set Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, width);

        g.setColor(boardColor);
        g.fillRect(0, 0, width - 1, width - 1);

        g.setColor(player);
        g.fillOval(0, 0, width - 1, width - 1);
    }

}
