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
        timer.start();
  }
  
  public void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(GRID_WIDTH);
            y = rand.nextInt(GRID_HEIGHT);
            food = new Point(x, y);
        }
        while (snake.contains(food));
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
      if (running) {
          move();
          repaint();
      }
  }

   private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!running && e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGame();
                repaint();
            } else if (running) {
                char prev = direction;
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        if (prev != 'R') direction = 'L'; break;
                    case KeyEvent.VK_RIGHT:
                        if (prev != 'L') direction = 'R'; break;
                    case KeyEvent.VK_UP:
                        if (prev != 'D') direction = 'U'; break;
                    case KeyEvent.VK_DOWN:
                        if (prev != 'U') direction = 'D'; break;
                }
            }
