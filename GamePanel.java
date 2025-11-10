import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

//main game area - this is where the snake moves and eyerthing happens 
public class GamePanel extends JPanel implements ActionListener {

  //size setup- box size, grid width & height
  private final int BOX_SIZE = 20;
  private final int GRID_WIDTH = 25, GRID_HEIGHT = 25;
  private final int PANEL_WIDTH = GRID_WIDTH * BOX_SIZE, PANEL_HEIGHT = GRID_HEIGHT * BOX_SIZE;

  //snake body and food position
  private LinkedList<Point> snake;
  private Point food;

  //controls movement diirections and game state
  private char direction = 'R';
  private boolean running = false;
  private javax.swing.Timer timer;

  private int score = 0; //saap kitna seb khaya uska tracker 

  //Constructor - sets up everything when the game starts 
  public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
  }

  // This starts or restarts the game 
  public final void startGame() {
        snake = new LinkedList<>();
        score = 0; // reset score
        //snake.add(new Point(5,5));
        Point head = new Point(5,5);
        direction = 'R';
        snake.add(head);
        snake.add(new Point(head.x -1, head.y)); //initial length 2
        spawnFood(); //places food randomly 
        running = true;
        timer = new javax.swing.Timer(200, this); // game speed controller 
        timer.start();
  }

  // Generates food in a random grid cell 
  public void spawnFood() {
        Random rand = new Random();
        int x, y;
        do {
            x = rand.nextInt(GRID_WIDTH);
            y = rand.nextInt(GRID_HEIGHT);
            food = new Point(x, y);
        }
        while (snake.contains(food));  // ensures food doesn't spawn on snake 
  }

  // called automatically after every tier (every 200ms)
  @Override
  public void actionPerformed(ActionEvent e) {
      if (running) {
          move();
          repaint();
      }
  }

  //controls how the snake moves 
  public void move() {
      Point head = snake.peekFirst(); //get current head position
      Point newHead = new Point(head.x, head.y);
      switch (direction) {
          case 'U' -> newHead.y--; //move up
          case 'D' -> newHead.y++; //move down
          case 'L' -> newHead.x--; //move left
          case 'R' -> newHead.x++; //move right 
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
  } 

  //handles keyboard inputs
  private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
          if (!running && e.getKeyCode() == KeyEvent.VK_ENTER) {
                startGame(); //restart when game over 
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
        Graphics2D g2 = (Graphics2D) g.create();

        // Draw ground tiles
        for (int gx = 0; gx < GRID_WIDTH; gx++) {
            for (int gy = 0; gy < GRID_HEIGHT; gy++) {
                int variant = groundVariant != null ? groundVariant[gx][gy] : (rng.nextBoolean() ? 1 : 0);
                BufferedImage tile = (variant == 0) ? groundA : groundB;
                if (tile != null) {
                    g2.drawImage(tile, gx * BOX_SIZE, gy * BOX_SIZE, null);
                } 
                else {
                  g2.setColor(variant == 0 ? new Color(0xDDEB5A) : new Color(0x8FC64F));
                  g2.fillRect(gx * BOX_SIZE, gy * BOX_SIZE, BOX_SIZE, BOX_SIZE);
                }
            }
        }
  }
}
