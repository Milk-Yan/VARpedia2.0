package main.java.controllers;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;
import main.java.tasks.ViewAudioTask;
import main.java.tasks.ViewCreationsTask;

/**
 * Controller for functionality of View.fxml
 * @author Milk
 *
 */
public class ViewCreationsController extends Controller{

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

	@FXML
	private Button _mainMenuBtn;

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

		_mainApp.displayLoadingViewCreationsScene(viewCreationTask, viewAudioTask);

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
				new AlertMaker(AlertType.ERROR, "Error", "Execution Exception", "Could not execute the view function");
			});
		}



	}

	@FXML
	private void play() {
		
		stopAudioPlayer();

		Tab tab = _tabPane.getSelectionModel().getSelectedItem();

		if (tab == _creationTab) {
			String selectionName = _listOfCreations.getSelectionModel().getSelectedItem();
			String videoName = selectionName.replaceFirst("\\d+\\. ", "").replace("\n", "");

			if (videoName == null) {
				new AlertMaker(AlertType.ERROR, "Error", "Wrong selection", "Selection cannot be null");
			} else {

				if (!(selectionName == null)) {
					_mainApp.playVideo(videoName);
				}

			}
		} else {

			// all audio files are leaves
			if (_listOfAudio.getSelectionModel().getSelectedItem().isLeaf()) {
				String term = _listOfAudio.getSelectionModel().getSelectedItem().getParent().getValue();
				String audioName = _listOfAudio.getSelectionModel().getSelectedItem().getValue();
				File audioFile = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
						+ "audio" + File.separator + term + File.separator + audioName);

				Media audio = new Media(audioFile.toURI().toString());
				_audioPlayer = new MediaPlayer(audio);
				_audioPlayer.play();
			}

		}


	}

	@FXML
	private void delete() {
		stopAudioPlayer();
		
		Tab tab = _tabPane.getSelectionModel().getSelectedItem();

		if (tab == _creationTab) { 
			String creationName = _listOfCreations.getSelectionModel().getSelectedItem();
			if (creationName != null) {
				String videoName = creationName.replaceFirst("\\d+\\. ", "").replace("\n", "");
				Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Warning", "Confirmation",
						"Would you like to delete " + videoName + "?").getAlert();
				if (alert.getResult() == ButtonType.OK) {
					String s = File.separator;
					File fileCreation = new File(System.getProperty("user.dir")+s+"bin"+s+"creations"+s+videoName+".mp4");
					//File fileAudio = new File(System.getProperty("user.dir")+s+"bin"+s+"audio"+s+videoName+".mp4");
					fileCreation.delete();
					//fileAudio.delete();

					_mainApp.displayViewCreationsScene();
				}
			}
		} else {
			if (_listOfAudio.getSelectionModel().getSelectedItem().isLeaf()) {
				String audioName = _listOfAudio.getSelectionModel().getSelectedItem().getValue();
				Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Confirmation", "Deleting audio", "Do you want to delete " + audioName + "?").getAlert();
				if (alert.getResult() == ButtonType.OK) {
					String term = _listOfAudio.getSelectionModel().getSelectedItem().getParent().getValue();
					File audioFile = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
							+ "audio" + File.separator + term + File.separator + audioName);
					audioFile.delete();
					
					File audioFolder = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
							+ "audio" + File.separator + term);
					if (audioFolder.exists() && audioFolder.listFiles().length == 0) {
						audioFolder.delete();
					}
					_mainApp.displayViewCreationsScene();
				}
			} else if (_listOfAudio.getSelectionModel().getSelectedItem() != null){
				// selected is a folder
				String term = _listOfAudio.getSelectionModel().getSelectedItem().getValue();
				Alert alert = new AlertMaker(AlertType.CONFIRMATION, "Confirmation", "Deleting audio folder", 
						"Do you want to delete all audio for " + term + "?").getAlert();
				if (alert.getResult() == ButtonType.OK) {
					File audioFolder = new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
							+ "audio" + File.separator + term);

					for (File audio:audioFolder.listFiles()) {
						audio.delete();
					}
					audioFolder.delete();
					
					_mainApp.displayViewCreationsScene();

				}
			}
		}

	}

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
