// imports

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Set;

/**
 * For use in the game DinoType (Version 2)
 *
 * @author Jay Acosta
 */
public class GameFrame extends JFrame {

    // class constants
    private final String GAME_NAME = "Dino Type 2";
    public static final int HEIGHT = 600;
    public static final int WIDTH = 600;

    private final String MAIN_MENU_STATE = "MAIN_MENU_STATE";
    private final String SELECTION_STATE = "SELECTION_STATE";
    private final String GAME_STATE = "GAME_STATE";
    private final String EXIT_STATE = "EXIT_STATE";

    private CardLayout currentLayout;

    // cards contains all the current panels and stores them in a CardLayout format
    private JPanel cards;
    private int difficulty = 0;
    private String fileName;

    private FileManager fileManager;

    public GameFrame(FileManager fileManager) {
        this.fileManager = fileManager;
        initializeGameStates();
    }

    private void initializeGameStates() {

        // set up the main JFrame of the program
        setTitle(GAME_NAME);
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.BLACK);
        setResizable(false);

        // capture the content pane of the JFrame
        Container contentPane = getContentPane();
        currentLayout = new CardLayout();
        cards = new JPanel(currentLayout);

        // set up game states
        setUpMainMenu();
        setUpSelectionScreen();
        setUpGameScreen();

        // add the CardLayout to the content pane
        contentPane.add(cards);

        // set the main frame's visibility to false
        setVisible(true);
    }

    // pre: none
    // post: sets the GUI for the selection screen for all prompts
    private void setUpMainMenu() {

        // set up the main panel for the background using a BoxLayout
        JPanel panel = new JPanel();
        panel.setFocusable(true);
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBackground(Color.BLACK);

        // set the properties for the title screen label
        JLabel titleLabel = new JLabel(GAME_NAME);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        // create a new box rigid area for spacing
        panel.add(Box.createRigidArea(new Dimension(1, 20)));

        panel.add(titleLabel);
        panel.add(Box.createVerticalGlue());

        JButton[] buttons = new JButton[2];

        buttons[0] = new JButton("Start Game");
        buttons[1] = new JButton("Exit");

        buttons[0].addActionListener(new TransitionState(MAIN_MENU_STATE, SELECTION_STATE));
        buttons[1].addActionListener(new TransitionState(MAIN_MENU_STATE, EXIT_STATE));

        for (JButton button : buttons) {
            button.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(button);
        }

        // use vertical glue to space out the JButton's
        panel.add(Box.createVerticalGlue());

        // add the current panel to the card layout container with the name MAIN_MENU_STATE
        cards.add(panel, MAIN_MENU_STATE);
        panel.grabFocus();
    }

    // pre: FileManager != null
    // post: sets the GUI for the selection screen for all prompts
    private void setUpSelectionScreen() {
        JPanel panel = new JPanel();
        // panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBackground(Color.BLACK);

        Set<String> keySet = fileManager.getFilesInAlphabeticalOrder().keySet();

        String[] promptArr = new String[keySet.size()];
        int index = 0;
        for (String key : keySet) {
            promptArr[index++] = key;
        }

        JComboBox<String> promptChoices = new JComboBox<>(promptArr);
        JComboBox<String> difficultyChoices = new JComboBox<>(new String[]{"Easy", "Medium", "Hard", "Very Hard"});

        promptChoices.addActionListener(e -> {
            fileName = promptChoices.getItemAt(promptChoices.getSelectedIndex());
        });

        difficultyChoices.addActionListener(e ->
                difficulty = difficultyChoices.getSelectedIndex()
        );

        JButton go = new JButton("Play game!");
        JButton random = new JButton("Play random!");
        JButton backToMain = new JButton("Back to Main Menu");
        random.setName("RANDOM_BUTTON");

        go.addActionListener(new TransitionState(SELECTION_STATE, GAME_STATE));
        random.addActionListener(new TransitionState(random.getName(), GAME_STATE));
        backToMain.addActionListener(new TransitionState(SELECTION_STATE, MAIN_MENU_STATE));

        // add components to the panel
        panel.add(promptChoices);
        panel.add(difficultyChoices);
        panel.add(go);
        panel.add(random);
        panel.add(backToMain);

        cards.add(panel, SELECTION_STATE);
    }

    // pre: none
    // post: sets the GUI for the game screen
    private void setUpGameScreen() {
        GamePanel gamePanel = new GamePanel();
        gamePanel.setGameEndTransition(new TransitionState(GAME_STATE, MAIN_MENU_STATE));
        cards.add(gamePanel, GAME_STATE);
    }

    public class TransitionState implements ActionListener {

        private String to;
        private String from;

        private TransitionState(String from, String goTo) {
            this.from = from;
            this.to = goTo;
        }

        public void actionPerformed(ActionEvent e) {
            goToPanel();
        }

        public void goToPanel() {
            if (to.equals(EXIT_STATE)) {
                System.out.println("Exiting...");
                System.exit(69);
            } else {
                currentLayout.show(cards, to);

                JPanel currentPanel = getCurrentPanel();
                assert currentPanel != null : "what, game panel was null? bruh moment";
                currentPanel.grabFocus();

                // if we are currently in the game, recognize
                if (currentPanel instanceof GamePanel) {
                    GamePanel gamePanel = (GamePanel) currentPanel;
                    gamePanel.resetGame();

                    if (from.equals("RANDOM_BUTTON")) {
                        gamePanel.setCurrentPrompt(FileManager.getRandomPrompt());
                    } else {
                        gamePanel.setCurrentPrompt(FileManager.getTextFiles().get(fileName));
                    }
                    gamePanel.setDifficulty(difficulty);
                }
            }
        }

        private JPanel getCurrentPanel() {

            for (Component component : cards.getComponents()) {
                if (component.isVisible() && component instanceof JPanel) {
                    return (JPanel) component;
                }
            }

            return null;
        }
    }
}
