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
 * Controller for ChooseImages.fxml
 * Allows user to select images to be used
 *
 * @author wcho400
 */
public class ChooseImages extends Controller {

    private String _term;
    private ArrayList<String> _audioList;

    private ArrayList<String> _imageCandidatesList = new ArrayList<String>();
    private ArrayList<String> _imageChosenList = new ArrayList<String>();

    @FXML
    private Text _message;

    @FXML
    private ListView<HBox> _imageCandidates;

    @FXML
    private ListView<HBox> _imageChosen;

    /**
     * Passes through list of desired audios, as well as teh search term
     *
     * @param term
     * @param audioList
     */
    public void setUp(String term, ArrayList<String> audioList) {
        _term = term;
        _audioList = audioList;

        _message.setText("Images files for " + term + ": ");

        File imageFolder =
                new File(Folders.TempImagesFolder.getPath() + File.separator + _term);

        int index = 1;

        for (File imageFile : Objects.requireNonNull(imageFolder.listFiles())) {
            _imageCandidatesList.add(imageFile.getName());

            Image image = new Image(imageFile.toURI().toString());
            ImageView imageView = new ImageView(image);

            imageView.setFitHeight(200);
            imageView.setFitWidth(180);
            imageView.setPreserveRatio(true);

            Label indexLabel = new Label(Integer.toString(index));
            indexLabel.setPrefWidth(20);

            HBox hbox = new HBox(indexLabel, imageView);
            hbox.setAlignment(Pos.CENTER_LEFT);
            _imageCandidates.getItems().add(hbox);

            index++;
        }

        _imageChosen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _imageCandidates.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    /**
     * button to passes desired parameters to a naming scene
     * checks if the images are valid
     */
    @FXML
    private void create() {

        // check if length of video will be long enough for images to be displayed
        double lengthOfAudio = 0;
        for (String audio : _audioList) {
            File audioFile =
                    new File(Folders.AudioPracticeFolder.getPath() + File.separator + _term + File.separator + audio + ".wav");

            AudioInputStream audioInputStream;
            try {
                audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                AudioFormat format = audioInputStream.getFormat();
                long frames = audioInputStream.getFrameLength();
                lengthOfAudio += (frames + 0.0) / format.getFrameRate();
            } catch (UnsupportedAudioFileException | IOException e) {
                // Don't do anything here for now.
            }


        }


        if (_imageChosenList.isEmpty()) {
            new AlertFactory(AlertType.ERROR, "Error", "No images selected",
                    "You have not selected any images");
        } else if (lengthOfAudio < _imageChosenList.size() * 2) {
            new AlertFactory(AlertType.ERROR, "Error", "The audio is too short", "Choose less " +
                    "images or make a longer audio for your creation.");
        } else {
            _mainApp.displayCreateCreationNamingScene(_term, _audioList, _imageChosenList);
        }
    }

    /**
     * returns to main menu
     * asks for confirmation before action
     */
    @FXML
    private void mainMenuPress() {
        Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Warning", "Return to Main Menu?",
                "Any unfinished progress will be lost").getAlert();
        if (alert.getResult() == ButtonType.OK) {
            mainMenu();
        }
    }

    /**
     * method to move a desired image to the list of desired images
     */
    @FXML
    private void candidateToChosen() {

        listToList(_imageCandidatesList, _imageCandidates, _imageChosenList, _imageChosen);

    }

    /**
     * method to move a undesired image back to a list of unselected images
     */
    @FXML
    private void chosenToCandidate() {

        listToList(_imageChosenList, _imageChosen, _imageCandidatesList, _imageCandidates);
    }

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
     * method to shift an image up the list
     * only if it is not at the top
     */
    @FXML
    private void moveChosenUp() {

        int currentIndex = _imageChosen.getSelectionModel().getSelectedIndices().get(0);

        if (currentIndex > 0) {
            int newIndex = currentIndex - 1;
            reorderList(currentIndex, newIndex, _imageChosenList, _imageChosen);
        }

    }

    /**
     * method to shift an image down the list
     * only if it is not at the bottom
     */
    @FXML
    private void moveChosenDown() {

        int currentIndex = _imageChosen.getSelectionModel().getSelectedIndex();

        if (currentIndex != -1 && currentIndex < _imageChosen.getItems().size() - 1) {

            int newIndex = currentIndex + 1;
            reorderList(currentIndex, newIndex, _imageChosenList, _imageChosen);
        }


    }

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
     * method to move a selected image to the other ListView
     *
     * @param candidate
     * @param imageList
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
     * method to sort the images in each TaskView
     */
    private void sortLists() {

        sort(_imageCandidates);
        sort(_imageChosen);
    }

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
