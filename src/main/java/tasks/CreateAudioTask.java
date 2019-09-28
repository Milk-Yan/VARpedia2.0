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
	String _voice=null;
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
	
	public CreateAudioTask(String name,String term, String text, WikiApplication mainApp, String voice) {
		_term = term;
		_name = name;
		_text = text;
		_mainApp = mainApp;
		_voice=voice;
	}

	@Override
	protected Void call() {

		try {
			String s = File.separator;
			
			// make the directory for the term if it doesn't already exist
			String audioFolder = System.getProperty("user.dir") + s + "bin" + s + "audio" + s + _term;
			new File(audioFolder).mkdirs();

			
			_process = new ProcessBuilder("bash", "-c",
					// set voice
					"echo \"(voice_" + _voice + ") "
					// create utterance
					+ "(set! utt1 (Utterance Text \\\"" + _text + "\\\")) "
					// synthesise utterance
					+ "(utt.synth utt1) "
					// save
					+ "(utt.save.wave utt1 \\\"" + audioFolder + s + _name + ".wav" +"\\\" \\`riff)\" | festival\n").start();


			try {
				_process.waitFor();
				
			} catch (InterruptedException e) {
				// don't do anything
			}
			
			if (_process.exitValue() != 0) {
				this.cancelled();
			}
			
		} catch (IOException e) {
			this.cancelled();
		}
		
		return null;
	}
	
	@Override
	public void cancelled() {
		_process.destroy();
		Platform.runLater(() -> {
			new AlertMaker(AlertType.ERROR, "Error", "Something went wrong", "Could not make audio file.");
		});
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
			_mainApp.displayMainMenuScene();
		});
		
	}


}
