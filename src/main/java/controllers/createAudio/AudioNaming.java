package main.java.controllers.createAudio;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.controllers.Controller;
import main.java.tasks.CreateAudioTask;

import java.io.File;

/**
 * Controller for functionality of Naming.fxml. Allows user to name the audio creation.
 *
 * @author Milk, OverCry
 */
public class AudioNaming extends Controller {

    @FXML private TextField _nameInput;
    @FXML private ProgressIndicator _indicator;
    @FXML private Button _mainMenuBtn;

    private String _name;
    private String _term;
    private String _chosenText;
    private String _voice;
    private CreateAudioTask _task;


    /**
     * Sets up inputs of audio creation parameters.
     *
     * @param term The term of the audio creation.
     * @param chosenText The text for the audio creation to contain.
     * @param voice The voice to synthesise.
     */
    public void setUp(String term, String chosenText, String voice) {
        _term = term;
        _chosenText = chosenText;
        _voice = voice;

        setUpDefaultName();
    }

    /**
     * Sets the name to the term plus the number of current audio for the term. This is for
     * convenience when creating the audio, if the user does not want to create the audio they
     * could just go with the default name.
     */
    private void setUpDefaultName() {
        File termFolder =
                new File(Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term);

        int fileNumber;
        if (termFolder.exists()) {
            fileNumber = termFolder.listFiles().length+1;
        } else {
            fileNumber = 1;
        }

        // replace all spaces with underscores and remove all other special characters
        String termName = _term.replaceAll(" ", "_").replaceAll("[^A-z0-9]", "");
        _nameInput.setText(termName + fileNumber);
    }

    /**
     * Functionality of the enter button to submit a name for the audio.
     * Checks if the name is valid or a repeat and sends an alert if not.
     */
    @FXML private void enter() {

        // check for correct input
        _name = _nameInput.getText().trim();

        if (_name.isEmpty()) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");

        } else if (!_name.matches("[A-Za-z0-9_]+")) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name can only contain " +
                    "a-A and 0-9.");

        } else if (new File(
                Folders.AUDIO_FOLDER.getPath() + File.separator + _term + File.separator +
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

    /**
     * Create the audio file.
     */
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

    /**
     * Functionality of the mainMenu button. Cancels current tasks before returning to the main
     * menu.
     */
    @FXML private void mainMenuPress() {

        // cancel current task before going back to main menu
        if (_task != null && !_task.isCancelled()) {
            _task.destroyProcess(); // need to destroy process before interrupting task
            _task.cancel();
        }

        if (_mainMenuBtn.getText().equals("Quit to Main Menu")){
            _mainMenuBtn.setText("Are you sure?");
        } else if (!_mainMenuBtn.getText().equals("Quit to Main Menu")){
            mainMenu();
        }

    }

    /**
     * resets menu button to original text
     */
    @FXML
    private void setMenu(){
        if (!(_mainMenuBtn.getText().equals("Quit to Main Menu"))) {
            _mainMenuBtn.setText("Quit to Main Menu");
        }
    }

    /**
     * Functionality of the parent pane. If the user presses the enter key while not selecting
     * anything else, it will be the same as clicking the enter button.
     * @param keyEvent The event triggered on a key press.
     */
    @FXML private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            enter();
        }
    }

    /**
     * checks if only spaces have been entered
     */
    @FXML
    private void removeOnlySpace(){
        if (_nameInput.getText().equals(" ")){
            _nameInput.setText("");
        }
    }

}
