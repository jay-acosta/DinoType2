import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private final String GAME_NAME = "Sample Text";
    private final int HEIGHT = 600;
    private final int WIDTH = 600;
    private final int TIMER_DELAY = 1000;

    private JFrame gameFrame;
    private Timer gameTimer;

    private JPanel mainMenu;

    public GameFrame() {
        gameFrame = new JFrame(GAME_NAME);
        gameFrame.setSize(WIDTH, HEIGHT);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = gameFrame.getContentPane();
        contentPane.setBackground(Color.WHITE);

        mainMenu = new JPanel();

        JLabel titleLabel = new JLabel(GAME_NAME);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 36));
        titleLabel.setHorizontalTextPosition(JLabel.CENTER);

        JButton button = new JButton("Click ME!");

        mainMenu.add(titleLabel);
        mainMenu.add(button);

        contentPane.add(mainMenu);
        gameFrame.setVisible(true);
    }
}
