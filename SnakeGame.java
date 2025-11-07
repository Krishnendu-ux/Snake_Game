import javax.swing.*;
public class SnakeGame extends JFrame {
  public SnakeGame() {
      add(new GamePanel());
      setTitle("Snake Game");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setResizable(false);
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
  }
  public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}
    
