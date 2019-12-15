// imports

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

/**
 * For use in the game DinoType (Version 2)
 *
 * @author Jay Acosta
 */
public class GameManager {

    // class constants
    private final String GAME_NAME = "Dino Type 2";
    private final int HEIGHT = 600;
    private final int WIDTH = 600;
    private final String MAIN_MENU_STATE = "MAIN MENU";
    private final String SELECTION_STATE = "PLAY GAME";
    private final String GAME_STATE = "GAME STATE";
    private final String EXIT_STATE = "EXIT";

    private boolean gameStarted;

    private JPanel cards;
    private String currentState;

    public GameManager() {
        initializeGameStates();
    }

    private void initializeGameStates() {
        // set up the main JFrame of the program
        JFrame mainFrame = new JFrame(GAME_NAME);
        mainFrame.setSize(WIDTH, HEIGHT);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);

        // capture the content pane of the JFrame
        Container contentPane = mainFrame.getContentPane();
        cards = new JPanel(new CardLayout());

        // set up game states
        setUpMainMenu();
        setUpSelectionScreen();
        setUpGameScreen();

        // add the CardLayout to the content pane
        contentPane.add(cards);

        // set the main frame's visibility to false
        mainFrame.setVisible(true);
    }

    private void setUpMainMenu() {

        // set up the main panel for the
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel(GAME_NAME);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(1, 20)));

        panel.add(titleLabel);
        panel.add(Box.createVerticalGlue());

        JButton[] buttons = new JButton[3];

        buttons[0] = new JButton("Start Game");
        buttons[1] = new JButton("How to Play");
        buttons[2] = new JButton("Exit");

        buttons[0].addActionListener(new TransitionState(SELECTION_STATE));
        buttons[2].addActionListener(new TransitionState(EXIT_STATE));

        for(JButton button: buttons) {
            button.setBackground(Color.BLACK);
            button.setForeground(Color.WHITE);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(button);
        }

        panel.add(Box.createVerticalGlue());
        cards.add(panel, MAIN_MENU_STATE);
    }

    private void setUpSelectionScreen() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JButton button = new JButton("Back to Main Menu");
        button.addActionListener(new TransitionState(MAIN_MENU_STATE));
        panel.add(button);


        Set<String> textFiles = FileManager.textFiles.keySet();
        String[] options = new String[textFiles.size()];

        int index = 0;
        for(String fileName: textFiles) {
            options[index++] = fileName;
        }

        JList<String> list = new JList<>(options);
        panel.add(list);

        JButton go = new JButton("Go");
        go.addActionListener(new SelectionListener(list));

        panel.add(go);

        cards.add(panel, SELECTION_STATE);
    }

    private void setUpGameScreen() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);

        GameLoop loop = new GameLoop();


        cards.add(panel, GAME_STATE);
    }

    private class SelectionListener implements ActionListener {

        private JList<String> options;
        private SelectionListener(JList<String> options) {
            this.options = options;
        }
        public void actionPerformed(ActionEvent e){
            System.out.println(options.getSelectedIndex());
        }
    }

    private class TransitionState implements ActionListener {

        private String menuName;

        private TransitionState(String goTo) {
            menuName = goTo;
        }

        public void actionPerformed(ActionEvent e) {

            if(menuName.equals(EXIT_STATE)) {
                System.out.println("Exiting...");
                System.exit(8008135);
            } else {
                CardLayout layout = (CardLayout) (cards.getLayout());

                layout.show(cards, menuName);
                currentState = menuName;
            }
        }
    }

    /**
     * The GameLoop class manages the frame by frame updates
     * necessary to check player input status in real time
     */
    private class GameLoop implements ActionListener {
        @Override
        public synchronized void actionPerformed(ActionEvent e) {

            CardLayout card = (CardLayout) (cards.getLayout());

            System.out.println(currentState);
        }
    }
}
