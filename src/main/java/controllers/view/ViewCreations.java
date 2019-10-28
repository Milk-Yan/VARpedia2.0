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
 * Controller for functionality of ViewCreations.fxml. Allows the view current audio and video
 * creations.
 *
 * @author Milk, OverCry
 */
public class ViewCreations extends Controller {

    private MediaPlayer _audioPlayer;
    private TreeItem<String> _creationsTreeRoot;
    private TreeItem<String> _audioTreeRoot;

    @FXML private TabPane _tabPane;
    @FXML private Tab _creationTab;
    @FXML private Tab _audioTab;
    @FXML private TreeView<String> _listOfCreations;
    @FXML private TreeView<String> _listOfAudio;
    @FXML private Button _playBtn;
    @FXML private Button _deleteBtn;

    /**
     * This method is to be called before the scene is showed to users. It creates a ListView of
     * current creations. The search for current creations is implemented on a different thread
     * to not freeze up the GUI thread if there is a large amount of creations.
     */
    public void setUp() {

        ViewCreationsTask viewCreationsTask = new ViewCreationsTask();
        ViewAudioTask viewAudioTask = new ViewAudioTask();

        new Thread(viewCreationsTask).start();
        new Thread(viewAudioTask).start();

        try {
            _creationsTreeRoot = viewCreationsTask.get();
            _audioTreeRoot = viewAudioTask.get();

            disableUnnecessaryButtons();
            _playBtn.setDisable(true);

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
     * Disable specific buttons depending on the current number of creations.
     */
    private void disableUnnecessaryButtons() {
        // if there are no creations, disable play and delete buttons
        if (_creationsTreeRoot.getChildren().isEmpty() && _audioTreeRoot.getChildren().isEmpty()) {
            _playBtn.setVisible(false);
            _deleteBtn.setVisible(false);
        }

        // if there are no creations, tell the user.
        if (_creationsTreeRoot.getChildren().isEmpty()) {
            _listOfCreations.setVisible(false);

            Text text = new Text("There are currently no creations available.");
            _creationTab.setContent(text);

        } else {
            _listOfCreations.setRoot(_creationsTreeRoot);
        }

        // if there are no audio creations, tell the user.
        if (_audioTreeRoot.getChildren().isEmpty()) {
            // show that there are no audio available
            _listOfAudio.setVisible(false);

            Text text = new Text("There are currently no audio available.");
            _audioTab.setContent(text);

        } else {
            _listOfAudio.setRoot(_audioTreeRoot);

        }


    }

    /**
     * Functionality of the play button. Plays a selected creation.
     */
    @FXML private void play() {

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

    /**
     * Plays the selected media.
     * @param selectedItem The selected media.
     * @param mediaFolder The folder the item is in.
     */
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
     * Functionality of the delete button. Deletes a selected creation. Can be an audio, video and
     * the entire folder.
     */
    @FXML private void delete() {
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

        disableUnnecessaryButtons();

    }

    /**
     * Deletes the selected item from the tree and the file itself.
     * @param selectedItem The selected item.
     * @param mediaFolder The folder containing the selected item.
     * @param treeView The TreeView that displays the selected item.
     */
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
                if (termPracticeFolder.exists() && Objects.requireNonNull(
                        termPracticeFolder.listFiles()).length == 0) {
                    termPracticeFolder.delete();
                }
                if (termTestFolder.exists() && Objects.requireNonNull(termTestFolder.listFiles()).length == 0) {
                    termTestFolder.delete();
                }

                for (TreeItem<String> parentTerm: treeView.getRoot().getChildren()) {
                    if (parentTerm.getValue().equals(term)) {
                        parentTerm.getChildren().remove(selectedItem);
                        removeIfEmpty(termPracticeFolder, termTestFolder, parentTerm);
                        break;
                    }
                }
            }
        } else {
            // selected is a folder
            String term = selectedItem.getValue();
            Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Confirmation", "Deleting " +
                    "media folder", "Do you want to delete all files for " + term + "?").getAlert();
            if (alert.getResult() == ButtonType.OK) {
                File termPracticeFolder =
                        new File(mediaFolder + File.separator + "practice" + File.separator + term);
                File termTestFolder =
                        new File(mediaFolder + File.separator + "test" + File.separator + term);
                for (File media: Objects.requireNonNull(termPracticeFolder.listFiles())) {
                    media.delete();
                }
                termPracticeFolder.delete();
                for (File media: Objects.requireNonNull(termTestFolder.listFiles())) {
                    media.delete();
                }
                termTestFolder.delete();

                treeView.getRoot().getChildren().remove(selectedItem);
            }
        }
    }


    /**
     * Deletes the practice, test and tree parents if all children are gone.
     * @param practiceFolder The practice folder to delete.
     * @param testFolder The test folder to delete.
     * @param parent The node which is the parent of the deleted node.
     */
    private void removeIfEmpty(File practiceFolder, File testFolder, TreeItem<String> parent) {
        if (practiceFolder.exists() && practiceFolder.listFiles().length == 0) {
            practiceFolder.delete();
        }

        if (testFolder.exists() && testFolder.listFiles().length == 0) {
            testFolder.delete();
        }

        if (parent.getChildren().isEmpty()) {
            TreeItem<String> root = parent.getParent();
            root.getChildren().remove(parent);
        }
    }

    /**
     * Functionality of the main menu button. Stops the audio player before returning to the main
     * menu.
     */
    @FXML private void mainMenuPress() {

        stopAudioPlayer();
        mainMenu();

    }

    /**
     * Stops the current audio playback
     */
    private void stopAudioPlayer() {
        // stop current player if it is playing
        if (_audioPlayer != null) {
            _audioPlayer.stop();
        }
    }

    /**
    * check if selected can be played, otherwise disable the button
    */
    @FXML
    private void checkPlayable(){
        if (_creationTab.isSelected()){
            if (!(_listOfCreations.getSelectionModel().getSelectedItem()==null)&&_listOfCreations.getSelectionModel().getSelectedItem().isLeaf()){
                _playBtn.setDisable(false);
            } else {
                _playBtn.setDisable(true);
            }
        } else if (_audioTab.isSelected()){
            if (!(_listOfAudio.getSelectionModel().getSelectedItem()==null)&&_listOfAudio.getSelectionModel().getSelectedItem().isLeaf()){
                _playBtn.setDisable(false);
            } else {
                _playBtn.setDisable(true);
            }
        }
    }
}
