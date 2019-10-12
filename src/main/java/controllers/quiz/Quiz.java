package main.java.controllers.quiz;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.MediaView;
import main.java.application.Folders;
import main.java.controllers.Controller;
import main.java.controllers.view.VideoPlayer;

import java.io.File;

public class Quiz extends Controller {

    // delegate for delegation pattern. Takes care of most of the video-playing.
    private VideoPlayer _videoPlayer;

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
    private void initialize() {
        // create the score folder
        new File(Folders.CreationScoreFolder.getPath()).mkdirs();

        _videoPlayer = new VideoPlayer();

        displayNotMasteredCreation();

        // randomise which term to test the user
    }

    private void displayNotMasteredCreation() {
        
    }

    @FXML
    private void playPause() {

    }

    @FXML
    private void fastForward() {

    }

    @FXML
    private void slowDown() {

    }

    @FXML
    private void forward() {

    }

    @FXML
    private void backward() {

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

    }
}
