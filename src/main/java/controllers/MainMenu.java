package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.application.Folders;

import java.io.File;

/**
 * Controller for functionality of Main.fxml
 *
 * @author Milk
 */
public class MainMenu extends Controller {

    @FXML
    private Button _createCreationBtn;

    @FXML
    private Button _quizBtn;

    @FXML
    private Button _viewCreationsBtn;

    @FXML
    public void initialize() {
        disableInvalidButtons();
    }

    private void disableInvalidButtons() {

        // check if audio exists so creations can to be created
        File audioFolder = new File(Folders.AudioPracticeFolder.getPath());
        if (!audioFolder.exists() || audioFolder.list().length == 0) {
            _createCreationBtn.setDisable(true);
        }

        // check if creations exist so there is something to be quizzed on
        File creationFolder = new File(Folders.CreationPracticeFolder.getPath());
        if (!creationFolder.exists() || creationFolder.list().length == 0) {
            _quizBtn.setDisable(true);
        }

        // if there are no audio and no creations then there is no reason to view
        if (_createCreationBtn.isDisabled() && _quizBtn.isDisabled()) {
            _viewCreationsBtn.setDisable(true);
        }
    }

    /**
     * button to start audio creation
     */
    @FXML
    private void createAudio() {

        _mainApp.displayCreateAudioSearchScene();

    }

    /**
     * button to start visual creation
     */
    @FXML
    private void createCreation() {

        _mainApp.displayCreateCreationSearchScene();
    }

    /**
     * button to display creations
     */
    @FXML
    private void viewCreations() {

        _mainApp.displayViewCreationsScene();

    }

    @FXML
    private void viewQuiz(){

        _mainApp.displayQuizScene(false);

    }
}
