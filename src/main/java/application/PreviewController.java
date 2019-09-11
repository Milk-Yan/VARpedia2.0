package main.java.application;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.media.MediaView;

public class PreviewController {

	private String _previewText;
	private Process _process;
	private Thread _thread;
	private String _searchTerm;
	
	
	@FXML
	private MediaView _audioPlay;
	
	@FXML
	private ChoiceBox<String> _voiceSelection;
	
	@FXML
	private Button _pausePlayBtn;
	
	@FXML
	private Button _saveBtn;
	
	@FXML
	private Button _backBtn;
	
	@FXML
	private void initialize() {

		_previewText = WikiApplication.getInstance().getCurrentPreviewText();
		_searchTerm=WikiApplication.getInstance().getCurrentTerm();

	}
	
	@FXML
	private void changeVoice() {
		
	}


	@FXML
	private void pausePlay() throws InterruptedException {
		
		if(_pausePlayBtn.getText().equals("Play")) {
			_thread = new Thread(new playAudio());
			_thread.start();
			
		}

	}
	
	@FXML
	private void save() {
		new File("./bin/audio/"+_searchTerm).mkdirs();
		Thread _audioThread = new Thread(new makeAudio());
		_audioThread.start();
		//Temporarily goes back to the full search kappa change this later
		back();
		
	}
	
	@FXML
	private void back() {
		WikiApplication.getInstance().displaySearchReturn();
		
	}
	
	private class playAudio extends Task<Void>{

		@Override
		protected Void call() throws Exception {
			

			//you cant stop the festival speaker since its from bash
				try {
					_process= new ProcessBuilder("/bin/bash", "-c", "echo "+_previewText+" | festival --tts").start();

				} catch (IOException e) {

					_process.destroy();

				}

			return null;
		}
	}
	
	private class makeAudio extends Task<Void>{

		@Override
		protected Void call() throws Exception {
			System.out.println("WORKS?");
			System.out.println(_previewText);
			System.out.println("/bin/bash -c "+
					"echo \""+_previewText+"\" | text2wave >"+_searchTerm+".wav;");
			_process = new ProcessBuilder("/bin/bash", "-c",
					"echo \""+_previewText+"\" | text2wave >"+_searchTerm+".wav; mv "+_searchTerm+".wav ./bin/audio/"+_searchTerm+"/").start();
			return null;
		}

	}

}
