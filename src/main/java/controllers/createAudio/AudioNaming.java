package main.java.controllers.createAudio;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.controllers.Controller;
import main.java.tasks.CreateAudioTask;

import java.io.File;

/**
 * Controller for functionality of Naming.fxml
 *
 * @author Milk
 */
public class AudioNaming extends Controller {

    @FXML
    private TextField _nameInput;

    @FXML
    private ProgressIndicator _indicator;

    private String _name;
    private String _term;
    private String _chosenText;
    private String _voice;
    private CreateAudioTask _task;

    /**
     * sets inputs of audio creation parameters
     *
     * @param term
     * @param chosenText
     * @param voice
     */
    public void setUp(String term, String chosenText, String voice) {
        _term = term;
        _chosenText = chosenText;
        _voice = voice;

        setUpDefaultName();
    }

    private void setUpDefaultName() {
        File termFolder =
                new File(Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term);
        int fileNumber = termFolder.listFiles().length+1;
        _nameInput.setText(_term + fileNumber);
    }

    /**
     * button to submit a name for the audio
     * checks if the name is valid or a repeat and sends an alert
     */
    @FXML
    private void enter() {

        String s = File.separator;

        // check for correct input
        _name = _nameInput.getText().trim();

        if (_name.isEmpty()) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");

        } else if (!_name.matches("[A-Za-z0-9]+")) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name can only contain " +
                    "a-A and 0-9.");

        } else if (new File(
                Folders.AUDIO_FOLDER.getPath() + s + _term + s +
                        _name + ".wav").isFile()) {

            // check if want to overwrite
            Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Warning", "Audio file already" +
                    " exists",
                    "Would you like to overwrite the existing file?").getAlert();
            if (alert.getResult() == ButtonType.OK) {
                create();
            }

        } else {
            create();
        }
    }

    private void create() {

        _term = _term.replaceAll(" ", "-");
        // folder to store audio
        File audioFolder =
                new File(Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator +  _term);

        // use new thread to create in bg
        _task = new CreateAudioTask(audioFolder, _term,  _name, _chosenText, _mainApp, _voice,
                false);
        new Thread(_task).start();

        // gives indication that the scene is loading
        _indicator.setVisible(true);
    }

    @FXML
    private void mainMenuPress() {

        // cancel current task before going back to main menu
        if (_task != null && !_task.isCancelled()) {
            _task.destroyProcess(); // need to destroy process before interrupting task
            _task.cancel();
        }

        mainMenu();
    }

    @FXML
    private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            enter();
        }
    }


}
