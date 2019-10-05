package main.java.controllers;

import javafx.fxml.FXML;

/**
 * Controller for functionality of Main.fxml
 *
 * @author Milk
 */
public class MainMenu extends Controller {

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
}
