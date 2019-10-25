package main.java.controllers.createCreation;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.application.StringManipulator;
import main.java.controllers.Controller;
import main.java.tasks.ViewAudioTermsTask;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Controller for AudioSearch. Allows the user to search for existing audio creations.
 *
 * @author Milk, OverCry
 */
public class AudioSearch extends Controller {

    @FXML private ListView<String> _wikitTerm;
    @FXML private Button _enterBtn;

    /**
     * Checks if any audio has been made and display possible Wikipedia terms
     * else return to main menu
     */
    public void initialize() {

        ViewAudioTermsTask searchTask = new ViewAudioTermsTask();
        new Thread(searchTask).start();

        try {
            ObservableList<String> folderList = searchTask.get();

            if (folderList.isEmpty()) {
                _wikitTerm.setVisible(false);
                _enterBtn.setVisible(false);

            } else {

                _wikitTerm.setItems(folderList);
            }


        } catch (InterruptedException e) {
            // probably intended, don't do anything
        } catch (ExecutionException e) {
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error", "Execution Exception", "Could not " +
                        "display creations properly");
            });
        }

    }

    /**
     * Checks if audio files exists and passes the list of audio into the new scene
     */
    @FXML private void enter() {

        //assume all made directors have SOMETHING in it
        String term = _wikitTerm.getSelectionModel().getSelectedItem();
        if (term != null) {
            StringManipulator edit = new StringManipulator();
            term = edit.removeNumberedLines(term);

            // check if audio files exists
            File file =
                    new File(Folders.AUDIO_PRACTICE_FOLDER.getPath()+ File.separator + term);

            if (file.isDirectory() && Objects.requireNonNull(file.list()).length > 0) {

                _mainApp.displayCreateCreationChooseAudioScene(term);

            } else {
                new AlertFactory(AlertType.ERROR, "Error", "Audio files do not exist.",
                        "You need to create audio files for this wikit term first.");
            }
        } else {
            new AlertFactory(AlertType.ERROR, "Error", "Audio wikit not selected.",
                    "Please select a wikit search");
        }
    }

    @FXML private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            enter();
        }
    }

}
