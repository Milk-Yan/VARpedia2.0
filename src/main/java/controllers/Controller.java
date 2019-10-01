package main.java.controllers;

import javafx.fxml.FXML;
import main.java.application.WikiApplication;

/**
 * Controller abstract for all controller classes
 * Each controller has reference to the same wikiApplication
 * and is declared within this class
 * @author wcho400
 *
 */
public abstract class Controller {

	protected WikiApplication _mainApp;

	public void setMainApplication(WikiApplication mainApp) {
		if (_mainApp == null) {
			_mainApp = mainApp;
		} else {
			throw new IllegalArgumentException("The main application of a controller cannot be set more than once.");
		}
	}

	/**
	 * Changes the scene back to the main menu and discards all edits.
	 */
	@FXML
	protected void mainMenu() {
		_mainApp.displayMainMenuScene();
	}
}
