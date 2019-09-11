package main.java.tasks;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;
import main.java.application.WikiApplication;

/**
 * Creates the video creation.
 * @author Milk
 *
 */
public class CreateAudioTask extends Task<Void>{

	String _name;
	String _term;
	String _text;
	WikiApplication _mainApp;
	
	int _lineNumber;
	Process _process;

	public CreateAudioTask(String name,String term, String text, WikiApplication mainApp) {
		_term = term;
		_name = name;
		_text = text;
		_mainApp = mainApp;
	}

	@Override
	protected Void call() {

		try {
			String s = File.separator;
			
			// make the directory for the term if it doesn't already exist
			new File(System.getProperty("user.dir") + s + "bin" + s + "audio" + s + _term).mkdirs();
			
			_process = new ProcessBuilder("bash", "-c", 
					// create audio (festival) file
					  "echo \""+_text+"\" | text2wave -o " +
					  System.getProperty("user.dir") + s + "bin" + s + "audio" + s +
					  _term + s + _name + ".wav"
					  ).start();
			
			try {
				_process.waitFor();
				
			} catch (InterruptedException e) {
				// don't do anything
			}
			
			if (_process.exitValue() != 0) {
				Platform.runLater(() -> {
					new AlertMaker(AlertType.ERROR, "Error", "Something went wrong", "Could not make audio file.");
				});
				_process.destroy();
				this.cancel();
			}
			
		} catch (IOException e) {
			this.cancel();
			_process.destroy();
		}
		
		return null;
	}
	
	/**
	 * destroys the current process.
	 */
	public void destroyProcess() {
		_process.destroy();
	}
	
	@Override
	public void running() {
		Platform.runLater(() -> {
			_mainApp.displayLoadingCreateAudioScene(this);
		});
	}
	
	@Override
	public void succeeded() {
		Platform.runLater(() -> {
			new AlertMaker(AlertType.INFORMATION, "Completed", "Creation completed", "Press OK to exit to the main menu.");
			_mainApp.displayMainMenu();
		});
		
	}
	
	@Override
	public void cancelled() {
		Platform.runLater(() -> {
			new AlertMaker(AlertType.ERROR, "Error", "Input invalid", "The input is valid.");
			_mainApp.displayMainMenu();
		});
	}

}
