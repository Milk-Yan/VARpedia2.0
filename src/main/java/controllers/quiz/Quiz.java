package main.java.controllers.quiz;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Controller for Quiz.fxml. Allows the user to play a quiz to test their knowledge.
 *
 * @author Milk, OverCry
 */
public class Quiz extends Controller {

    // delegate for delegation pattern. Takes care of most of the video-playing functionality.
    private VideoPlayer _videoPlayer;
    private MediaPlayer _player;
    private final File _happyFaceFile = new File(System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "main" + File.separator + "resources" +
            File.separator + "images" + File.separator + "Happy.png");
    private final File _sadFaceFile = new File(System.getProperty("user.dir") +
            File.separator + "src" + File.separator + "main" + File.separator + "resources" +
            File.separator + "images" + File.separator + "Sad.png");

    @FXML private Slider _volSlider;
    @FXML private Slider _timeSlider;
    @FXML private Label _playTime;
    @FXML private Label _maxTime;
    @FXML private MediaView _videoViewer;
    @FXML private CheckBox _includeMastered;
    @FXML private HBox _contentContainer;
    @FXML private Button _playPauseBtn;
    @FXML private Button _confirmBtn;
    @FXML private TextField _termInput;
    @FXML private Label _correctionLabel;
    @FXML private Label _promptLabel;
    @FXML private Pane _buttonPane;
    @FXML private ImageView _emotionViewer;

    /**
     * Called immediately after the root element of the scene is processed by the FXMLLoader.
     * Note that FXML elements may have not been initialized yet and therefore should not be
     * called from this method.
     */
    @FXML private void initialize() {
        // create the score folder
        new File(Folders.CREATION_SCORE_FOLDER.getPath()).mkdirs();

        _videoPlayer = new VideoPlayer();

        displayNotMasteredCreation();

    }

    /**
     * Sets the quiz to display only not mastered creations.
     */
    private void displayNotMasteredCreation() {

        stopCurrentPlayer();
        setVisibilityOfVideo(true);

        File notMasteredFolder = new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath());

        int numberOfTerms;
        numberOfTerms = Objects.requireNonNull(notMasteredFolder.listFiles()).length;

        // if there are no terms available, hide everything related to the video.
        if (numberOfTerms == 0) {
            setVisibilityOfVideo(false);
        } else {

            ArrayList<File> listOfFiles = new ArrayList<>();

            // create a list of the path to all not mastered creations
            for (File notMasteredScoreFile: Objects.requireNonNull(notMasteredFolder.listFiles())) {
                listOfFiles.add(new File(Folders.CREATION_TEST_FOLDER.getPath() + File.separator + notMasteredScoreFile.getName().replace(".txt", "")));
            }

            File randomCreation = getRandomCreationFile(numberOfTerms,
                    listOfFiles.toArray(new File[0]));

            _player = _videoPlayer.createPlayer(randomCreation, _playPauseBtn, _timeSlider,
                    _volSlider,
                    _playTime, _maxTime);

            _videoViewer.setMediaPlayer(_player);
            _videoPlayer.setUpSliders(_timeSlider, _volSlider, _player);
        }
    }

    /**
     * Sets the visibility of video content.
     * @param isVisible Whether the video content should be visible.
     */
    private void setVisibilityOfVideo(boolean isVisible) {
        _contentContainer.setVisible(isVisible);
        _promptLabel.setVisible(!isVisible);
        _confirmBtn.setDisable(!isVisible);
        _buttonPane.setVisible(isVisible);

    }

    /**
     * Sets the quiz to display all creations, including mastered creations.
     */
    @FXML private void displayIncludeMastered() {
        stopCurrentPlayer();
        setVisibilityOfVideo(true);

        if (_includeMastered.isSelected()) {
            // get the number of terms to test on and the files
            File testFolder = new File(Folders.CREATION_TEST_FOLDER.getPath());

            // exclude the score folder from the list of creations
            File[] creations = testFolder.listFiles((file) ->
                    file.isDirectory() && !file.getName().equals("score"));

            int numberOfTerms = creations.length;
            File randomCreation = getRandomCreationFile(numberOfTerms, creations);
            _player = _videoPlayer.createPlayer(randomCreation, _playPauseBtn, _timeSlider,
                    _volSlider, _playTime, _maxTime);

            _videoViewer.setMediaPlayer(_player);

        } else {
            displayNotMasteredCreation();
        }

    }

    /**
     * Play button functionality. For more details, see
     * {@link main.java.controllers.view.VideoPlayer#playPauseFunctionality(MediaPlayer, Button)} ()}
     */
    @FXML private void playPause() {
        _videoPlayer.playPauseFunctionality(_player, _playPauseBtn);
    }

    /**
     * Fast forward button functionality. For more details, see
     * {@link main.java.controllers.view.VideoPlayer#forwardFunctionality(MediaPlayer)}
     */
    @FXML private void fastForward() {
        _videoPlayer.fastForwardFunctionality(_player);
    }

    /**
     * Slow down button functionality. For more details, see
     * {@link main.java.controllers.view.VideoPlayer#slowDownFunctionality(MediaPlayer)}
     */
    @FXML private void slowDown() {
        _videoPlayer.slowDownFunctionality(_player);
    }

    /**
     * Forward button functionality. For more details, see
     * {@link main.java.controllers.view.VideoPlayer#forwardFunctionality(MediaPlayer)}
     */
    @FXML private void forward() {
        _videoPlayer.forwardFunctionality(_player);
    }

    /**
     * Backward button functionality. For more details, see
     * {@link main.java.controllers.view.VideoPlayer#backwardFunctionality(MediaPlayer)}
     */
    @FXML private void backward() {
        _videoPlayer.backwardFunctionality(_player);
    }

    /**
     * Stops the current player.
     */
    private void stopCurrentPlayer() {
        if (_player != null) {
            _player.stop();
        }
    }

    /**
     * Gets a random creation file from the list of files.
     * @param numberOfTerms Number of current terms available.
     * @param arrayOfFiles The array of files of the term.
     * @return The random creation file chosen.
     */
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

        File[] creations = randomTermFolder.listFiles((file) -> file.getName().contains(".mp4"));

        int numberOfCreations = creations.length;
        int randomCreationIndex = (int) (Math.random() * numberOfCreations);
        File randomCreation = (File) Array.get(randomTermFolder.listFiles(),
                randomCreationIndex);

        return randomCreation;
    }

    /**
     * Functionality of the confirm button. Checks if the user input is correct, and updates
     * their score accordingly.
     */
    @FXML private void confirm() {

        if (_confirmBtn.getText().equals("Confirm")) {
            // get the name of the creation
            File currentFile = new File(_player.getMedia().getSource());
            File termFolder = new File(currentFile.getParent());
            String termName = termFolder.getName();

            String text = _termInput.getText().replaceAll("-", " ");
            // check whether the user got the term correct
            if (text.equalsIgnoreCase(termName)) {
                // correct input
                showCorrect();
                updateScore(termName, true);
            } else {
                // incorrect input
                showWrong(termName);
                updateScore(termName, false);
            }
            _confirmBtn.setText("Next");
        } else {
            clearCorrection();
            _confirmBtn.setText("Confirm");

            if (_includeMastered.isSelected()) {
                displayIncludeMastered();
            } else {
                displayNotMasteredCreation();
            }
        }
    }

    private void showCorrect() {
        _correctionLabel.setText("Correct!");
        _correctionLabel.setVisible(true);
        _videoViewer.setVisible(false);

        // show the user a happy face
        Image image = new Image(_happyFaceFile.toURI().toString());
        _emotionViewer.setImage(image);
        _emotionViewer.setVisible(true);
    }

    private void showWrong(String termName) {
        _correctionLabel.setText("Incorrect. Answer: " + termName);
        _correctionLabel.setVisible(true);
        _videoViewer.setVisible(false);

        // show the user a happy face
        Image image = new Image(_sadFaceFile.toURI().toString());
        _emotionViewer.setImage(image);
        _emotionViewer.setVisible(true);
    }

    private void clearCorrection() {
        _correctionLabel.setVisible(false);
        _termInput.clear();
        _emotionViewer.setVisible(false);
        _videoViewer.setVisible(true);
    }

    /**
     * Updates the score for the term.
     * @param termName The term whose score is to be updated.
     * @param ifCorrect If the user input was correct.
     */
    private void updateScore(String termName, boolean ifCorrect) {

        // check which folder the creation is in
        boolean isMastered = checkIfMastered(termName);

        if (ifCorrect) {
            if (isMastered) {
                // don't need to do anything, it is already mastered
            } else {
                // update score to +1
                File scoreFile =
                        new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath() + File.separator + termName + ".txt");
                try {
                    int score = Integer.parseInt(new StringManipulator().readScoreFromFile(scoreFile));
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

    /**
     * Updates the score in a file depending on the score given.
     * @param newFile The file to put the score in.
     * @param score The score to put in the file.
     */
    private void updateScoreFile(File newFile, String score) {
        List<String> zero = Collections.singletonList(score);
        try {
            // create new file if it does not exist
            newFile.createNewFile();
            Files.write(Paths.get(newFile.getPath()), zero, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the term is mastered by checking if it is in the mastered folder.
     * @param termName The Wikipedia term.
     * @return If the term is mastered.
     */
    private boolean checkIfMastered(String termName) {
        File masteredFolder = Folders.CREATION_SCORE_MASTERED_FOLDER.getFile();

        if (masteredFolder.listFiles() != null) {
            for (File notMasteredTerm: Objects.requireNonNull(masteredFolder.listFiles())) {
                if (notMasteredTerm.getName().replace(".txt","").equals(termName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Functionality of the clear progress button. Makes the scores of all creations 0 and puts
     * them all back to the not mastered folder.
     */
    @FXML private void clearProgress() {
        File notMasteredFolder = Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getFile();
        File masteredFolder = Folders.CREATION_SCORE_MASTERED_FOLDER.getFile();

        // change all the scores in the not mastered folder to 0
        for (File notMasteredTerm: Objects.requireNonNull(notMasteredFolder.listFiles())) {
            updateScoreFile(notMasteredTerm, "0");
        }

        // move all terms from the mastered folder to the not mastered folder and make their
        // score to be 0
        for (File masteredTerm: Objects.requireNonNull(masteredFolder.listFiles())) {
            updateScore(masteredTerm.getName().replace(".txt", ""), false);
        }

        // start quiz
        displayNotMasteredCreation();
    }

    /**
     * Functionality of the main menu button. Stops the current player before returning to the
     * main screen.
     */
    @FXML private void mainMenuPressed() {
        stopCurrentPlayer();
        mainMenu();
    }

    /**
     * When the user presses the enter key, it will be the same as clicking the confirm button.
     * @param keyEvent The event triggered when a key is pressed.
     */
    @FXML private void onEnter(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            confirm();
        }
    }

    /**
     * Used to define if to include the mastered creations from outside this class.
     * @param includeMastered If the mastered creations should be included.
     */
    public void setIncludeMastered(boolean includeMastered) {
        if (includeMastered) {
            displayIncludeMastered();
        } else {
            displayNotMasteredCreation();
        }
    }
}
