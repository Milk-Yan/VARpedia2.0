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
 * @author Milk
 */
public class TermSearch extends Controller {

    @FXML
    private TextField _termInput;

    @FXML
    private ProgressIndicator _indicator;

    private SearchTermTask _task;

    /**
     * search for a term in wikipedia
     * checks if input is empty
     */
    @FXML
    private void search() {

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

    @FXML
    private void mainMenuPress() {

        // cancel current task before going back to main menu
        if (_task != null && !_task.isCancelled()) {
            _task.cancel();
        }

        mainMenu();
    }

    @FXML
    private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            search();
        }
    }

}
