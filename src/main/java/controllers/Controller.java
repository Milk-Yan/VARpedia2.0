package main.java.controllers;

import javafx.fxml.FXML;
import main.java.application.Main;

/**
 * Controller abstract for all controller classes.
 * Each controller has reference to the same wikiApplication and is declared within this class.
 *
 * @author Milk, OverCry
 */
public abstract class Controller {

    protected Main _mainApp;

    public void setMainApplication(Main mainApp) {
        if (_mainApp == null) {
            _mainApp = mainApp;
        } else {
            throw new IllegalArgumentException("The main application of a controller cannot be " +
                    "set more than once.");
        }
    }

    /**
     * Changes the scene back to the main menu and discards all edits.
     */
    @FXML protected void mainMenu() {
        _mainApp.displayMainMenuScene();
    }
}
