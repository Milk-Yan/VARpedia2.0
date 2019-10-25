package main.java.application;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Assists with the creation and manipulation of strings.
 *
 * @author Milk, OverCry
 */
public class StringManipulator {

    /**
     * Converts an InputStream to a String.
     * @param inputStream The InputStream to convert.
     * @return The String translated from the InputStream.
     */
    public String inputStreamToString(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String result = bufferedReader.lines().collect(Collectors.joining("\n"));
        try {
            // Closes the stream to release system resources associated with it.
            bufferedReader.close();
        } catch (IOException e) {
            // Don't do anything, the reader is already closed.
        }
        return result;
    }

    /**
     * Count the number of words in a string.
     * @param string The input string to count.
     * @return The number of words in the string.
     */
    public int countWords(String string) {
        String[] words = string.split("\\s+");
        return words.length;
    }

    /**
     * Read the characters from a file and find the line with the keytype specified.
     * @param file The file to read from.
     * @param keyType The string to look for.
     * @return The String of characters from the file.
     */
    public String readFromFile(File file, String keyType) {
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith(keyType)) {
                    br.close();
                    return line.substring(line.indexOf("=") + 1).trim();
                }
            }
            br.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(
                    "Couldn't find " + keyType + " in config file " + file.getName());
        } catch (IOException e) {
            throw new RuntimeException("Can't read file.");
        }

        return null;

    }

    /**
     * Reads the score from a score file.
     * @param file The file that contains the score for a term.
     * @return The score read
     * @throws IOException Throws this if couldn't read the file.
     */
    public String readScoreFromFile(File file) throws IOException {
        String text =  new String(Files.readAllBytes(Paths.get(file.getPath())));

        // remove whitespace if there is any from both sides of the string
        return text.trim();

    }

    /**
     * Removes the line numbers from a string if there are any.
     * @param text The string with line numbers.
     * @return The string without line numbers.
     */
    public String removeNumberedLines(String text) {
        // removes all substrings such that it is one to multiple numbers followed by a dot and a
        // space.
        return text.replaceAll("\\d+\\. ", "").replaceAll("\n", "");
    }

    /**
     * Removes the term from the text so that the user doesn't hear it.
     * @param practiceText The original text, possibly containing the term.
     * @param term The term to remove from the text.
     * @return The text without the term. Secret word in inserted in place.
     */
    public String getQuizText(String practiceText, String term) {
        return practiceText.replaceAll(term, "secret word");
    }
}

