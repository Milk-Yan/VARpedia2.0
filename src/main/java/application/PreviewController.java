package main.java.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.media.MediaView;

public class PreviewController {

	@FXML
	private MediaView _audioPlay;
	
	@FXML
	private ChoiceBox<String> _voiceSelection;
	
	@FXML
	private Button _pausePlayBtn;
	
	@FXML
	private Button _replayBtn;
	
	@FXML
	private Button _saveBtn;
	
	@FXML
	private Button _backBtn;
	
	@FXML
	private void initialize() {
		
		
		
		String previewText = WikiApplication.getInstance().getCurrentPreviewText();
		
	}
	
	@FXML
	private void changeVoice() {
		
	}
	
	@FXML
	private void pausePlay() {
		
	}
	
	@FXML
	private void replay() {
		
	}
	
	@FXML
	private void save() {
		
	}
	
	@FXML
	private void back() {
		
	}
}
