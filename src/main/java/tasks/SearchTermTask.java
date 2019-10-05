package main.java.tasks;

import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;

import main.java.application.AlertFactory;
import main.java.application.Main;
import main.java.application.StringManipulator;

/**
 * Searches for terms uses bash wikit on a new thread.
 * @author Milk
 *
 */
public class SearchTermTask extends Task<String>{

	private String _term;
	private String _searchResults;
	private boolean _isInvalid = false;
	private Main _mainApp;

	public SearchTermTask(String searchTerm, Main mainApp) {
		_term = searchTerm;
		_mainApp = mainApp;
	}

	@Override
	protected String call() throws Exception {
		try {

			StringManipulator manipulator = new StringManipulator();

			// wikit the term given
			Process process = new ProcessBuilder("bash", "-c", "wikit \"" + _term + "\"").start();

			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// don't do anything
			}

			if (process.exitValue() != 0) {

				// run on GUI thread
				Platform.runLater(() -> {
					new AlertFactory(AlertType.ERROR, "Error encountered", "Problem finding term.",
							manipulator.inputStreamToString(process.getErrorStream()));
				}); 

			} else {

				String text = manipulator.inputStreamToString(process.getInputStream());

				_searchResults = text;
			}


		} catch (IOException e) {
			// run on GUI thread
			Platform.runLater(() -> {
				new AlertFactory(AlertType.ERROR, "Error encountered", "I/O Exception", e.getStackTrace().toString());
				_mainApp.displayMainMenuScene();
			});
		}

		return null;
	}

	@Override
	protected void succeeded() {
		
		if (_searchResults.contains(":^(")) {
			_isInvalid = true;
			cancelled(); // run cancel operations
			return;
		}
		
		// run on GUI thread
		Platform.runLater(() -> {
			_mainApp.displayCreateAudioChooseTextScene(_term, _searchResults);
		});
		
	}

	@Override
	protected void cancelled() {
		if (_isInvalid) {
			// run on GUI thread
			Platform.runLater(() -> {
				new AlertFactory(AlertType.ERROR, "Error encountered", "Invalid term", "This term cannot be found. Please try again.");
				_mainApp.displayCreateAudioSearchScene();
			});
		}
		
	}


}
