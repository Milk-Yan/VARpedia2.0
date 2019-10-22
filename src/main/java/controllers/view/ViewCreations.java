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
                _creationTab.setDisable(true);

//                Text text = new Text("There are currently no creations available.");
//                _creationTab.setContent(text);
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
            String creationFile = Folders.CREATION_PRACTICE_FOLDER.getPath();
            playMedia(_listOfCreations.getSelectionModel().getSelectedItem(), creationFile);
        } else {
            String audioFile = Folders.AUDIO_PRACTICE_FOLDER.getPath();
            playMedia(_listOfAudio.getSelectionModel().getSelectedItem(), audioFile);

        }
    }

    private void playMedia(TreeItem<String> selectedItem, String mediaFolder) {
        if (selectedItem.isLeaf()) {
            String term = selectedItem.getParent().getValue();
            String name = selectedItem.getValue();

            File mediaFile = new File(mediaFolder + File.separator + term + File.separator + name);
            Media media = new Media(mediaFile.toURI().toString());

            if (selectedItem.getParent().getParent().getValue().equals("Audio")) {
                _audioPlayer = new MediaPlayer(media);
                _audioPlayer.play();
            } else {
                _mainApp.playVideo(mediaFile);
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
            String creationFolder = Folders.CREATIONS_FOLDER.getPath();
            deleteMedia(_listOfCreations.getSelectionModel().getSelectedItem(), creationFolder,
                    _listOfCreations);

        } else {
            String audioFolder = Folders.AUDIO_FOLDER.getPath();
            deleteMedia(_listOfAudio.getSelectionModel().getSelectedItem(), audioFolder, _listOfAudio);
        }

    }

    private void deleteMedia(TreeItem<String> selectedItem, String mediaFolder,
                             TreeView<String> treeView) {
        if (selectedItem.isLeaf()) {
            String name = selectedItem.getValue();
            Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Confirmation", "Deleting " +
                    "media", "Do you want to delete " + name + "?").getAlert();
            if (alert.getResult() == ButtonType.OK) {
                String term = selectedItem.getParent().getValue();
                File mediaPracticeFile =
                        new File(mediaFolder + File.separator + "practice" + File.separator + term +
                                File.separator + name);
                File mediaTestFile =
                        new File(mediaFolder + File.separator + "test" + File.separator + term + File.separator + name);
                mediaPracticeFile.delete();
                mediaTestFile.delete();

                // if folder does not contain any creations, delete the folder
                File termPracticeFolder =
                        new File(mediaFolder + File.separator + "practice" + File.separator + term);
                File termTestFolder =
                        new File(mediaFolder + File.separator + "test" + File.separator + term);
                if (termPracticeFolder.exists() && termPracticeFolder.listFiles().length == 0) {
                    termPracticeFolder.delete();
                }
                if (termTestFolder.exists() && termTestFolder.listFiles().length == 0) {
                    termTestFolder.delete();
                }

                for (TreeItem<String> parentTerm: treeView.getRoot().getChildren()) {
                    if (parentTerm.getValue().equals(term)) {
                        parentTerm.getChildren().remove(selectedItem);
                    }
                }


            }
        } else if (selectedItem != null) {
            // selected is a folder
            String term = selectedItem.getValue();
            Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Confirmation", "Deleting " +
                    "media folder", "Do you want to delete all files for " + term + "?").getAlert();
            if (alert.getResult() == ButtonType.OK) {
                File termPracticeFolder =
                        new File(mediaFolder + File.separator + "practice" + File.separator + term);
                File termTestFolder =
                        new File(mediaFolder + File.separator + "test" + File.separator + term);
                for (File media: termPracticeFolder.listFiles()) {
                    media.delete();
                }
                termPracticeFolder.delete();
                for (File media: termTestFolder.listFiles()) {
                    media.delete();
                }
                termTestFolder.delete();

                treeView.getRoot().getChildren().remove(selectedItem);
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
