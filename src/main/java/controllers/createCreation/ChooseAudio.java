package main.java.controllers.createCreation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.application.StringManipulator;
import main.java.controllers.Controller;
import main.java.tasks.GetImagesTask;

import java.io.File;
import java.util.*;

/**
 * Controller for ChooseAudio.fxml
 * For displaying pre-existing creations
 *
 * @author wcho400
 */
public class ChooseAudio extends Controller {

    private String _term;
    private ArrayList<MediaPlayer> _listOfMediaPlayers = new ArrayList<>();
    private GetImagesTask _task;

    @FXML private ListView<String> _audioCandidates;
    @FXML private ListView<String> _audioChosen;
    @FXML private Button _mainMenuBtn;
    @FXML private ProgressIndicator _indicator;
    @FXML private ChoiceBox<String> _musicChoice;

    /**
     * Lists audio files of the Wikipedia search term
     *
     * @param term The Wikipedia search term of the audio.
     */
    public void setUp(String term) {
        _term = term;

        // make the ListView single-selection
        _audioCandidates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        File folder =
                new File(Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term);

        File[] arrayOfAudioFiles = folder.listFiles((file) -> file.getName().contains(".wav"));

        // sorts the list of creations candidates in alphabetical order.
        assert arrayOfAudioFiles != null;
        List<File> listOfAudioFiles = Arrays.asList(arrayOfAudioFiles);
        Collections.sort(listOfAudioFiles);

        // only gives the name of the file, and gives a number as
        // indication of the number of current creations.
        ObservableList<String> audioList = FXCollections.observableArrayList();
        int lineNumber = 1;

        for (File file : listOfAudioFiles) {
            String name = file.getName().replace(".wav", "");
            audioList.add(lineNumber + ". " + name + "\n");
            lineNumber++;
        }

        _audioCandidates.setItems(audioList);

        // only allow re-ordering one at a time
        _audioCandidates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _audioChosen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        setUpMusicChoice();
    }

    private void setUpMusicChoice() {

        // add a choice for no music
        _musicChoice.getItems().add("None");

        File musicFolder = Folders.MUSIC_FOLDER.getFile();

        for (File music: Objects.requireNonNull(musicFolder.listFiles())) {
            // remove the extension name
            String musicName = music.getName().replace(".wav", "");
            _musicChoice.getItems().add(musicName);
        }
    }

    /**
     * button to confirm audio order for creation
     * checks if an accepted number of audio files are selected
     */
    @FXML private void confirm() {

        stopAudioPlayer();

        ObservableList<String> selectedList = _audioChosen.getItems();

        if (selectedList.isEmpty()) {

            new AlertFactory(AlertType.ERROR, "Error", "No items selected", "Please select at " +
                    "least one audio file.");

        } else if (selectedList.size() > 10) {

            new AlertFactory(AlertType.ERROR, "Error", "Too many items selected", "Please " +
                    "selected less than 10 audio files");

        } else {

            // clear numbering
            ArrayList<String> selectedListClean = new ArrayList<>();
            for (String audio : selectedList) {
                selectedListClean.add(audio.replaceFirst("\\d+\\. ", "").trim());
            }

            if (_musicChoice.getSelectionModel().getSelectedIndex() != 0) {
                String musicSelection = _musicChoice.getSelectionModel().getSelectedItem() + ".wav";
                _task = new GetImagesTask(_term, _mainApp, selectedListClean, musicSelection);
            } else {
                _task = new GetImagesTask(_term, _mainApp, selectedListClean, null);
            }
            new Thread(_task).start();

            // show indicator of loading
            _indicator.setVisible(true);
        }


    }

    /**
     * method to arrange audio ordering
     * moves audio up the list if it is not at the top
     */
    @FXML private void moveChosenUp() {

        if (_audioChosen.getSelectionModel().getSelectedItems().size() == 1) {
            int currentIndex = _audioChosen.getSelectionModel().getSelectedIndex();

            if (currentIndex > 0) {
                int newIndex = currentIndex - 1;

                reorderList(currentIndex, newIndex, _audioChosen);
            }
        }
    }

    /**
     * method to arrange audio ordering
     * moves audio down the list if it is not at the bottom
     */
    @FXML private void moveChosenDown() {

        if (_audioChosen.getSelectionModel().getSelectedItems().size() == 1) {

            int currentIndex = _audioChosen.getSelectionModel().getSelectedIndex();

            if (currentIndex != -1 && currentIndex < _audioChosen.getItems().size() - 1) {
                int newIndex = currentIndex + 1;
                reorderList(currentIndex, newIndex, _audioChosen);
            }

        }
    }

    /**
     * Reorders the item (String) in the list according to the supplied indexes.
     * @param currentIndex The current index of the item.
     * @param newIndex The index the item will go to.
     * @param listView The list to reorder.
     */
    private void reorderList(int currentIndex, int newIndex, ListView<String> listView) {
        String item = listView.getSelectionModel().getSelectedItem();

        // remove at current position
        listView.getItems().remove(currentIndex);

        // insert at new position
        listView.getItems().add(newIndex, item);

        sortLists();
    }

    /**
     * Passes the selected audio file of yet-to-be chosen audio files to chosen audio files
     */
    @FXML private void candidateToChosen() {

        String candidate = _audioCandidates.getSelectionModel().getSelectedItem();
        int candidateIndex = _audioCandidates.getSelectionModel().getSelectedIndex();

        if (candidateIndex != -1) {
            // add to chosen list
            addToEndOfList(candidate, _audioChosen);
            //remove from candidate list
            _audioCandidates.getItems().remove(candidate);

            sortLists();
        }

    }

    /**
     * Passes the selected audio file of chosen audio files to yet-to-be chosen audio files
     */
    @FXML private void chosenToCandidate() {
        String chosen = _audioChosen.getSelectionModel().getSelectedItem();
        int chosenIndex = _audioChosen.getSelectionModel().getSelectedIndex();

        if (chosenIndex != -1) {
            // add to candidate list
            addToEndOfList(chosen, _audioCandidates);
            // remove from chosen list
            _audioChosen.getItems().remove(chosen);

            sortLists();
        }
    }

    /**
     * Update to display of audio in both ListView's
     */
    private void sortLists() {
        int indexCandidates = 1;
        int indexChosen = 1;
        ArrayList<String> currentItemsCandidates =
                new ArrayList<>(_audioCandidates.getItems());
        ArrayList<String> currentItemsChosen = new ArrayList<>(_audioChosen.getItems());

        // remove all items
        _audioCandidates.getItems().removeAll(currentItemsCandidates);
        _audioChosen.getItems().removeAll(currentItemsChosen);

        for (String audio : currentItemsCandidates) {
            // insert new items
            _audioCandidates.getItems().add(audio.replaceFirst("\\d+\\. ", indexCandidates + ". "));
            indexCandidates++;
        }

        for (String audio : currentItemsChosen) {
            // insert new items
            _audioChosen.getItems().add(audio.replaceFirst("\\d+\\. ", indexChosen + ". "));
            indexChosen++;
        }


    }

    /**
     * Bring a selected audio to the end of the other ListView
     *
     * @param candidate The item to move
     * @param audioList The audioList to move it to
     */
    private void addToEndOfList(String candidate, ListView<String> audioList) {
        // remove numbering
        candidate = candidate.replaceFirst("\\d+\\. ", "");

        // add to end of list
        int index = audioList.getItems().size() + 1;
        candidate = index + ". " + candidate;
        audioList.getItems().add(candidate);
    }


    /**
     * Plays the selected audio.
     */
    @FXML private void listen() {

        // ensures only one audio is played at a time by stopping any old audios.
        stopAudioPlayer();

        ObservableList<String> audioCandidate =
                _audioCandidates.getSelectionModel().getSelectedItems();
        ObservableList<String> audioChosen = _audioChosen.getSelectionModel().getSelectedItems();

        if (audioCandidate.size() + audioChosen.size() == 1) {
            String audioName;
            if (audioCandidate.size() == 1) {
                // play audio candidate
                audioName = _audioCandidates.getSelectionModel().getSelectedItem();

            } else {
                // play audio chosen
                audioName = _audioChosen.getSelectionModel().getSelectedItem();
            }

            // remove numbering and \n at end
            audioName = new StringManipulator().removeNumberedLines(audioName).replaceAll("\n", "");

            File audioFile = new File(
                    Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term + File.separator + audioName +
                            ".wav");
            File musicFile =
                    new File(Folders.MUSIC_FOLDER.getPath() + File.separator + _musicChoice.getSelectionModel().getSelectedItem() + ".wav");

            playFile(audioFile);
            playFile(musicFile);

        } else {
            new AlertFactory(AlertType.ERROR, "Error", "Invalid selection",
                    "You can only listen to one audio at a time");
        }

    }

    /**
     * Puts the file in a JavaFX MediaPlayer and plays it.
     * @param file The file to play.
     */
    private void playFile(File file) {
        Media media = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        _listOfMediaPlayers.add(player);

        player.play();
    }

    /**
     * Stops the audio player before returning to the main menu.
     * Asks for confirmation with an alert
     */
    @FXML private void mainMenuPress() {

        stopAudioPlayer();

        if (_mainMenuBtn.getText().equals("Confirm?")) {

            // cancel current task before going back to main menu
            if (_task != null) {
                _task.cancel();
            }
            _mainApp.displayMainMenuScene();

        } else {
            _mainMenuBtn.setText("Confirm?");
        }
    }

    /**
     * Stop audio playback.
     */
    private void stopAudioPlayer() {
        for (MediaPlayer player:_listOfMediaPlayers) {
            if (player != null) {
                player.stop();
            }
            _listOfMediaPlayers.remove(player);
        }
    }
}
