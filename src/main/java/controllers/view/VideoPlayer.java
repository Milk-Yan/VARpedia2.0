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
 * Controller for functionality of VideoPlayer.fxml. Allows the user to play a creation.
 *
 * @author Milk, OverCry
 */
public class VideoPlayer extends Controller {

    @FXML private MediaView _viewer;
    @FXML private Button _playPauseBtn;
    @FXML private Slider _volSlider;
    @FXML private Slider _timeSlider;
    @FXML private Label _maxTime;
    @FXML private Label _playTime;

    private MediaPlayer _player;

    /**
     * Initial setup for video playback. Video starts automatically.
     *
     * @param videoFile The video file to play.
     */
    public void setUp(File videoFile) {

        _player = createPlayer(videoFile, _playPauseBtn, _timeSlider, _volSlider, _playTime, _maxTime);
        _viewer.setMediaPlayer(_player);

        // create a new window for the VideoPlayer
        Stage stage = new Stage();
        stage.setTitle(videoFile.getName());
        stage.setScene(_viewer.getScene());
        stage.setX(700);
        stage.setY(300);

        // make the video stop when the window is closed.
        stage.setOnCloseRequest(closeEvent -> {
            stopPlayer();
        });

        stage.show();

    }

    /**
     * Set up the time and volume sliders for the video.
     * @param timeSlider The JavaFX Slider for the time.
     * @param volSlider The JavaFX Slider for the volume.
     * @param player The JavaFX MediaPlayer for the video.
     */
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

    /**
     * Update the value of the time slider and label when the MediaPlayer time has changed.
     * @param currentTime The current time of the MediaPlayer.
     * @param timeSlider The JavaFX slider to set the time.
     * @param playTime The play time label.
     */
    private void updateValues(Duration currentTime, Slider timeSlider, Label playTime) {
        timeSlider.setValue(currentTime.toMillis());
        formatTime(playTime, currentTime);
    }

    /**
     * Format the time of the label according to the current duration.
     * @param label The label to format.
     * @param duration The time to format the label to.
     */
    private void formatTime(Label label, Duration duration) {

        double durationMillis = duration.toMillis();
        int minutes = (int) Math.floor(durationMillis/1000/60);
        int seconds = (int) Math.floor(durationMillis/1000 - minutes*60);
        String minutesStr;
        String secondsStr;

        // Make the format mm:ss
        if (minutes > 9) {
            minutesStr = String.valueOf(minutes);
        } else {
            minutesStr = "0" + minutes;
        }

        if (seconds > 9) {
            secondsStr = String.valueOf(seconds);
        } else {
            secondsStr = "0" + seconds;
        }

        label.setText(minutesStr + ":" + secondsStr);
    }

    /**
     * Functionality of the play/pause button to allow the user to pause/play the video.
     */
    @FXML private void playPause() {
        playPauseFunctionality(_player, _playPauseBtn);
    }

    /**
     * When given a player and the play pause button, checks the status of the player and
     * play/pauses the video accordingly.
     * @param player The JavaFX MediaPlayer of the video.
     * @param playPauseBtn The play pause button of the scene.
     */
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
     * Functionality of the fast forward button to allow the user to increase the speed of
     * playback by 0.1 times each time the user presses the button
     */
    @FXML private void fastForward() {
        fastForwardFunctionality(_player);
    }

    /**
     * When given a player, increases the rate of the player by 0.1.
     * @param player The player whose rate to increase.
     */
    public void fastForwardFunctionality(MediaPlayer player) {
        double fasterRate = player.getRate() + 0.1;
        player.setRate(fasterRate);
    }

    /**
     * Functionality of the slow down button to allow the user to decrease the speed of
     * playback by 0.1 times each time the user presses the button
     */
    @FXML private void slowDown() {
        slowDownFunctionality(_player);
    }

    /**
     * When given a player, decreases the rate of the player by 0.1.
     * @param player The player whose rate to decrease.
     */
    public void slowDownFunctionality(MediaPlayer player) {
        double slowerRate = player.getRate() - 0.1;

        // if rate is already the slowest, don't make the video stop.
        if (slowerRate != 0) {
            player.setRate(slowerRate);
        }
    }

    /**
     * Functionality of the forward button to allow the user to skip forward 5 seconds in the video.
     */
    @FXML private void forward() {
        forwardFunctionality(_player);
    }

    /**
     * When given a player, changes the time of the player to 5 seconds later.
     * @param player The player whose time to change.
     */
    public void forwardFunctionality(MediaPlayer player) {
        Duration newTime = player.getCurrentTime().add(Duration.seconds(5));
        player.seek(newTime);
    }

    /**
     * Functionality of the backward button to allow the user to go backward 5 seconds in the
     * video.
     */
    @FXML private void backward() {
        backwardFunctionality(_player);

    }

    /**
     * When given a player, changes the time of the player to 5 seconds earlier.
     * @param player The player whose time to change.
     */
    public void backwardFunctionality(MediaPlayer player) {
        Duration newTime = player.getCurrentTime().subtract(Duration.seconds(5));
        player.seek(newTime);
    }

    /**
     * Creates the video player from given parameters.
     * @param mediaFile The file to play.
     * @param playPauseBtn The button to play and pause the video.
     * @param timeSlider The slider to display and change the time of the player.
     * @param volSlider The slider to display and change volume of the player.
     * @param playTime The label to display the current time of the player.
     * @param maxTime The label to display the total duration of the player.
     * @return The MediaPlayer created.
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
            stopPlayer();
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

    /**
     * When the user presses the enter key, it will be as if they had clicked the play/pause
     * button. Likewise, the left key corresponds to the backward button, and the right key
     * corresponds to the forward button.
     * @param keyEvent The event triggered on a key press.
     */
    @FXML private void onKey(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) || keyEvent.getCode().equals(KeyCode.SPACE)) {
            playPause();
        } else if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            backward();
        } else if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            forward();
        }
    }

    /**
     * Stops the current player.
     */
    private void stopPlayer() {
        if (_player != null) {
            _player.stop();
        }
    }

}
