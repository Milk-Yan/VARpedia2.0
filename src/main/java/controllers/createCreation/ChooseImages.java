package main.java.controllers.createCreation;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.controllers.Controller;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for ChooseImages.fxml.
 * Allows user to select images to be used.
 *
 * @author Milk, OverCry
 */
public class ChooseImages extends Controller {

    private String _term;
    private String _musicSelection;
    private ArrayList<String> _audioList;

    private ArrayList<String> _imageCandidatesList = new ArrayList<>();
    private ArrayList<String> _imageChosenList = new ArrayList<>();

    @FXML private Text _message;
    @FXML private ListView<HBox> _imageCandidates;
    @FXML private ListView<HBox> _imageChosen;

    /**
     * Passes through list of selected audio, as well as the search term
     *
     * @param term The Wikipedia search term.
     * @param audioList The list of selected audio.
     */
    public void setUp(String term, ArrayList<String> audioList, String musicSelection) {
        _term = term;
        _audioList = audioList;
        _musicSelection = musicSelection;
        _message.setText("Images files for " + term + ": ");

        File imageFolder =
                new File(Folders.TEMP_IMAGES_FOLDER.getPath() + File.separator + _term);

        int index = 1;

        for (File imageFile : Objects.requireNonNull(imageFolder.listFiles())) {
            _imageCandidatesList.add(imageFile.getName());

            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = setUpImageView(image);

            Label indexLabel = setUpIndexLabel(index);
            setUpHBox(indexLabel, imageView);

            index++;
        }

        _imageChosen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _imageCandidates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    /**
     * Sets up the image view for images.
     * @param image Image to put in ImageView
     * @return ImageView with image put in and some other customisable characteristics.
     */
    private ImageView setUpImageView(Image image) {
        ImageView imageView = new ImageView(image);

        imageView.setFitHeight(200);
        imageView.setFitWidth(180);
        imageView.setPreserveRatio(true);

        return imageView;
    }

    /**
     * Sets up the index label for images.
     * @param index Index number to put in index label.
     * @return IndexLabel with index put in and some other customisable characteristics.
     */
    private Label setUpIndexLabel(int index) {
        Label indexLabel = new Label(Integer.toString(index));
        indexLabel.setPrefWidth(20);
        return indexLabel;
    }

    /**
     * Sets up the hbox for images.
     * @param indexLabel index label of image
     * @param imageView ImageView of image.
     */
    private void setUpHBox(Label indexLabel, ImageView imageView) {
        HBox hbox = new HBox(indexLabel, imageView);
        hbox.setAlignment(Pos.CENTER_LEFT);
        _imageCandidates.getItems().add(hbox);
    }

    /**
     * Functionality of create button to passes desired parameters to a naming scene,
     */
    @FXML private void create() {

        // check if length of video will be long enough for images to be displayed
        double lengthOfAudio = 0;
        for (String audio : _audioList) {
            File audioFile =
                    new File(Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term + File.separator + audio + ".wav");

            AudioInputStream audioInputStream;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                lengthOfAudio += (frames + 0.0) / format.getFrameRate();
            } catch (UnsupportedAudioFileException | IOException e) {
                new AlertFactory(AlertType.ERROR, "Error", null, "Could not load the audio " +
                        "file. Please try again with another audio file.");
            }
        }

        if (_imageChosenList.isEmpty()) {
            new AlertFactory(AlertType.ERROR, "Error", "No images selected",
                    "You have not selected any images");
        } else if (lengthOfAudio < _imageChosenList.size() * 2) {
            new AlertFactory(AlertType.ERROR, "Error", "The audio is too short", "Choose less " +
                    "images or make a longer audio for your creation.");
        } else {
            _mainApp.displayCreateCreationNamingScene(_term, _audioList, _imageChosenList, _musicSelection);
        }
    }

    /**
     * Asks for confirmation before returning to the main menu.
     */
    @FXML private void mainMenuPress() {
        Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Warning", "Return to Main Menu?",
                "Any unfinished progress will be lost").getAlert();
        if (alert.getResult() == ButtonType.OK) {
            mainMenu();
        }
    }

    /**
     * Move a desired image to the list of desired images
     */
    @FXML private void candidateToChosen() {

        listToList(_imageCandidatesList, _imageCandidates, _imageChosenList, _imageChosen);

    }

    /**
     * Move a undesired image back to a list of unselected images
     */
    @FXML private void chosenToCandidate() {

        listToList(_imageChosenList, _imageChosen, _imageCandidatesList, _imageCandidates);
    }

    /**
     * Moves the selected item from one list to the end of another list.
     * @param fromList The list that the item is from.
     * @param fromListView The ListView that the item is displayed in.
     * @param toList The list that the item is going to.
     * @param toListView The ListView that the item will be displayed in.
     */
    private void listToList(ArrayList<String> fromList, ListView<HBox> fromListView,
                            ArrayList<String> toList, ListView<HBox> toListView) {
        int index = fromListView.getSelectionModel().getSelectedIndex();

        if (index != -1) {
            // add to toList
            toList.add(fromList.get(index));
            addToEndOfList(fromListView.getItems().get(index), toListView);

            // remove from fromList
            fromList.remove(index);
            fromListView.getItems().remove(index);

            sortLists();
        }
    }


    /**
     * Shift an image up the list, only if it is not at the top
     */
    @FXML private void moveChosenUp() {

        int currentIndex = _imageChosen.getSelectionModel().getSelectedIndices().get(0);

        if (currentIndex > 0) {
            int newIndex = currentIndex - 1;
            reorderList(currentIndex, newIndex, _imageChosenList, _imageChosen);
        }

    }

    /**
     * Shift an image down the list, only if it is not at the bottom
     */
    @FXML private void moveChosenDown() {

        int currentIndex = _imageChosen.getSelectionModel().getSelectedIndex();

        if (currentIndex != -1 && currentIndex < _imageChosen.getItems().size() - 1) {

            int newIndex = currentIndex + 1;
            reorderList(currentIndex, newIndex, _imageChosenList, _imageChosen);
        }

    }

    /**
     * Reorder an item in the selected list to a new index.
     * @param currentIndex The index the item is currently at.
     * @param newIndex The index the item will be in.
     * @param list The list the item is from.
     * @param listView The ListView the item is displayed in.
     */
    private void reorderList(int currentIndex, int newIndex,
                             ArrayList<String> list, ListView<HBox> listView) {
        HBox item = listView.getSelectionModel().getSelectedItem();
        String chosenURL = list.get(currentIndex);

        // remove at current position
        listView.getItems().remove(currentIndex);
        list.remove(currentIndex);

        // insert at new position
        listView.getItems().add(newIndex, item);
        list.add(newIndex, chosenURL);

        sortLists();
    }

    /**
     * Move a selected image to the end of the other ListView
     *
     * @param candidate The item to move.
     * @param imageList The ListView to move the item to.
     */
    private void addToEndOfList(HBox candidate, ListView<HBox> imageList) {
        // remove numbering and put new numbering in
        candidate.getChildren().remove(0);
        Label newLabel = new Label(Integer.toString(imageList.getItems().size() + 1));
        candidate.getChildren().add(0, newLabel);

        // add to end of list
        imageList.getItems().add(candidate);
    }

    /**
     * Sort the images in each TaskView
     */
    private void sortLists() {

        sort(_imageCandidates);
        sort(_imageChosen);
    }

    /**
     * Sorts the images according to their position so that their new label is in accordance with
     * their current position. I.E. the first image will have the index label 1.
     * @param list The ListView to sort.
     */
    private void sort(ListView<HBox> list) {
        int index = 1;
        for (HBox item : list.getItems()) {
            // remove numbering
            item.getChildren().remove(0);
            // add new numbering
            Label newLabel = new Label(Integer.toString(index));
            item.getChildren().add(0, newLabel);

            index++;
        }
    }

}
