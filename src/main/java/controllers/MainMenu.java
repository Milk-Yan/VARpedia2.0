package main.java.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.text.Text;
import main.java.application.AlertFactory;
import main.java.tasks.ViewAudioTask;
import main.java.tasks.ViewCreationsTask;
import main.java.tasks.ViewSearchTask;

import java.util.concurrent.ExecutionException;

/**
 * Controller for functionality of Main.fxml
 *
 * @author Milk
 */
public class MainMenu extends Controller {

    @FXML
    private Button _createCreationBtn;

    public void initialize() {
        ViewSearchTask searchTask = new ViewSearchTask();
        new Thread(searchTask).start();

        try {
            ObservableList<String> folderList = searchTask.get();

            if (folderList.isEmpty()) {
                _createCreationBtn.setDisable(true);
            } else {
                _createCreationBtn.setDisable(false);
            }


        } catch (InterruptedException | ExecutionException e) {

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

        _mainApp.displayQuizScene();

    }
}
