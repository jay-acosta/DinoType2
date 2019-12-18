// imports
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;
import java.util.List;

/**
 * For use in the game DinoType (Version 2)
 *  Manages file imports to use as text prompts and
 *  gathers images to be used in the game
 * @author Jay Acosta
 */
public class FileManager {

    // class constants
    private final String PROMPTS_DIR_PATH = "./prompts/";
    // Make one instance of all the text files present in the game
    // Resetting the game is necessary in order to account for added files
    private static Map<String, Queue<String>> textFiles;
    private static List<Image> dinoFrames;
    private final String FRAMES_DIR_PATH = "./images/dino-frames/";

    /**
     * Updates the file processor
     */
    public FileManager() {

        textFiles = new HashMap<>();
        dinoFrames = new LinkedList<>();
        updateWordFiles();
        updateFrameFiles();
    }

    public static Queue<String> getRandomPrompt() {

        Queue<String> temp = new LinkedList<>();
        temp.add("It looks like you have no files currently. Why?");

        if (textFiles == null || textFiles.isEmpty()) {
            return temp;
        }

        int randomIndex = (int) (Math.random() * textFiles.size());
        int index = 0;

        for (Queue<String> promptQueue : textFiles.values()) {

            if (index == randomIndex)
                return promptQueue;
            index++;
        }

        return temp;
    }

    public static Map<String, Queue<String>> getTextFiles() {
        return textFiles;
    }

    public static Map<String, Queue<String>> getFilesInAlphabeticalOrder() {
        return new TreeMap<>(textFiles);
    }

    public static List<Image> getDinoFrames() {
        return dinoFrames;
    }

    /**
     * pre: the directory "prompts" exists within the local directory
     * post: updates the map contain the prompts that will be used in the main game
     */
    private void updateWordFiles() {

        File directory = new File(PROMPTS_DIR_PATH);

        // check preconditions
        if (!directory.exists() && directory.isDirectory()) {
            throw new IllegalStateException("the directory " + directory.getPath() + " does not exist within game files.");
        }

        System.out.println("Loading prompts...");

        // get all files in the directory that are .txt files
        FilenameFilter textFilter = new GenericFilenameFilter("txt");
        File[] promptFiles = directory.listFiles(textFilter);

        assert promptFiles != null : "prompts directory was null";

        // iterate through all text files and add to the map of text files
        for (File textFile : promptFiles) {

            try {

                // add a text file if not already in the list of prompts
                if (!textFiles.containsKey(textFile.getName())) {

                    Queue<String> promptQueue = new LinkedList<>();
                    Scanner input = new Scanner(textFile);
                    // input.useDelimiter("[A-Za-z]");

                    final int CHAR_CAP = 50;
                    // StringBuilder promptSegment = new StringBuilder();

                    // parse through the text file and add the words to a List<String>
                    while (input.hasNextLine()) {

                        String currentLine = input.nextLine();

                        if (currentLine.length() <= CHAR_CAP) {
                            promptQueue.add(currentLine);
                        } else {

                            int index;
                            for (index = 0; index < currentLine.length() - CHAR_CAP; index += CHAR_CAP) {
                                promptQueue.add(currentLine.substring(index, index + CHAR_CAP) + "-");
                            }

                            promptQueue.add(currentLine.substring(index));
                        }
                    }

                    // if(promptSegment.length() != 0) {
                    //     promptQueue.add(promptSegment.toString().trim());
                    // }

                    // add this word list to the map
                    textFiles.put(textFile.getName(), promptQueue);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("could not find the file " + textFile.getName());
            }
        }

        System.out.println("Successfully loaded " + promptFiles.length + " files.");
    }

    private void updateFrameFiles() {
        File directory = new File(FRAMES_DIR_PATH);

        // check preconditions
        if (!directory.exists() && directory.isDirectory()) {
            throw new IllegalStateException("the directory " + directory.getPath() + " does not exist within game files.");
        }

        System.out.println("Loading images...");

        // get all files in the directory that are .jpg files
        FilenameFilter imageFilter = new GenericFilenameFilter("jpg");
        File[] images = directory.listFiles(imageFilter);

        assert images != null : FRAMES_DIR_PATH + " was empty";
        for (File fileImage : images) {

            try {
                dinoFrames.add(ImageIO.read(fileImage));
            } catch (Exception e) {
                System.out.println("Error while reading " + fileImage.getName());
            }
        }

        System.out.println("Successfully loaded " + dinoFrames.size() + " images.");
    }

    // file filter class that allows only certain file types
    private class GenericFilenameFilter implements FilenameFilter {

        private String validExtension;

        private GenericFilenameFilter(String validExtension) {
            this.validExtension = validExtension;
        }

        // returns true if pathName ends with the given extension
        // pathName in lowercase to ensure matching is correct
        public boolean accept(File directory, String pathName) {
            return pathName.toLowerCase().endsWith(validExtension);
        }
    }
}
