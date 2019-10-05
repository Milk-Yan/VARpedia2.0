package main.java.controllers.loading;

import javafx.fxml.FXML;
import main.java.controllers.Controller;
import main.java.tasks.CreateAudioTask;

/**
 * Controller for CreateAudio.fxml
 *
 * @author wcho400
 */
public class CreateAudio extends Controller {

    private CreateAudioTask _task;

    /**
     * button to return to main menu if pressed
     */
    @FXML
    private void mainMenuPress() {
        // cancel current task before going back to main menu
        if (_task != null && !_task.isCancelled()) {
            _task.destroyProcess(); // need to destroy process before interrupting task
            _task.cancel();
        }

        mainMenu();
    }

    /**
     * set tasks to be canceled when the user returns to menu before completion
     *
     * @param task
     */
    public void setTask(CreateAudioTask task) {
        _task = task;
    }
}
