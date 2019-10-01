package main.java.tasks;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import main.java.application.AlertFactory;
import main.java.application.WikiApplication;

/**
 * Creates the video creation.
 * @author Milk
 *
 */
public class CreateAudioTask extends Task<Void>{
	private String _voice=null;
	private String _name;
	private String _term;
	private String _text;
	private WikiApplication _mainApp;

	private File _audioFolder;
	private Process _process;

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
			_audioFolder = new File(audioFolder);
			_audioFolder.mkdirs();


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

		deleteEmptyFolder();

		Platform.runLater(() -> {
			new AlertFactory(AlertType.ERROR, "Error", "Something went wrong", "Could not make audio file.");
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
		deleteEmptyFolder();
		
		Platform.runLater(() -> {
			Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Next", "Would you like to make another audio?",
					"Press 'OK'. Otherwise, press 'Cancel' to return to the main menu").getAlert();

			if (alert.getResult() == ButtonType.OK) {
				//go to preview scene again
				//NOT DONE YET
				_mainApp.setAudioScene();
			} else {
				_mainApp.displayMainMenuScene();
			}
		});

	}

	private void deleteEmptyFolder() {
		if (_audioFolder != null && _audioFolder.exists() && _audioFolder.listFiles().length == 0) {
			_audioFolder.delete();
		}
	}


}
