package main.java.application;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.media.MediaView;

public class PreviewController {

	private String _previewText;
	Process _process;
	Thread _thread;
	
	
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
		
		_thread = new Thread(new playAudio());
		_pausePlayBtn.setText("Play");
		_previewText = WikiApplication.getInstance().getCurrentPreviewText();		
		
	}
	
	@FXML
	private void changeVoice() {
		
	}
	
	@SuppressWarnings("deprecation")
	@FXML
	private void pausePlay() {
		
		if (_thread.isAlive()) {
			if (_pausePlayBtn.getText().equals("Pause")) {
				_thread.destroy();
			}
		}
		
		if(!_thread.isAlive()) {
			_pausePlayBtn.setText("Pause");
	       	_thread.start(); 	
		} 

		
		

		
		

		
//		if(_pausePlayBtn.getText().equals("Play")) {
//			_thread.start();
//
//		} else if (_pausePlayBtn.getText().equals("Pause")) {
//			System.out.println("YAY");
//			_pausePlayBtn.setText("Play");
//		}
		

	}
	
	@FXML
	private void save() {
		
	}
	
	@FXML
	private void back() {
		WikiApplication.getInstance().displaySearchReturn();
		
	}
	
	private class playAudio extends Task<Void>{

		@Override
		protected Void call() throws Exception {
			
		
				try {
					_process= new ProcessBuilder("/bin/bash", "-c", "echo "+_previewText+" | festival --tts").start();

					try {
						_process.waitFor();
					} catch (InterruptedException e) {
						
					}

					if (_process.exitValue() != 0) {
						_process.destroy();
					}
					
					
				} catch (IOException e) {

					_process.destroy();
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
			
			
			return null;
		}
		
	}
	

}
