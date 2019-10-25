package main.java.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertFactory;
import main.java.application.Main;
import main.java.application.StringManipulator;

import java.io.IOException;
import java.util.Arrays;

/**
 * Searches for terms using BASH wikit.
 *
 * @author Milk, OverCry
 */
public class SearchTermTask extends Task<String> {

    private String _term;
    private String _searchResults;
    private boolean _isInvalid = false;
    private Main _mainApp;

    /**
     * Searches for the Wikipedia term using the BASH wikit.
     * @param searchTerm The Wikipedia term to search.
     * @param mainApp The main of this application, used for switching between scenes.
     */
    public SearchTermTask(String searchTerm, Main mainApp) {
        _term = searchTerm;
        _mainApp = mainApp;
    }

    /**
     * Invoked when the Task is executed. Performs the background thread logic to get the search
     * results on Wikipedia.
     *
     * @return The search results for the term on Wikipedia.
     */
    @Override
    protected String call() {
        try {

            StringManipulator manipulator = new StringManipulator();

            // wikit the term given
            Process process = new ProcessBuilder("bash", "-c",
                    "yes '' | wikit \"" + _term + "\"").start();

            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // don't do anything
            }

            if (process.exitValue() != 0) {

                // run on GUI thread
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error encountered", "Problem finding term.",
                            manipulator.inputStreamToString(process.getErrorStream()));
                });

            } else {
                _searchResults = manipulator.inputStreamToString(process.getInputStream());
                if (_searchResults.contains("? Ambiguous results")) {
                    Platform.runLater(() -> {
                        new AlertFactory(AlertType.ERROR, "Ambiguous results", "There are " +
                                "multiple results for this term",
                                "refine your search and try again.");
                    });
                    cancel();
                }
            }


        } catch (IOException e) {
            // run on GUI thread
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error encountered", "I/O Exception",
                        Arrays.toString(e.getStackTrace()));
                _mainApp.displayMainMenuScene();
            });
        }

        return null;
    }

    /**
     * This method is called when the Task transforms to the succeeded state. It checks if wikit
     * was able to find this term, and cancels the task if wikit was unable. If everything is
     * successful, it switches scene to allow the user to choose the text they want to include in
     * their audio.
     */
    @Override
    protected void succeeded() {

        if (_searchResults.contains(":^(")) {
            _isInvalid = true;
            cancel(); // run cancel operations
            return;
        }

        // run on GUI thread
        Platform.runLater(() -> {
            _mainApp.displayCreateAudioChooseTextScene(_term, _searchResults);
        });

    }

    /**
     * This method is called whenever the Task transforms to the cancelled state. It tells the
     * user and goes back to the audio search screen.
     */
    @Override
    protected void cancelled() {
        super.cancelled();
        if (_isInvalid) {
            // run on GUI thread
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error encountered", "Invalid term",
                        "This term cannot be found. Please try again.");
            });

        }
        _mainApp.displayCreateAudioSearchScene();

    }


}
