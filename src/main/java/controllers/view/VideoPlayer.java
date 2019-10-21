package main.java.controllers.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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


    private File _videoFile;
    private MediaPlayer _player;

    /**
     * initial setup for video playback. Video starts automatically
     *
     * @param videoFile
     */
    public void setUp(File videoFile) {
        _videoFile = videoFile;

        _player = createPlayer(_videoFile, _playPauseBtn, _timeSlider, _volSlider, _playTime, _maxTime);
        _viewer.setMediaPlayer(_player);

        // create a new window for the VideoPlayer
        Stage stage = new Stage();
        stage.setTitle(_videoFile.getName());
        stage.setScene(_viewer.getScene());
        stage.setX(700);
        stage.setY(300);


        stage.setOnCloseRequest(closeEvent -> {
            _player.stop();
        });

        stage.show();

    }

    public void setUpSliders(Slider timeSlider, Slider volSlider, MediaPlayer player) {

        // set up time slider
        timeSlider.setMin(0);
        timeSlider.setMax(player.getTotalDuration().toMillis());

        // set up vol slider
        volSlider.setValue(50);

        // add listener for time slider
        timeSlider.valueProperty().addListener(observable -> {
            if (timeSlider.isPressed()) {
                player.seek(new Duration(timeSlider.getValue()));
            }
        });

        // add listener for volume
        volSlider.valueProperty().addListener(observable -> {
            player.setVolume(volSlider.getValue()/100.0);
        });
    }

    private void updateValues(Duration currentTime, Slider timeSlider, Label playTime) {
        timeSlider.setValue(currentTime.toMillis());
        formatTime(playTime, currentTime);
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
        playPauseFunctionality(_player, _playPauseBtn);
    }

    public void playPauseFunctionality(MediaPlayer player, Button playPauseBtn) {
        if (player.getStatus() != Status.PLAYING) {
            player.play();
            playPauseBtn.setText("Pause");
        } else {
            player.pause();
            playPauseBtn.setText("Play");
        }
    }

    /**
     * button to allow video playback speed to increase
     */
    @FXML
    private void fastForward() {
        fastForwardFunctionality(_player);
    }

    public void fastForwardFunctionality(MediaPlayer player) {
        double fasterRate = player.getRate() + 0.5;
        player.setRate(fasterRate);
    }

    /**
     * button to allow video playback speed to decrease
     */
    @FXML
    private void slowDown() {
        slowDownFunctionality(_player);
    }

    public void slowDownFunctionality(MediaPlayer player) {
        double slowerRate = player.getRate() - 0.5;

        // if rate is already the slowest, don't make the video stop.
        if (slowerRate != 0) {
            player.setRate(slowerRate);
        }
    }

    /**
     * button to allow video to skip 5 seconds
     */
    @FXML
    private void forward() {
        forwardFunctionality(_player);
    }

    public void forwardFunctionality(MediaPlayer player) {
        Duration newTime = player.getCurrentTime().add(Duration.seconds(5));
        player.seek(newTime);
    }

    /**
     * button to allow video to return 5 seconds
     */
    @FXML
    private void backward() {
        backwardFunctionality(_player);

    }

    public void backwardFunctionality(MediaPlayer player) {
        Duration newTime = player.getCurrentTime().subtract(Duration.seconds(5));
        player.seek(newTime);
    }

    /**
     * initialize the video playing
     *
     * @return
     */
    public MediaPlayer createPlayer(File mediaFile, Button playPauseBtn, Slider timeSlider,
                                    Slider volSlider, Label playTime, Label maxTime) {
        Media video =
                new Media(Paths.get(mediaFile.getPath()).toUri().toString());
        MediaPlayer player = new MediaPlayer(video);
        player.setAutoPlay(true);

        // Media functionality
        player.setOnEndOfMedia(() -> {
            playPauseBtn.setText("Play");
            player.stop();
            player.seek(Duration.ZERO);
        });

        // add listener for media player that will trigger the sliders
        player.currentTimeProperty().addListener(
                (observable, oldTime, newTime) -> updateValues(newTime, timeSlider, playTime));


        player.setOnReady(() -> {
            setUpSliders(timeSlider, volSlider, player);

            // set up the duration time for the player label
            formatTime(maxTime, player.getTotalDuration());
            playTime.setText("00:00");

            updateValues(Duration.ZERO, timeSlider, playTime);
        });

        return player;
    }

    @FXML
    private void onKey(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.SPACE)) {
            playPause();
        } else if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            backward();
        } else if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            forward();
        }
    }

}
