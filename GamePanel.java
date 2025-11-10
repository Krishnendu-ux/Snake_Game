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

  // --- Sprite & ground fields ---
  private final String ASSET_DIR = "assets";
  private BufferedImage groundA, groundB;
  private BufferedImage appleImg;

  // head frames (in/out)
  private BufferedImage headUpIn, headUpOut, headDownIn, headDownOut, headLeftIn, headLeftOut, headRightIn, headRightOut;

  // optional body/corner/tail (if you have them)
  private BufferedImage bodyH, bodyV;
  private BufferedImage cornerUL, cornerUR, cornerDL, cornerDR;
  private BufferedImage tailUp, tailDown, tailLeft, tailRight;

  // ground variant assignment per tile
  private int[][] groundVariant;

  // movement-tick counter (for 2-in/1-out tongue cycle)
  private int moveStep = 0;

  private final Random rng = new Random();

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
        if (running) {
            if (appleImg != null) {
                g2.drawImage(appleImg, food.x * BOX_SIZE, food.y * BOX_SIZE, null);
            } 
            else {
                g2.setColor(Color.RED);
                g2.fillOval(food.x * BOX_SIZE + 3, food.y * BOX_SIZE + 3, BOX_SIZE - 6, BOX_SIZE - 6);
            } 

            // draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point p = snake.get(i);
                int px = p.x * BOX_SIZE;
                int py = p.y * BOX_SIZE;

                if (i == 0) {
                    BufferedImage headImg = headFrameFor(direction);
                    if (headImg != null) {
                        g2.drawImage(headImg, px, py, null);
                    } 
                    else {
                        g2.setColor(new Color(0x2E8B57));
                        g2.fillRect(px, py, BOX_SIZE, BOX_SIZE);
                        g2.setColor(Color.BLACK);
                        int eye = Math.max(1, BOX_SIZE / 6);
                        if (direction == 'U') g2.fillRect(px + BOX_SIZE/2 - eye/2, py + BOX_SIZE/4 - eye/2, eye, eye);
                        else if (direction == 'D') g2.fillRect(px + BOX_SIZE/2 - eye/2, py + (BOX_SIZE*3)/4 - eye/2, eye, eye);
                        else if (direction == 'L') g2.fillRect(px + BOX_SIZE/4 - eye/2, py + BOX_SIZE/2 - eye/2, eye, eye);
                        else g2.fillRect(px + (BOX_SIZE*3)/4 - eye/2, py + BOX_SIZE/2 - eye/2, eye, eye);
                    }
                    continue;
                }
                              // tail
                if (i == snake.size() - 1) {
                    if (drawTailIfAvailable(g2, i, p)) continue;
                    g2.setColor(new Color(0x1E7A3A));
                    g2.fillRect(px, py, BOX_SIZE, BOX_SIZE);
                    continue;
                }

                // body segment (straight or corner)
                Point prev = snake.get(i - 1);
                Point next = snake.get(i + 1);
                Point dPrev = new Point(prev.x - p.x, prev.y - p.y);
                Point dNext = new Point(next.x - p.x, next.y - p.y);

                // straight horizontal
                if (dPrev.y == 0 && dNext.y == 0) {
                    if (bodyH != null) g2.drawImage(bodyH, px, py, null);
                    else { g2.setColor(new Color(0x2E8B57)); g2.fillRect(px, py, BOX_SIZE, BOX_SIZE); }
                }
                // straight vertical
                else if (dPrev.x == 0 && dNext.x == 0) {
                    if (bodyV != null) g2.drawImage(bodyV, px, py, null);
                    else { g2.setColor(new Color(0x2E8B57)); g2.fillRect(px, py, BOX_SIZE, BOX_SIZE); }
                }
                // corners
                else {
                    if ((dPrev.x == -1 && dPrev.y == 0 && dNext.x == 0 && dNext.y == -1) ||
                        (dNext.x == -1 && dNext.y == 0 && dPrev.x == 0 && dPrev.y == -1)) {
                        if (cornerUL != null) g2.drawImage(cornerUL, px, py, null);
                        else { g2.setColor(new Color(0x2E8B57)); g2.fillRect(px, py, BOX_SIZE, BOX_SIZE); }
                    } 
                    else if ((dPrev.x == 1 && dPrev.y == 0 && dNext.x == 0 && dNext.y == -1) || (dNext.x == 1 && dNext.y == 0 && dPrev.x == 0 && dPrev.y == -1)) 
                    {
                        if (cornerUR != null) g2.drawImage(cornerUR, px, py, null);
                        else { g2.setColor(new Color(0x2E8B57)); g2.fillRect(px, py, BOX_SIZE, BOX_SIZE); }
                    } 
                    else if ((dPrev.x == 1 && dPrev.y == 0 && dNext.x == 0 && dNext.y == 1) || (dNext.x == 1 && dNext.y == 0 && dPrev.x == 0 && dPrev.y == 1)) 
                    {
                        if (cornerDR != null) g2.drawImage(cornerDR, px, py, null);
                        else { g2.setColor(new Color(0x2E8B57)); g2.fillRect(px, py, BOX_SIZE, BOX_SIZE); }
                    } else {
                        if (cornerDL != null) g2.drawImage(cornerDL, px, py, null);
                        else { g2.setColor(new Color(0x2E8B57)); g2.fillRect(px, py, BOX_SIZE, BOX_SIZE); }
                    }
                }
            }
        }
        else {
            // game over UI
            g2.setColor(Color.black);
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            String msg1 = "Game Over! Score: " + score;
            String msg2 = "Press Enter to Restart";
            FontMetrics metrics = g2.getFontMetrics(g2.getFont());
            int x1 = (PANEL_WIDTH - metrics.stringWidth(msg1)) / 2;
            int x2 = (PANEL_WIDTH - metrics.stringWidth(msg2)) / 2;
            int y1 = PANEL_HEIGHT / 2 - metrics.getHeight();
            int y2 = PANEL_HEIGHT / 2 + metrics.getHeight();
            g2.drawString(msg1, x1, y1);
            g2.drawString(msg2, x2, y2);
        }
        g2.dispose();
    }
    // Helper: choose head frame based on direction and moveStep (2 in / 1 out)
    private BufferedImage headFrameFor(char dir) {
        boolean out = (moveStep % 3) == 2; // 0,1 -> in ; 2 -> out
        return switch (dir) {
            case 'U' -> out ? headUpOut : headUpIn;
            case 'D' -> out ? headDownOut : headDownIn;
            case 'L' -> out ? headLeftOut : headLeftIn;
            default  -> out ? headRightOut : headRightIn;
        };    
    }
    // Helper: draw directional tail if available (returns true if drawn)
    private boolean drawTailIfAvailable(Graphics2D g2, int tailIndex, Point tailPoint) {
        if (tailUp == null && tailDown == null && tailLeft == null && tailRight == null) return false;
        Point tail = tailPoint;
        Point beforeTail = snake.get(tailIndex - 1);
        int dx = beforeTail.x - tail.x;
        int dy = beforeTail.y - tail.y;
        int px = tail.x * BOX_SIZE, py = tail.y * BOX_SIZE;
        if (dx == 1 && tailLeft != null) { g2.drawImage(tailLeft, px, py, null); return true; }
        if (dx == -1 && tailRight != null) { g2.drawImage(tailRight, px, py, null); return true; }
        if (dy == 1 && tailUp != null) { g2.drawImage(tailUp, px, py, null); return true; }
        if (dy == -1 && tailDown != null) { g2.drawImage(tailDown, px, py, null); return true; }
        return false;
    }

    // ---------------- Asset loading & ground init ----------------
    private void loadSprites() {
        groundA = safeScale(loadImage("ground_a.png"));
        groundB = safeScale(loadImage("ground_b.png"));
        appleImg = safeScale(loadImage("apple.png"));

        headUpIn  = safeScale(loadImage("head_up_in.png"));
        headUpOut = safeScale(loadImage("head_up_out.png"));
        headDownIn  = safeScale(loadImage("head_down_in.png"));
        headDownOut = safeScale(loadImage("head_down_out.png"));
        headLeftIn  = safeScale(loadImage("head_left_in.png"));
        headLeftOut = safeScale(loadImage("head_left_out.png"));
        headRightIn  = safeScale(loadImage("head_right_in.png"));
        headRightOut = safeScale(loadImage("head_right_out.png"));

        // optional images (no error if missing)
        bodyH = safeScale(loadImage("body_h.png"));
        bodyV = safeScale(loadImage("body_v.png"));
        cornerUL = safeScale(loadImage("corner_ul.png"));
        cornerUR = safeScale(loadImage("corner_ur.png"));
        cornerDL = safeScale(loadImage("corner_dl.png"));
        cornerDR = safeScale(loadImage("corner_dr.png"));
        tailUp = safeScale(loadImage("tail_up.png"));
        tailDown = safeScale(loadImage("tail_down.png"));
        tailLeft = safeScale(loadImage("tail_left.png"));
        tailRight = safeScale(loadImage("tail_right.png"));
    }

    private BufferedImage loadImage(String filename) {
        File f = new File(ASSET_DIR + File.separator + filename);
        if (!f.exists()) {
            System.out.println("Asset not found: " + f.getPath() + "  (using fallback)");
            return null;
        }
        try {
            return ImageIO.read(f);
        } 
        catch (IOException ex) {
            System.err.println("Failed to load: " + f.getPath());
            return null;
        }
    }
  
    private BufferedImage safeScale(BufferedImage src) {
        if (src == null) return null;
        if (src.getWidth() == BOX_SIZE && src.getHeight() == BOX_SIZE) return src;
        BufferedImage scaled = new BufferedImage(BOX_SIZE, BOX_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(src, 0, 0, BOX_SIZE, BOX_SIZE, null);
        g.dispose();
        return scaled;
    }
    
}
