package main.java.controllers.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.controllers.Controller;
import main.java.tasks.ViewAudioTask;
import main.java.tasks.ViewCreationsTask;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Controller for functionality of View.fxml
 *
 * @author Milk
 */
public class ViewCreations extends Controller {

    private MediaPlayer _audioPlayer;

    @FXML
    private TabPane _tabPane;

    @FXML
    private Tab _creationTab;

    @FXML
    private Tab _audioTab;

    @FXML
    private TreeView<String> _listOfCreations;

    @FXML
    private TreeView<String> _listOfAudio;

    @FXML
    private Button _playBtn;

    @FXML
    private Button _deleteBtn;

    /**
     * Creates a ListView of current creations. The search for
     * current creations is implemented on a different thread to
     * allow concurrency.
     */
    public void setUp() {

        ViewCreationsTask viewCreationsTask = new ViewCreationsTask();
        ViewAudioTask viewAudioTask = new ViewAudioTask();

        new Thread(viewCreationsTask).start();
        new Thread(viewAudioTask).start();

        try {
            TreeItem<String> creationsTreeRoot = viewCreationsTask.get();
            TreeItem<String> audioTreeRoot = viewAudioTask.get();

            if (creationsTreeRoot.getChildren().isEmpty() && audioTreeRoot.getChildren().isEmpty()) {
                _playBtn.setVisible(false);
                _deleteBtn.setVisible(false);
            }

            if (creationsTreeRoot.getChildren().isEmpty()) {
                // show that there are no creations available
                _listOfCreations.setVisible(false);

                Text text = new Text("There are currently no creations available.");
                _creationTab.setContent(text);
                //_container.getChildren().add(1, text);

            } else {
                _listOfCreations.setRoot(creationsTreeRoot);
            }

            if (audioTreeRoot.getChildren().isEmpty()) {
                // show that there are no audio available
                _listOfAudio.setVisible(false);

                Text text = new Text("There are currently no audio available.");
                _audioTab.setContent(text);

            } else {
                _listOfAudio.setRoot(audioTreeRoot);

            }


        } catch (InterruptedException e) {
            // probably intended, don't do anything
        } catch (ExecutionException e) {
            Platform.runLater(() -> {
                e.printStackTrace();
                new AlertFactory(AlertType.ERROR, "Error", "Execution Exception", "Could not " +
                        "execute the view function");
            });
        }


    }

    /**
     * method to play a selected creation
     * Allowed both audio and video to be play back
     */
    @FXML
    private void play() {

        stopAudioPlayer();

        Tab tab = _tabPane.getSelectionModel().getSelectedItem();

        //checks where the selected creation is listed as
        if (tab == _creationTab) {
            if (_listOfCreations.getSelectionModel().getSelectedItem().isLeaf()) {
                String term =
                        _listOfCreations.getSelectionModel().getSelectedItem().getParent().getValue();
                String creationName =
                        _listOfCreations.getSelectionModel().getSelectedItem().getValue();
                File creationFile =
                        new File(Folders.CreationPracticeFolder.getPath() + File.separator + term + File.separator + creationName);

                _mainApp.playVideo(creationFile);
            }
        } else {

            // all audio files are leaves
            if (_listOfAudio.getSelectionModel().getSelectedItem().isLeaf()) {
                String term =
                        _listOfAudio.getSelectionModel().getSelectedItem().getParent().getValue();
                String audioName = _listOfAudio.getSelectionModel().getSelectedItem().getValue();
                File audioFile =
                        new File(Folders.AudioPracticeFolder.getPath() + File.separator + term + File.separator + audioName);

                Media audio = new Media(audioFile.toURI().toString());
                _audioPlayer = new MediaPlayer(audio);
                _audioPlayer.play();
            }

        }


    }

    /**
     * deletes a selected creation. Can be an audio, video and the entire folder
     */
    @FXML
    private void delete() {
        stopAudioPlayer();

        Tab tab = _tabPane.getSelectionModel().getSelectedItem();

        //checks if the selected item is an audio or a video
        if (tab == _creationTab) {
            if (_listOfCreations.getSelectionModel().getSelectedItem().isLeaf()) {
                String creationName =
                        _listOfCreations.getSelectionModel().getSelectedItem().getValue();
                Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Confirmation", "Deleting " +
                        "creation", "Do you want to delete " + creationName + "?").getAlert();
                if (alert.getResult() == ButtonType.OK) {
                    String term =
                            _listOfCreations.getSelectionModel().getSelectedItem().getParent().getValue();
                    File creationFile =
                            new File(Folders.CreationPracticeFolder.getPath() + File.separator + term + File.separator + creationName);
                    creationFile.delete();

                    File creationFolder =
                            new File(Folders.CreationPracticeFolder.getPath() + File.separator + term);
                    if (creationFolder.exists() && creationFolder.listFiles().length == 0) {
                        creationFolder.delete();
                    }

                    _mainApp.displayViewCreationsScene();
                }
            } else if (_listOfCreations.getSelectionModel().getSelectedItem() != null) {
                // selected is a folder
                String term = _listOfCreations.getSelectionModel().getSelectedItem().getValue();
                Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Confirmation", "Deleting " +
                        "creation folder", "Do you want to delete all creation(s) for " + term +
                        "?").getAlert();
                if (alert.getResult() == ButtonType.OK) {
                    File audioFolder =
                            new File(Folders.CreationPracticeFolder.getPath() + File.separator + term);

                    for (File creation:audioFolder.listFiles()) {
                        creation.delete();
                    }
                    audioFolder.delete();
                }
                _mainApp.displayViewCreationsScene();
            }
        } else {
            if (_listOfAudio.getSelectionModel().getSelectedItem().isLeaf()) {
                String audioName = _listOfAudio.getSelectionModel().getSelectedItem().getValue();
                Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Confirmation", "Deleting " +
                        "audio", "Do you want to delete " + audioName + "?").getAlert();
                if (alert.getResult() == ButtonType.OK) {
                    String term =
                            _listOfAudio.getSelectionModel().getSelectedItem().getParent().getValue();
                    File audioFile =
                            new File(Folders.AudioFolder.getPath() + File.separator + term + File.separator + audioName);
                    audioFile.delete();

                    File audioFolder =
                            new File(Folders.AudioFolder.getPath() + File.separator + term);
                    if (audioFolder.exists() && Objects.requireNonNull(audioFolder.listFiles().length == 0)){
                        audioFolder.delete();
                    }
                    _mainApp.displayViewCreationsScene();
                }
            } else if (_listOfAudio.getSelectionModel().getSelectedItem() != null) {
                // selected is a folder
                String term = _listOfAudio.getSelectionModel().getSelectedItem().getValue();
                Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Confirmation",
                        "Deleting audio folder",
                        "Do you want to delete all audio for " + term + "?").getAlert();
                if (alert.getResult() == ButtonType.OK) {
                    File audioFolder = new File(
                            Folders.AudioFolder.getPath() + File.separator + term);

                    for (File audio : Objects.requireNonNull(audioFolder.listFiles())) {
                        audio.delete();
                    }
                    audioFolder.delete();

                    _mainApp.displayViewCreationsScene();

                }
            }
        }

    }

    /**
     * Returns to the main menu
     */
    @FXML
    private void mainMenuPress() {

        stopAudioPlayer();

        mainMenu();

    }

    /**
     * stops the current audio playback
     */
    private void stopAudioPlayer() {
        // stop current player if it is playing
        if (_audioPlayer != null) {
            _audioPlayer.stop();
            _audioPlayer = null;
        }
    }


}
