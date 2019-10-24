package main.java.controllers.createCreation;

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
import main.java.tasks.CreateCreationTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Controller for CreationNaming.fxml
 *
 * @author Milk, OverCry
 */
public class CreationNaming extends Controller {

    private String _name;
    private String _term;
    private ArrayList<String> _audioList;
    private ArrayList<String> _imageList;
    private CreateCreationTask _task;
    private String _musicSelection;

    @FXML private TextField _nameInput;
    @FXML private ProgressIndicator _indicator;

    /**
     * Initializes parameters to be passed on to the next scene
     *
     * @param term The Wikipedia term of the creation.
     * @param audioList The selected audio list for the creation.
     * @param imageList The selected image list for the creation.
     */
    public void setUp(String term, ArrayList<String> audioList, ArrayList<String> imageList,
                      String musicSelection) {
        _term = term;
        _audioList = audioList;
        _imageList = imageList;
        _musicSelection = musicSelection;

        setUpDefaultName();
    }

    /**
     * Set up a default name for the creation so that users will not have to name it if they do
     * not want to.
     */
    private void setUpDefaultName() {
        File termFolder =
                new File(Folders.CREATION_PRACTICE_FOLDER.getPath() + File.separator + _term);

        int fileNumber = 1;
        if (termFolder.exists()) {
            fileNumber = termFolder.listFiles().length+1;
        }

        _nameInput.setText(_term + fileNumber);
    }

    /**
     * Checks if name is valid. If it overlaps with an existing creation, ask for confirmation for
     * overwriting.
     */
    @FXML private void create() {

        // check for correct input
        _name = _nameInput.getText();

        if (_name.isEmpty()) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");

        } else if (!_name.matches("[A-Za-z0-9]+")) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name can only contain " +
                    "a-A and 0-9.");

        } else if (new File(Folders.CREATION_PRACTICE_FOLDER.getPath() + File.separator + _name +
                ".mp4").isFile()) {

            // check if want to overwrite
            Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Warning", "Creation already " +
                    "exists",
                    "Would you like to overwrite the existing file?").getAlert();
            if (alert.getResult() == ButtonType.OK) {
                createCreation();
            }

        } else {
            createCreation();
        }
    }

    /**
     * Starts creating the creation in the background.
     */
    private void createCreation() {

        // use new thread to create in bg
        _task = new CreateCreationTask(_name, _term, _audioList, _imageList, _musicSelection,
                _mainApp);

        new Thread(_task).start();

        // display indicator so user knows is loading
        _indicator.setVisible(true);

    }


    /**
     * Asks for confirmation being returning to the main menu.
     */
    @FXML private void mainMenuPress() {

        Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Warning", "Return to Main Menu?",
                "Any unfinished progress will be lost").getAlert();

        if (alert.getResult() == ButtonType.OK) {
            // cancel current task before going back to main menu
            if (_task != null) {
                _task.cancel();
            }
            mainMenu();
        } else {
            _task.notify();
        }
    }

    /**
     * If the user presses enter, it will be the same as clicking create.
     * @param keyEvent The event triggered by a key press.
     */
    @FXML private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            create();
        }
    }

}
