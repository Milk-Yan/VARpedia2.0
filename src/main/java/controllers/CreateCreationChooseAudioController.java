package main.java.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import main.java.application.AlertMaker;
import main.java.tasks.ScrapeImagesTask;
import javafx.scene.control.Alert.AlertType;

public class CreateCreationChooseAudioController extends Controller{

	private String _term;
	private MediaPlayer _audioPlayer;

	@FXML
	private Text _title;

	@FXML
	private ListView<String> _audioCandidates;

	@FXML
	private ListView<String> _audioChosen;

	@FXML
	private Button _confirmBtn;

	@FXML
	private Button _listenBtn;

	@FXML 
	private Button _mainMenuBtn;

	public void setUp(String term) {
		_term = term;

		_title.setText("Audio files for " + term + ":");

		// make the ListView multiple-selection
		_audioCandidates.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		File folder = new File(System.getProperty("user.dir")+File.separator+"bin"+File.separator+"audio"+File.separator+_term);

		File[] arrayOfAudioFiles = folder.listFiles((file) -> {

			if (file.getName().contains(".wav") ) {
				return true;
			} else {
				return false;
			}

		});

		// sorts the list of creations candidates in alphabetical order.
		List<File> listOfAudioFiles = Arrays.asList(arrayOfAudioFiles);
		Collections.sort(listOfAudioFiles);

		// only gives the name of the file, and gives a number as 
		// indication of the number of current creations.
		ObservableList<String> audioList = FXCollections.observableArrayList();
		int lineNumber = 1;

		for (File file: listOfAudioFiles) {
			String name = file.getName().replace(".wav", "");
			audioList.add(lineNumber + ". " + name + "\n");
			lineNumber++;
		}

		_audioCandidates.setItems(audioList);
	}

	@FXML
	private void confirm() {

		stopAudioPlayer();

		ObservableList<String> selectedList = _audioChosen.getItems();

		if (selectedList.isEmpty()) {

			new AlertMaker(AlertType.ERROR, "Error", "No items selected", "Please select at least one audio file.");

		} else if (selectedList.size() > 10) {

			new AlertMaker(AlertType.ERROR, "Error", "Too many items selected", "Please selected less than 10 audio files");

		} else {

			// clear numbering
			ArrayList<String> selectedListClean = new ArrayList<String>();
			for (String audio:selectedList) {
				selectedListClean.add(audio.replaceFirst("\\d+\\. ", "").trim());
			}

			ScrapeImagesTask task = new ScrapeImagesTask(_term, _mainApp, selectedListClean);
			new Thread(task).start();

			_mainApp.displayLoadingScrapingImagesScene(_term, task);
			//_mainApp.displayCreateCreationCreateSlideshowScene(_term, selectedList);
			//			for (String audioName:selectedList) {
			//				// TO-DO: implement audio merging here (maybe a new task. likely a new task.)
			//			}
		}


	}

	@FXML
	private void moveChosenUp() {
		if (_audioChosen.getSelectionModel().getSelectedItems().size() == 1) {
			int currentIndex = _audioChosen.getSelectionModel().getSelectedIndices().get(0);

			if (currentIndex == 0) {
				new AlertMaker(AlertType.ERROR, "Error", "Cannot move", "Already at top of list");
				return;
			}

			int newIndex = currentIndex-1;
			String audioChosen = _audioChosen.getSelectionModel().getSelectedItems().get(0);

			// remove at current position
			_audioChosen.getItems().remove(currentIndex);

			// insert at new position
			_audioChosen.getItems().add(newIndex, audioChosen);

			// sorts list
			sortLists();
		} else {
			new AlertMaker(AlertType.ERROR, "Error", "Invalid selection", "Can only reorder one item at a time.");
		}
	}

	@FXML
	private void moveChosenDown() {
		if (_audioChosen.getSelectionModel().getSelectedItems().size() == 1) {
			int currentIndex = _audioChosen.getSelectionModel().getSelectedIndices().get(0);

			if (currentIndex == _audioChosen.getItems().size()-1) {
				new AlertMaker(AlertType.ERROR, "Error", "Cannot move", "Already at end of list");
				return;
			}

			int newIndex = currentIndex+1;
			String audioChosen = _audioChosen.getSelectionModel().getSelectedItems().get(0);

			// remove at current position
			_audioChosen.getItems().remove(currentIndex);

			// insert at new position
			_audioChosen.getItems().add(newIndex, audioChosen);

			// sorts list
			sortLists();
		} else {
			new AlertMaker(AlertType.ERROR, "Error", "Invalid selection", "Can only reorder one item at a time.");
		}
	}

	@FXML
	private void candidateToChosen() {
		ObservableList<String> candidates = _audioCandidates.getSelectionModel().getSelectedItems();

		for (String candidate:candidates) {
			addToEndOfList(candidate, _audioChosen);
		}

		_audioCandidates.getItems().removeAll(candidates);
		sortLists();
	}

	@FXML
	private void chosenToCandidate() {
		ObservableList<String> candidates = _audioChosen.getSelectionModel().getSelectedItems();
		for (String candidate:candidates) {
			addToEndOfList(candidate, _audioCandidates);
		}

		_audioChosen.getItems().removeAll(candidates);
		sortLists();
	}

	private void sortLists() {
		int indexCandidates = 1;
		int indexChosen = 1;
		ArrayList<String> currentItemsCandidates = new ArrayList<String>(_audioCandidates.getItems());
		ArrayList<String> currentItemsChosen = new ArrayList<String>(_audioChosen.getItems());

		// remove all items
		_audioCandidates.getItems().removeAll(currentItemsCandidates);
		_audioChosen.getItems().removeAll(currentItemsChosen);

		for (String audio:currentItemsCandidates) {
			// insert new items
			_audioCandidates.getItems().add(audio.replaceFirst("\\d+\\. ", indexCandidates + ". "));
			indexCandidates++;
		}

		for (String audio:currentItemsChosen) {
			// insert new items
			_audioChosen.getItems().add(audio.replaceFirst("\\d+\\. ", indexChosen + ". "));
			indexChosen++;
		}


	}

	private void addToEndOfList(String candidate, ListView<String> audioList) {
		// remove numbering
		candidate = candidate.replaceFirst("\\d+\\. ", "");

		// add to end of list
		int index = audioList.getItems().size() + 1;
		candidate = index + ". " + candidate;
		audioList.getItems().add(candidate);
	}


	@FXML
	private void listen() {

		stopAudioPlayer();

		ObservableList<String> audioCandidate = _audioCandidates.getSelectionModel().getSelectedItems();
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
			audioName = audioName.replaceFirst("\\d+\\. ", "").replaceAll("\n", "");

			File audioFile = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
					+ "audio" + File.separator + _term + File.separator + audioName + ".wav");

			Media audio = new Media(audioFile.toURI().toString());
			_audioPlayer = new MediaPlayer(audio);
			_audioPlayer.play();

		} else {
			new AlertMaker(AlertType.ERROR, "Error", "Invalid selection", "You can only listen to one audio at a time");
		}

	}

	// potential code to add elsewhere
	//	@FXML
	//	private void delete() {
	//		
	//		ObservableList<String> selectedList = _audioList.getSelectionModel().getSelectedItems();
	//		
	//		if (selectedList.isEmpty()) {
	//			
	//			new AlertMaker(AlertType.ERROR,"Error", "No items to delete", "Please select the item you wish to delete");
	//			
	//		} else {
	//			
	//			Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Confirmation", "Deleting files", 
	//									"Are you sure you want to delete " + selectedList.size() + " items?").getAlert();
	//			
	//			if (alert.getResult() == ButtonType.OK) {
	//				
	//				for (String audioName:selectedList) {
	//					
	//					String s = File.separator;
	//					String fileName = audioName.replaceFirst("\\d+. ", "") + ".wav";
	//					
	//					File audioFile = new File(System.getProperty("user.dir") + s + "bin" + s + "audio" +
	//											_term + s + fileName);
	//					
	//					audioFile.delete();
	//					
	//				}
	//			}
	//		}
	//	}

	@FXML
	private void mainMenu() {

		stopAudioPlayer();

		_mainApp.displayMainMenuScene();
	}

	private void stopAudioPlayer() {
		// stop current player if it is playing
		if (_audioPlayer != null) {
			_audioPlayer.stop();
			_audioPlayer = null;
		}
	}
}
