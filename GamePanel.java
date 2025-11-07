import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
--------------------------------------------------------------
public class GamePanel extends JPanel implements ActionListener {
  private final int BOX_SIZE = 20;
  private final int GRID_WIDTH = 25, GRID_HEIGHT = 25;
  private final int PANEL_WIDTH = GRID_WIDTH * BOX_SIZE, PANEL_HEIGHT = GRID_HEIGHT * BOX_SIZE;
  private LinkedList<Point> snake;
  private Point food;
  private char direction = 'R';
  private boolean running = false;
  private javax.swing.Timer timer;

  public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
   }
  public final void startGame() {
        snake = new LinkedList<>();
        snake.add(new Point(5, 5));
        direction = 'R';
        spawnFood();
        running = true;
        timer = new javax.swing.Timer(200, this);
  }
  
  public void spawnFood() {
        Random rand = new Random();
        int x, y;
  }
  
}
