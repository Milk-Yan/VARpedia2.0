package main.java.controllers.createAudio;

import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.application.AlertFactory;
import main.java.controllers.Controller;
import main.java.tasks.SearchTermTask;

/**
 * Controller for functionality of Create.fxml
 *
 * @author Milk, OverCry
 */
public class TermSearch extends Controller {

    @FXML private TextField _termInput;
    @FXML private ProgressIndicator _indicator;

    private SearchTermTask _task;

    /**
     * Search for a term in Wikipedia.
     * Checks if input is empty.
     */
    @FXML private void search() {

        if (_termInput.getText().trim().isEmpty()) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "The term cannot be empty" +
                    ".");

        } else {

            String term = _termInput.getText();

            // use a new thread to complete the search task
            _task = new SearchTermTask(term, _mainApp);

            new Thread(_task).start();

            // gives indication that the scene is loading
            _indicator.setVisible(true);
        }
    }

    /**
     * Functionality of the main menu button. Cancels the current task before returning to the
     * main menu.
     */
    @FXML private void mainMenuPress() {

        if (_task != null && !_task.isCancelled()) {
            _task.cancel();
        }

        mainMenu();
    }

    /**
     * If the user presses enter, it will be the same as clicking search.
     * @param keyEvent The event triggered by a keypress
     */
    @FXML
    private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            search();
        }
    }

}
