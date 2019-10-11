package main.java.controllers.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.java.controllers.Controller;

import java.io.File;
import java.nio.file.Paths;

/**
 * Controller for functionality of VideoPlayer.fxml
 *
 * @author Milk
 */
public class VideoPlayer extends Controller {

    @FXML
    private MediaView _viewer;

    @FXML
    private Button _playPauseBtn;

    @FXML
    private Slider _volSlider;

    @FXML
    private Slider _timeSlider;

    @FXML
    private Label _maxTime;

    @FXML
    private Label _playTime;


    private String _videoName;
    private MediaPlayer _player;

    /**
     * initial setup for video playback. Video starts automatically
     *
     * @param videoName
     */
    public void setUp(String videoName) {
        _videoName = videoName;

        MediaPlayer player = createPlayer();
        _viewer.setMediaPlayer(player);

        // create a new window for the VideoPlayer
        Stage stage = new Stage();
        stage.setTitle(_videoName);
        stage.setScene(_viewer.getScene());
        stage.setX(700);
        stage.setY(300);

        // add listener for media player that will trigger the sliders
        _player.currentTimeProperty().addListener(
                (observable, oldTime, newTime) -> updateValues(newTime));


        _player.setOnReady(() -> {
            setUpSliders();

            // set up the duration time for the player label
            formatTime(_maxTime, _player.getTotalDuration());
            _playTime.setText("00:00");

            updateValues(Duration.ZERO);
        });


        stage.setOnCloseRequest(closeEvent -> {
            player.stop();
        });

        stage.show();

    }

    private void setUpSliders() {

        // set up time slider
        _timeSlider.setMin(0);
        _timeSlider.setMax(_player.getTotalDuration().toMillis());

        // set up vol slider
        _volSlider.setValue(50);

        // add listener for time slider
        _timeSlider.valueProperty().addListener(observable -> {
            if (_timeSlider.isPressed()) {
                _player.seek(new Duration(_timeSlider.getValue()));
            }
        });

        // add listener for volume
        _volSlider.valueProperty().addListener(observable -> {
            _player.setVolume(_volSlider.getValue()/100.0);
        });
    }

    private void updateValues(Duration currentTime) {
        _timeSlider.setValue(currentTime.toMillis());
        formatTime(_playTime, currentTime);
    }

    private void formatTime(Label label, Duration duration) {
        Double durationMillis = duration.toMillis();
        int minutes = (int) Math.floor(durationMillis/1000/60);
        int seconds = (int) Math.floor(durationMillis/1000 - minutes*60);
        String minutesStr;
        String secondsStr;

        if (minutes > 9) {
            minutesStr = String.valueOf(minutes);
        } else {
            minutesStr = "0" + String.valueOf(minutes);
        }

        if (seconds > 9) {
            secondsStr = String.valueOf(seconds);
        } else {
            secondsStr = "0" + String.valueOf(seconds);
        }

        label.setText(minutesStr + ":" + secondsStr);
    }

    /**
     * button to allow the user to pause/play the video
     */
    @FXML
    private void playPause() {
        if (_player.getStatus() == Status.PAUSED || _player.getStatus() == Status.STOPPED) {
            _player.play();
            _playPauseBtn.setText("Pause");
        } else if (_player.getStatus() == Status.PLAYING) {
            _player.pause();
            _playPauseBtn.setText("Play");
        }
    }

    /**
     * button to allow video playback speed to increase
     */
    @FXML
    private void fastForward() {
        double fasterRate = _player.getRate() + 0.5;
        _player.setRate(fasterRate);
    }

    /**
     * button to allow video playback speed to decrease
     */
    @FXML
    private void slowDown() {
        double slowerRate = _player.getRate() - 0.5;

        // if rate is already the slowest, don't make the video stop.
        if (slowerRate != 0) {
            _player.setRate(slowerRate);
        }
    }

    /**
     * button to allow video to skip 5 seconds
     */
    @FXML
    private void forward() {
        Duration newTime = _player.getCurrentTime().add(Duration.seconds(5));
        _player.seek(newTime);
    }

    /**
     * button to allow video to return 5 seconds
     */
    @FXML
    private void backward() {
        Duration newTime = _player.getCurrentTime().subtract(Duration.seconds(5));
        _player.seek(newTime);

    }

    /**
     * button to mute audio
     */
    @FXML
    private void mute() {
        if (_player.isMute()) {
            _player.setMute(false);
        } else {
            _player.setMute(true);
        }
    }

    /**
     * initialize the video playing
     *
     * @return
     */
    private MediaPlayer createPlayer() {
        Media video =
                new Media(Paths.get("bin" + File.separator + "creations" + File.separator +
                        _videoName + ".mp4").toUri().toString());
        _player = new MediaPlayer(video);
        _player.setAutoPlay(true);

        // Media functionality
        _player.setOnEndOfMedia(() -> {
            _playPauseBtn.setText("Play");
            _player.stop();
            _player.seek(Duration.ZERO);
        });

        return _player;
    }

}
