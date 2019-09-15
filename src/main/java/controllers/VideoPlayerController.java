package main.java.controllers;

import java.nio.file.Paths;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import javafx.stage.Stage;
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

	private String _videoName;
	private MediaPlayer _player;
	private Stage _stage;


	public void setUp(String videoName) {
		_videoName = videoName;

		MediaPlayer player = createPlayer();
		_viewer.setMediaPlayer(player);

		// create a new window for the VideoPlayer
		_stage = new Stage();
		_stage.setScene(_viewer.getScene());

		_stage.setOnCloseRequest(closeEvent -> {
			player.stop();
		});
		
		_stage.show();

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

	private MediaPlayer createPlayer() {
		Media video = new Media(Paths.get("bin/creations/" + _videoName + ".mp4").toUri().toString());
		_player = new MediaPlayer(video);
		_player.setAutoPlay(true);

		// Media functionality
		_player.setOnEndOfMedia(() -> {
			_player.dispose();
			_stage.close();
		});

		return _player;
	}

}
