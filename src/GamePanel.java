import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.List;

public class GamePanel extends JPanel {

    // instance variables and constants

    // class constants
    private final int HEIGHT = GameFrame.HEIGHT;
    private final int WIDTH = GameFrame.WIDTH;

    private final int[] LOCAL_DIFFICULTIES = {
            25 * WIDTH / 3000,
            50 * WIDTH / 3000,
            75 * WIDTH / 3000,
            120 * WIDTH / 3000
    };
    private final Font FONT = new Font("Comic Sans MS", Font.BOLD, 20);

    // instance variables
    private boolean error;
    private boolean gameStarted;

    private int lives;
    private int dinoX;
    private int stepSize;

    private List<Image> frames;
    private int frameIndex;

    private Queue<String> promptQueue;
    private Timer gameTimer;

    private StringBuilder currentText;

    private GameFrame.TransitionState stateAfterDone; // for exiting to the main menu

    public GamePanel() {

        // set the panel attributes
        setSize(WIDTH, HEIGHT);
        setFocusable(true);
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);

        // initialize the StringBuilder with the current text
        currentText = new StringBuilder();
        addKeyListener(new KeyboardEar());

        GameLoop timerTask = new GameLoop();
        gameTimer = new Timer(100, timerTask);

        frames = FileManager.getDinoFrames();

        // initialize the current queue with the text prompts
        resetGame();

        setDifficulty(3);
    }

    public void setDifficulty(int difficultyIndex) {

        // check preconditions
        if (difficultyIndex < 0 || difficultyIndex >= LOCAL_DIFFICULTIES.length) {
            throw new IllegalArgumentException("difficulty index is out of bounds");
        }

        stepSize = LOCAL_DIFFICULTIES[difficultyIndex];
    }

    public void resetGame() {
        dinoX = 0;
        lives = 1;

        if (gameTimer.isRunning()) {
            gameTimer.stop();
        }

        gameStarted = false;
        error = false;

        Queue<String> sample = new LinkedList<>();
        sample.add("The current text file is empty.");

        setCurrentPrompt(sample);

        currentText = new StringBuilder();

        frameIndex = 0;
    }

    public void setCurrentPrompt(Queue<String> prompt) {
        promptQueue = makeDeepCopy(prompt);
    }

    private Queue<String> makeDeepCopy(Queue<String> source) {
        Queue<String> result = new LinkedList<>();

        for (String s : source) {
            result.add(s);
        }

        return result;
    }

    public void paintComponent(Graphics graphics) {

        clearGraphics(graphics);

        graphics.setColor(error ? Color.PINK : Color.WHITE);
        graphics.setFont(FONT);

        if (promptQueue.peek() != null)
            graphics.drawString(promptQueue.peek(), 0, HEIGHT - 6 * 20);

        graphics.drawString(currentText.toString(), 0, HEIGHT - 4 * 20);
        graphics.drawString("Laps left: " + lives, 0, 2 * 20);
        graphics.drawString("Prompts left: " + promptQueue.size(), 0, 4 * 20);

        graphics.drawImage(frames.get(frameIndex), dinoX - 200, 120, null);
    }

    // clears the current graphics on the panel by filling the canvas with a rectangle
    private void clearGraphics(Graphics graphics) {
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
    }

    public void setGameEndTransition(GameFrame.TransitionState transition) {
        stateAfterDone = transition;
    }

    private void checkGameConditions() {

        // the following conditionals will be checked if the current prompt is not empty
        if (!promptQueue.isEmpty()) {

            // fairly inefficient solution, will do for now

            String currentPrompt = promptQueue.peek();
            int currentLength = currentText.length();
            int promptLength = currentPrompt.length();

            if (currentText.toString().equals(currentPrompt)) {

                // player has written the prompt on the screen, move onto the next prompt
                promptQueue.poll();
                lives++;

                if (promptQueue.isEmpty()) {
                    // if the current queue is empty, we won!
                    gameTimer.stop();

                    stateAfterDone.goToPanel();

                } else {
                    // otherwise, clear the current text for the player (make a new StringBuilder)
                    currentText = new StringBuilder();
                }
            } else if (currentLength < promptLength) {

                // current string is smaller than the prompt, check the substring up to this point
                error = !currentText.toString().equals(currentPrompt.substring(0, currentLength));
            } else {

                // the current length is larger than the prompt, guaranteed to be wrong
                error = true;
            }
        }
    }

    /**
     * The KeyboardEar class handles all of the key typing event
     * while the game is in play
     */
    private class KeyboardEar implements KeyListener {
        public void keyTyped(KeyEvent e) {

            if (!gameStarted) {
                gameStarted = true;
                gameTimer.start();
            }

            char keyChar = e.getKeyChar();

            if (keyChar == KeyEvent.VK_BACK_SPACE) {

                // if the current char typed is a backspace, delete the last character
                if (currentText.length() > 0) {
                    currentText.deleteCharAt(currentText.length() - 1);
                }
            } else if (keyChar != KeyEvent.VK_SHIFT) {

                currentText.append(keyChar);
            }

            checkGameConditions();
            repaint();
        }

        /*
            Unimplemented methods
         */
        public void keyReleased(KeyEvent e) {
        }

        public void keyPressed(KeyEvent e) {
        }
    }

    /**
     * The GameLoop class manages the frame by frame updates
     * necessary to check player input status in real time
     */
    private class GameLoop implements ActionListener {
        @Override
        public synchronized void actionPerformed(ActionEvent e) {
            dinoX = (dinoX + stepSize) % (200 + WIDTH);

            if (dinoX < stepSize) {
                lives--;
            }

            if (lives <= 0) {

                gameTimer.stop();
                stateAfterDone.goToPanel();
            }

            frameIndex = (frameIndex + 1) % frames.size();

            repaint();
        }
    }
}
