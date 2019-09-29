package main.java.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import main.java.application.AlertMaker;

/**
 * Controller for CreateCreationChooseImages.fxml
 * Allows user to select images to be used
 * 
 * @author wcho400
 *
 */
public class CreateCreationChooseImagesController extends Controller{

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
	
	@FXML
	private Button _mainMenuBtn;

	/**
	 * Passes through list of desired audios, as well as teh search term
	 * @param term
	 * @param audioList
	 */
	public void setUp(String term, ArrayList<String> audioList) {
		_term = term;
		_audioList = audioList;

		_message.setText("Images files for " + term + ": ");

		File imageFolder = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator + 
				"tempImages" + File.separator + _term);
		
		int index = 1;
		
		for (File imageFile:imageFolder.listFiles()) {
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
		
		
	
	}

	/**
	 * button to passes desired parameters to a naming scene
	 * checks if the images are valid
	 */
	@FXML
	private void create() {
		
		// check if length of video will be long enough for images to be displayed
		double lengthOfAudio = 0;
		for (String audio:_audioList) {
			File audioFile = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator + 
					"audio" + File.separator + _term + File.separator + audio + ".wav");
			
			AudioInputStream audioInputStream;
			try {
				audioInputStream = AudioSystem.getAudioInputStream(audioFile);
				AudioFormat format = audioInputStream.getFormat();
				long frames = audioInputStream.getFrameLength();
				lengthOfAudio += (frames+0.0) / format.getFrameRate();  
			} catch (UnsupportedAudioFileException | IOException e) {
				// Don't do anything here for now.
			}
			
			
		}

		
		if (_imageChosenList.isEmpty()){
			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Create Creation?",
					"You have not selected any images").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				_mainApp.displayCreateCreationNamingScene(_term, _audioList, _imageChosenList);
			}
		} else if (lengthOfAudio < _imageChosenList.size()*2) {
			new AlertMaker(AlertType.ERROR, "Error", "The video is too short", "Choose less images");
		} else {
			_mainApp.displayCreateCreationNamingScene(_term, _audioList, _imageChosenList);
		}
	}

	/**
	 * returns to main menu
	 * asks for confirmation before action
	 */
	@FXML
	private void mainMenu() {
		Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Return to Main Menu?",
				"Any unfinished progress will be lost").getAlert();
		if (alert.getResult() == ButtonType.OK) {
			_mainApp.displayMainMenuScene();
		}
	}
	
	/**
	 * method to move a desired image to the list of desired images
	 */
	@FXML
	private void candidateToChosen() {
		
		ObservableList<HBox> candidates = _imageCandidates.getSelectionModel().getSelectedItems();
		
		ArrayList<String> toDelete = new ArrayList<String>();
		for (int index:_imageCandidates.getSelectionModel().getSelectedIndices()) {
			String imageURL = _imageCandidatesList.get(index);
			_imageChosenList.add(imageURL);
			toDelete.add(imageURL);
		}
		
		_imageCandidatesList.removeAll(toDelete);
		
		for (HBox candidate:candidates) {
			addToEndOfList(candidate, _imageChosen);
		}
		
		_imageCandidates.getItems().removeAll(candidates);
		sortLists();
		
	}
	
	/**
	 * method to move a undesired image back to a list of unselected images
	 */
	@FXML
	private void chosenToCandidate() {
		ObservableList<HBox> chosen = _imageChosen.getSelectionModel().getSelectedItems();
		
		ArrayList<String> toDelete = new ArrayList<String>();
		for (int index:_imageChosen.getSelectionModel().getSelectedIndices()) {
			String imageURL = _imageChosenList.get(index);
			_imageCandidatesList.add(imageURL);
			toDelete.add(imageURL);
		}
		
		_imageChosenList.removeAll(toDelete);
		
		for (HBox chosenBoxes:chosen) {
			addToEndOfList(chosenBoxes, _imageCandidates);
		}
		
		_imageChosen.getItems().removeAll(chosen);
		sortLists();
	}
	
	/**
	 * method to shift an image up the list
	 * only if it is not at the top
	 */
	@FXML
	private void moveChosenUp() {
		
		if (_imageChosen.getSelectionModel().getSelectedItems().size()==1) {
			int currentIndex = _imageChosen.getSelectionModel().getSelectedIndices().get(0);
			
			if (currentIndex == 0) {
				new AlertMaker(AlertType.ERROR, "Error", "Cannot move", "Already at top of list");
				return;
			}
			
			int newIndex = currentIndex-1;
			HBox chosenImage = _imageChosen.getSelectionModel().getSelectedItem();
			String chosenImageURL = _imageChosenList.get(currentIndex);
			
			// remove at current position
			_imageChosen.getItems().remove(currentIndex);
			_imageChosenList.remove(currentIndex);
			
			// insert at new position
			_imageChosen.getItems().add(newIndex, chosenImage);
			_imageChosenList.add(newIndex, chosenImageURL);
			
			sortLists();
		} else {
			new AlertMaker(AlertType.ERROR, "Error", "Invalid selection", "Can only reorder one item at a time.");
		}

	}
	
	/**
	 * method to shift an image down the list
	 * only if it is not at the bottom
	 */
	@FXML
	private void moveChosenDown() {
		
		if (_imageChosen.getSelectionModel().getSelectedItems().size()==1) {
			int currentIndex = _imageChosen.getSelectionModel().getSelectedIndex();
			
			if (currentIndex == _imageChosen.getItems().size()-1) {
				new AlertMaker(AlertType.ERROR, "Error", "Cannot move", "Already at end of list");
				return;
			}
			
			int newIndex = currentIndex+1;
			HBox chosenImage = _imageChosen.getSelectionModel().getSelectedItem();
			String chosenImageURL = _imageChosenList.get(currentIndex);
			
			// remove at current position
			_imageChosen.getItems().remove(currentIndex);
			_imageChosenList.remove(currentIndex);
			
			// insert at new position
			_imageChosen.getItems().add(newIndex, chosenImage);
			_imageChosenList.add(newIndex, chosenImageURL);
			
			sortLists();
		}
		
		new AlertMaker(AlertType.ERROR, "Error", "Invalid selection", "Can only reorder one item at a time.");
	}
	
	/**
	 * method to move a selected image to the other ListView
	 * @param candidate
	 * @param imageList
	 */
	private void addToEndOfList(HBox candidate, ListView<HBox> imageList) {
		// remove numbering and put new numbering in
		candidate.getChildren().remove(0);
		Label newLabel = new Label(Integer.toString(imageList.getItems().size()+1));
		candidate.getChildren().add(0, newLabel);

		// add to end of list
		imageList.getItems().add(candidate);
	}
	
	/**
	 * method to sort the images in each TaskView
	 */
	private void sortLists() {
		int candidateIndex = 1;
		for (HBox candidate:_imageCandidates.getItems()) {
			// remove numbering
			candidate.getChildren().remove(0);
			// add new numbering
			Label newLabel = new Label(Integer.toString(candidateIndex));
			candidate.getChildren().add(0, newLabel);
			
			candidateIndex++;
		}
		
		int chosenIndex = 1;
		for (HBox chosen:_imageChosen.getItems()) {
			// remove numbering
			chosen.getChildren().remove(0);
			// add new numbering
			Label newLabel = new Label(Integer.toString(chosenIndex));
			chosen.getChildren().add(0, newLabel);
			
			chosenIndex++;
		}
	}

}
