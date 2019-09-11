package main.java.controllers;

import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

/**
 * Controller for functionality of VideoPlayer.fxml
 * @author Milk
 *
 */
public class VideoPlayerController extends Controller{

	@FXML
	private MediaView _viewer;
	
	@FXML
	private Button _playPauseBtn;
	
	@FXML
	private Button _fastForwardBtn;
	
	@FXML
	private Button _slowDownBtn;
	
	@FXML
	private Button _forwardBtn;
	
	@FXML
	private Button _backwardBtn;
	
	@FXML
	private Button _muteBtn;
	
	private MediaPlayer _player;
	

	/**
	 * Initialises the player to show what the user chose.
	 */
	@FXML
	public void initialize() {

		_player = WikiApplication.getInstance().getCurrentPlayer();

		_viewer.setMediaPlayer(_player);

	}
	
	@FXML
	private void playPause() {
		if (_player.getStatus() == Status.PAUSED) {
			_player.play();
			_playPauseBtn.setText("Pause");
		} else if (_player.getStatus() == Status.PLAYING) {
			_player.pause();
			_playPauseBtn.setText("Play");
		}
	}
	
	@FXML
	private void fastForward() {
		Double fasterRate = _player.getRate() + 0.5;
		_player.setRate(fasterRate);
	}
	
	@FXML
	private void slowDown() {
		Double slowerRate = _player.getRate() - 0.5;
		
		// if rate is already the slowest, don't make the video stop.
		if (slowerRate != 0) {
			_player.setRate(slowerRate);
		}
	}
	
	@FXML
	private void forward() {
		Duration newTime = _player.getCurrentTime().add(Duration.seconds(5));
		_player.seek(newTime);
	}
	
	@FXML
	private void backward() {
		Duration newTime = _player.getCurrentTime().subtract(Duration.seconds(5));
		_player.seek(newTime);
		
	}
	
	@FXML
	private void mute() {
		if (_player.isMute()) {
			_player.setMute(false);
		} else {
			_player.setMute(true);
		}
	}
	
}
