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

