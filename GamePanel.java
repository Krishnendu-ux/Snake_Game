import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener {
  private final int BOX_SIZE = 20;
  private final int GRID_WIDTH = 25, GRID_HEIGHT = 25;
  private final int PANEL_WIDTH = GRID_WIDTH * BOX_SIZE, PANEL_HEIGHT = GRID_HEIGHT * BOX_SIZE;
  private LinkedList<Point> snake;
  private Point food;
  private char direction = 'R';
  private boolean running = false;
  private javax.swing.Timer timer;

  private int score = 0; //saap kitna seb khaya uska tracker 

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

  public void move() {
        Point head = snake.peekFirst();
        Point newHead = new Point(head.x, head.y);
        switch (direction) {
            case 'U' -> newHead.y--;
            case 'D' -> newHead.y++;
            case 'L' -> newHead.x--;
            case 'R' -> newHead.x++;
        }
    //game over conditions
  if (newHead.x < 0 || newHead.y < 0 || newHead.x >= GRID_WIDTH || newHead.y >= GRID_HEIGHT || snake.contains(newHead)) {
    running = false;
    timer.stop();
  }else {
    snake.addFirst(newHead);
    if (newHead.equals(food)) {
                spawnFood();
                score++;
    } else {
      snake.removeLast();
    }
  }
  
  private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
          if (!running && e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGame();
                repaint();
          } 
          else if (running) {
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
        }
    }
  @Override
  public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
  public void draw(Graphics g) {
        if (running) {
            g.setColor(Color.RED);
            g.fillRect(food.x * BOX_SIZE, food.y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
            g.setColor(Color.GREEN);
            for (Point p : snake) {
                g.fillRect(p.x * BOX_SIZE, p.y * BOX_SIZE, BOX_SIZE, BOX_SIZE);
            }
        } else {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            String msg1 = "Game Over! Score: " + (snake.size() - 1);
            String msg2 = "Press Enter to Restart";
          // Get FontMetrics
            FontMetrics metrics = g.getFontMetrics(g.getFont());
          // Calculate positions
            int x1 = (PANEL_WIDTH - metrics.stringWidth(msg1)) / 2;
            int x2 = (PANEL_WIDTH - metrics.stringWidth(msg2)) / 2;

            int y1 = PANEL_HEIGHT / 2 - metrics.getHeight();
            int y2 = PANEL_HEIGHT / 2 + metrics.getHeight();

            g.drawString(msg1, x1, y1);
            g.drawString(msg2, x2, y2);
        }
  }
}
