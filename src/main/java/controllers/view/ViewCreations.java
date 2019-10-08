package main.java.controllers.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
    private ListView<String> _listOfCreations;

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

        ViewCreationsTask viewCreationTask = new ViewCreationsTask();
        ViewAudioTask viewAudioTask = new ViewAudioTask();

        new Thread(viewCreationTask).start();
        new Thread(viewAudioTask).start();

        try {
            ObservableList<String> creationsList = viewCreationTask.get();
            TreeItem<String> audioTreeRoot = viewAudioTask.get();

            if (creationsList.isEmpty() && audioTreeRoot.getChildren().isEmpty()) {
                _playBtn.setVisible(false);
                _deleteBtn.setVisible(false);
            }

            if (creationsList.isEmpty()) {
                // show that there are no creations available
                _listOfCreations.setVisible(false);

                Text text = new Text("There are currently no creations available.");
                _creationTab.setContent(text);
                //_container.getChildren().add(1, text);

            } else {
                _listOfCreations.setItems(creationsList);
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
            String selectionName = _listOfCreations.getSelectionModel().getSelectedItem();
            String videoName = selectionName.replaceFirst("\\d+\\. ", "").replace("\n", "");

            if (videoName.isEmpty()) {
                new AlertFactory(AlertType.ERROR, "Error", "Wrong selection", "Selection cannot " +
                        "be null");
            } else {

                if (!(selectionName.isEmpty())) {
                    _mainApp.playVideo(videoName);
                }

            }
        } else {

            // all audio files are leaves
            if (_listOfAudio.getSelectionModel().getSelectedItem().isLeaf()) {
                String term =
                        _listOfAudio.getSelectionModel().getSelectedItem().getParent().getValue();
                String audioName = _listOfAudio.getSelectionModel().getSelectedItem().getValue();
                File audioFile =
                        new File(Folders.AudioFolder.getPath() + File.separator + term + File.separator + audioName);

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
            String creationName = _listOfCreations.getSelectionModel().getSelectedItem();
            if (creationName != null) {
                String videoName = creationName.replaceFirst("\\d+\\. ", "").replace("\n", "");
                Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Warning", "Confirmation",
                        "Would you like to delete " + videoName + "?").getAlert();
                if (alert.getResult() == ButtonType.OK) {
                    String s = File.separator;
                    File fileCreation =
                            new File(Folders.CreationsFolder.getPath() +
                                    s + videoName + ".mp4");

                    fileCreation.delete();

                    _mainApp.displayViewCreationsScene();
                }
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
