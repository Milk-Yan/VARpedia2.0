package main.java.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import main.java.application.Folders;

import java.io.File;
import java.util.Objects;

/**
 * Controller for functionality of Main.fxml
 *
 * @author Milk, OverCry
 */
public class MainMenu extends Controller {

    @FXML private Button _createCreationBtn;
    @FXML private Button _quizBtn;
    @FXML private Button _viewCreationsBtn;

    /**
     * Called immediately after the root element of the scene is processed by the FXMLLoader.
     * Note that FXML elements may have not been initialized yet and therefore should not be
     * called from this method.
     */
    @FXML public void initialize() {
        disableInvalidButtons();
    }

    /**
     * Functionality of create audio button. Takes the user to a screen to start the creation of
     * an audio creation.
     */
    @FXML private void createAudio() {

        _mainApp.displayCreateAudioSearchScene();

    }

    /**
     * Functionality of create creation button. Takes the user to a screen to start the creation of
     * a video creation.
     */
    @FXML private void createCreation() {

        _mainApp.displayCreateCreationSearchScene();
    }

    /**
     * Functionality of view creations button. Takes the user to a screen showing all current
     * audio and video creations.
     */
    @FXML private void viewCreations() {

        _mainApp.displayViewCreationsScene();

    }

    /**
     * Functionality of view quiz button. Takes the user to a screen to start the quiz on created
     * creations.
     */
    @FXML private void viewQuiz(){

        _mainApp.displayQuizScene(false);

    }

    /**
     * Disables specific buttons if it doesn't make sense for these to be enabled.
     */
    private void disableInvalidButtons() {

        // check if audio exists so creations can to be created
        File audioFolder = Folders.AUDIO_PRACTICE_FOLDER.getFile();
        if (!audioFolder.exists() || Objects.requireNonNull(audioFolder.list()).length == 0) {
            _createCreationBtn.setDisable(true);
        }

        // check if creations exist so there is something to be quizzed on
        File creationFolder = Folders.CREATION_PRACTICE_FOLDER.getFile();
        if (!creationFolder.exists() || Objects.requireNonNull(creationFolder.list()).length == 0) {
            _quizBtn.setDisable(true);
        }

        // if there are no audio and no creations then there is no reason to view
        if (_createCreationBtn.isDisabled() && _quizBtn.isDisabled()) {
            _viewCreationsBtn.setDisable(true);
        }
    }

}
