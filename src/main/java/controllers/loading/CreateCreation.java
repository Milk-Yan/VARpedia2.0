package main.java.controllers.loading;

import javafx.fxml.FXML;
import main.java.controllers.Controller;
import main.java.tasks.CreateCreationTask;

/**
 * Controller for CreateCreation.fxml
 *
 * @author wcho400
 */
public class CreateCreation extends Controller {
    private CreateCreationTask _task;

    /**
     * returns to main menu when button is pressed
     */
    @FXML
    private void mainMenuPress() {
        // cancel current task before going back to main menu
        if (_task != null) {
            _task.cancel();
        }

        mainMenu();
    }

    /**
     * ensures correct task is canceled when button is pressed
     *
     * @param task
     */
    public void setTask(CreateCreationTask task) {
        _task = task;
    }
}
