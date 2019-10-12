package main.java.controllers.quiz;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import main.java.application.Folders;
import main.java.controllers.Controller;
import main.java.controllers.view.VideoPlayer;

import java.io.File;
import java.lang.reflect.Array;

public class Quiz extends Controller {

    // delegate for delegation pattern. Takes care of most of the video-playing.
    private VideoPlayer _videoPlayer;
    private MediaPlayer _player;

    @FXML
    private Slider _volSlider;

    @FXML
    private Slider _timeSlider;

    @FXML
    private Label _playTime;

    @FXML
    private Label _maxTime;

    @FXML
    private MediaView _videoViewer;

    @FXML
    private CheckBox _includeMastered;

    @FXML
    private HBox _contentContainer;

    @FXML
    private Button _playPauseBtn;

    @FXML
    private void initialize() {
        // create the score folder
        new File(Folders.CreationScoreFolder.getPath()).mkdirs();

        _videoPlayer = new VideoPlayer();

        displayNotMasteredCreation();

        // randomise which term to test the user
    }

    private void displayNotMasteredCreation() {
        File notMasteredFolder = new File(Folders.CreationScoreNotMasteredFolder.getPath());

        int numberOfTerms = 0;
        if (notMasteredFolder.listFiles() != null) {
            numberOfTerms = notMasteredFolder.listFiles().length;
        }

        if (numberOfTerms == 0) {
            // delete the mediaview
            _contentContainer.getChildren().remove(_videoViewer);
            // add the text
            Text text = new Text("You've mastered everything! Practice what you've mastered or " +
                    "create new creations.");
            _contentContainer.getChildren().add(text);
        } else {
            // choose a random term for user
            int randomTermIndex = (int) (Math.random() * numberOfTerms);
            File randomTermFolder = (File) Array.get(notMasteredFolder.listFiles(),
                    randomTermIndex);

            // choose a random creation for the term
            int numberOfCreations = randomTermFolder.listFiles().length;
            int randomCreationIndex = (int) (Math.random() * numberOfCreations);
            File randomCreation = (File) Array.get(randomTermFolder.listFiles(),
                    randomCreationIndex);

            _player = _videoPlayer.createPlayer(randomCreation, _playPauseBtn);
            _videoViewer.setMediaPlayer(_player);
        }
    }

    @FXML
    private void playPause() {
        _videoPlayer.playPauseFunctionality(_player, _playPauseBtn);
    }

    @FXML
    private void fastForward() {
        _videoPlayer.fastForwardFunctionality(_player);
    }

    @FXML
    private void slowDown() {
        _videoPlayer.slowDownFunctionality(_player);
    }

    @FXML
    private void forward() {
        _videoPlayer.forwardFunctionality(_player);
    }

    @FXML
    private void backward() {
        _videoPlayer.backwardFunctionality(_player);
    }

    @FXML
    private void includeMastered() {

    }

    @FXML
    private void confirm() {

    }

    @FXML
    private void clearProgress() {

    }

    @FXML
    private void mainMenuPressed() {
        _player.stop();

        mainMenu();
    }
}
