package main.java.controllers.quiz;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.application.StringManipulator;
import main.java.controllers.Controller;
import main.java.controllers.view.VideoPlayer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private Button _confirmBtn;

    @FXML
    private TextField _termInput;

    @FXML
    private Label _correctionLabel;

    @FXML
    private Label _promptLabel;

    @FXML
    private Pane _buttonPane;

    @FXML
    private void initialize() {
        // create the score folder
        new File(Folders.CREATION_SCORE_FOLDER.getPath()).mkdirs();

        _videoPlayer = new VideoPlayer();

        displayNotMasteredCreation();

    }

    public void setIncludeMastered(boolean includeMastered) {
        if (includeMastered) {
            includeMastered();
        }
    }

    private void displayNotMasteredCreation() {
        stopCurrentPlayer();
        File notMasteredFolder = new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath());

        int numberOfTerms = 0;
        if (notMasteredFolder.listFiles() != null) {
            numberOfTerms = notMasteredFolder.listFiles().length;
        }

        if (numberOfTerms == 0) {
            setVisibilityOfVideo(false);
        } else {
            setVisibilityOfVideo(true);
            ArrayList<File> listOfFiles = new ArrayList<File>();
            for (File notMasteredScoreFile: notMasteredFolder.listFiles()) {
                listOfFiles.add(new File(Folders.CREATION_TEST_FOLDER.getPath() + File.separator + notMasteredScoreFile.getName().replace(".txt", "")));
            }
            File randomCreation = getRandomCreationFile(numberOfTerms,
                    listOfFiles.toArray(new File[listOfFiles.size()]));

            _player = _videoPlayer.createPlayer(randomCreation, _playPauseBtn, _timeSlider,
                    _volSlider,
                    _playTime, _maxTime);
            // don't autoplay the video
            _player.setAutoPlay(false);

            _videoViewer.setMediaPlayer(_player);

            _videoPlayer.setUpSliders(_timeSlider, _volSlider, _player);
        }
    }

    private void setVisibilityOfVideo(boolean isVisible) {
        _contentContainer.setVisible(isVisible);
        _promptLabel.setVisible(!isVisible);
        _confirmBtn.setDisable(!isVisible);
        _buttonPane.setVisible(isVisible);

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
        stopCurrentPlayer();
        setVisibilityOfVideo(true);

        if (_includeMastered.isSelected()) {
            // get the number of terms to test on and the files

            File testFolder = new File(Folders.CREATION_TEST_FOLDER.getPath());
            File[] creations = testFolder.listFiles((file) -> {
                if (file.isDirectory() && !file.getName().equals("score")) {
                    return true;
                } else {
                    return false;
                }
            });
            int numberOfTerms = creations.length;
            File randomCreation = getRandomCreationFile(numberOfTerms, creations);
            _player = _videoPlayer.createPlayer(randomCreation, _playPauseBtn, _timeSlider,
                    _volSlider, _playTime, _maxTime);

            // don't autoplay the video
            _player.setAutoPlay(false);

            _videoViewer.setMediaPlayer(_player);


        } else {
            displayNotMasteredCreation();
        }

    }

    private void stopCurrentPlayer() {
        if (_player != null) {
            _player.stop();
        }
    }

    private File getRandomCreationFile(int numberOfTerms, File[] arrayOfFiles) {
        // choose a random term for user
        int randomTermIndex = (int)Math.round (Math.random() * numberOfTerms);
        // prevent array index out of bounds
        if (randomTermIndex == numberOfTerms) {
            randomTermIndex--;
        }
        String randomTerm = arrayOfFiles[randomTermIndex].getName();

        // choose a random creation for the term
        File randomTermFolder =
                new File(Folders.CREATION_TEST_FOLDER.getPath() + File.separator + randomTerm);

        File[] creations = randomTermFolder.listFiles((file) -> {
            if (file.getName().contains(".mp4")) {
                return true;
            } else {
                return false;
            }
        });

        int numberOfCreations = creations.length;
        int randomCreationIndex = (int) (Math.random() * numberOfCreations);
        File randomCreation = (File) Array.get(randomTermFolder.listFiles(),
                randomCreationIndex);

        return randomCreation;
    }

    @FXML
    private void confirm() {

        if (_confirmBtn.getText().equals("CONFIRM")) {
            // get the name of the creation
            File currentFile = new File(_player.getMedia().getSource());
            File termFolder = new File(currentFile.getParent());
            String termName = termFolder.getName();

            String text = _termInput.getText().replaceAll("-", " ");
            // check whether the user got the term correct
            if (text.equalsIgnoreCase(termName)) {
                // correct input
                _correctionLabel.setText("Correct!");
                _correctionLabel.setVisible(true);
                updateScore(termName, true);
            } else {
                // incorrect input
                _correctionLabel.setText("Incorrect. Answer: " + termName);
                _correctionLabel.setVisible(true);
                updateScore(termName, false);
            }

            _confirmBtn.setText("Next");
        } else {
            _confirmBtn.setText("CONFIRM");
            _correctionLabel.setVisible(false);
            if (_includeMastered.isSelected()) {
                includeMastered();
            } else {
                displayNotMasteredCreation();
            }
        }




    }

    private void updateScore(String termName, boolean ifCorrect) {

        // check which folder the creation is in
        boolean isMastered = checkIfMastered(termName);

        if (ifCorrect) {
            if (isMastered) {
                // don't need to do anything
            } else {
                // update score to +1
                File scoreFile =
                        new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath() + File.separator + termName + ".txt");
                try {
                    int score = Integer.valueOf(new StringManipulator().readScoreFromFile(scoreFile));
                    int newScore = score + 1;
                    if (newScore >= 5) {
                        // remove from not mastered folder
                        scoreFile.delete();

                        // add to mastered folder
                        File newFile =
                                new File(Folders.CREATION_SCORE_MASTERED_FOLDER.getPath() + File.separator + termName + ".txt");
                        updateScoreFile(newFile, "5");
                    } else {
                        updateScoreFile(scoreFile, String.valueOf(newScore));
                    }
                } catch (IOException e) {
                    new AlertFactory(Alert.AlertType.ERROR, "Error", "Score file corrupted", "Try" +
                            " resetting your progress.");
                    e.printStackTrace();
                }
            }
        } else {
            if (isMastered) {
                // remove original file from mastered
                File oldScore =
                        new File(Folders.CREATION_SCORE_MASTERED_FOLDER.getPath() + File.separator + termName + ".txt");
                oldScore.delete();

                // add new score file to not mastered, with score 0
                File newScore =
                        new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath() + File.separator + termName + ".txt");
                updateScoreFile(newScore, "0");
            } else {
                // update file to a score of 0
                File score =
                        new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath() + File.separator + termName + ".txt");
                updateScoreFile(score, "0");
            }
        }



    }

    private void updateScoreFile(File newFile, String score) {
        List<String> zero = Arrays.asList(score);
        try {
            // create new file if it does not exist
            newFile.createNewFile();
            Files.write(Paths.get(newFile.getPath()), zero, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfMastered(String termName) {
        File masteredFolder = Folders.CREATION_SCORE_MASTERED_FOLDER.getFile();

        if (masteredFolder.listFiles() != null) {
            for (File notMasteredTerm:masteredFolder.listFiles()) {
                if (notMasteredTerm.getName().replace(".txt","").equals(termName)) {
                    return true;
                }
            }
        }

        return false;
    }

    @FXML
    private void clearProgress() {
        File notMasteredFolder = Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getFile();
        File masteredFolder = Folders.CREATION_SCORE_MASTERED_FOLDER.getFile();

        // change all the scores in the not mastered folder to 0
        for (File notMasteredTerm: notMasteredFolder.listFiles()) {
            updateScoreFile(notMasteredTerm, "0");
        }

        // move all terms from the mastered folder to the not mastered folder and make their
        // score to be 0
        for (File masteredTerm: masteredFolder.listFiles()) {
            updateScore(masteredTerm.getName().replace(".txt", ""), false);
        }

        // start quiz
        displayNotMasteredCreation();
    }

    @FXML
    private void mainMenuPressed() {
        stopCurrentPlayer();
        mainMenu();
    }

    @FXML
    private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            confirm();
        }
    }
}
