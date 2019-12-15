// imports
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;

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
    public static Map<String, List<String>> textFiles;

    /**
     * Updates the file processor
     */
    public FileManager() {

        textFiles = new HashMap<>();
        updateWordFiles();
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

        System.out.println("Loading files...");

        // get all files in the directory that are .txt files
        FilenameFilter textFilter = new GenericFilenameFilter("txt");
        File[] promptFiles = directory.listFiles(textFilter);

        assert promptFiles != null : "prompts directory was null";

        // iterate through all text files and add to the map of text files
        for (File textFile : promptFiles) {

            try {

                // add a text file if not already in the list of prompts
                if (!textFiles.containsKey(textFile.getName())) {

                    List<String> promptWordList = new ArrayList<>();
                    Scanner input = new Scanner(textFile);

                    // parse through the text file and add the words to a List<String>
                    while (input.hasNext()) {
                        promptWordList.add(input.next());
                    }

                    // add this word list to the map
                    textFiles.put(textFile.getName(), promptWordList);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("could not find the file " + textFile.getName());
            }
        }

        System.out.println("Successfully loaded " + promptFiles.length + " files.");
    }

    public Map<String, List<String>> getTextFiles() {
        return textFiles;
    }

    public Map<String, List<String>> getFilesInAlphabeticalOrder() {
        return new TreeMap<>(textFiles);
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
