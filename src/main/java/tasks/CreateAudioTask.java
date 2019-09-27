package main.java.tasks;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
			new File(System.getProperty("user.dir") + s + "bin" + s + "audio" + s + _term).mkdirs();

			
			_process = new ProcessBuilder("bash", "-c",
					"echo -e \"(voice_" + _voice + ") ;; \n(utt.save.wave (SayText \\\"" +
							_text + "\\\" ) \\\""+
							System.getProperty("user.dir") + s + "bin" + s + "audio" + s + _term+s+
							_name+".wav"+"\\\" \'riff) \" | festival -i ").start();

//							System.getProperty("user.dir") +
//							File.separator + "bin" + File.separator + "audio" + _term + File.separator +
//							_name + ".wav").start();

//			echo -e "(voice_rab_diphone);; \n(utt.save.wave (SayText \"hello hello\") \"test.wav\" 'riff)" | festival -i


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
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Next", "Would you like to make another audio?",
					"Press 'OK'. Otherwise, press 'Cancel'").getAlert();

			if (alert.getResult() == ButtonType.OK) {
				//go to preview scene again
				//NOT DONE YET
			} else {
				_mainApp.displayMainMenuScene();
			}
		});
		
	}


}
