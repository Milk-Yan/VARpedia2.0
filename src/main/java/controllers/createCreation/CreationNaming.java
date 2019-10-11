package main.java.controllers.createCreation;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.controllers.Controller;
import main.java.tasks.CreateCreationTask;

import java.io.File;
import java.util.ArrayList;

/**
 * Controller for CreationNaming.fxml
 *
 * @author wcho400
 */
public class CreationNaming extends Controller {

    private String _name;
    private String _term;
    private ArrayList<String> _audioList;
    private ArrayList<String> _imageList;
    private CreateCreationTask _task;

    @FXML
    private TextField _nameInput;

    @FXML
    private ProgressIndicator _indicator;

    /**
     * initializes parameters to be passed on to the next scene
     *
     * @param term
     * @param audioList
     * @param imageList
     */
    public void setUp(String term, ArrayList<String> audioList, ArrayList<String> imageList) {
        _term = term;
        _audioList = audioList;
        _imageList = imageList;
    }

    /**
     * button to pass name
     * checks if name is valid
     * if overlaps with an existing creation, ask for confirmation for overwriting
     */
    @FXML
    private void create() {

        String s = File.separator;

        // check for correct input
        _name = _nameInput.getText();

        if (_name.isEmpty()) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name cannot be empty");

        } else if (_name.contains(" ")) {

            new AlertFactory(AlertType.ERROR, "Error", "Input invalid", "Name cannot contain " +
                    "spaces");

        } else if (new File(Folders.CreationPracticeFolder.getPath() + s + _name +
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
     * being task of creation creation
     * pass's task to allow cancellation if desired
     */
    private void createCreation() {

        // use new thread to create in bg
        _task = new CreateCreationTask(_name, _term, _audioList, _imageList,
                _mainApp);

        new Thread(_task).start();

        // display indicator so user knows is loading
        _indicator.setVisible(true);

    }


    /**
     * return to main menu
     * asks for confirmation being returning
     */
    @FXML
    private void mainMenuPress() {

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

}
