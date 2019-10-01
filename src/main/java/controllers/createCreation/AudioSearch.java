package main.java.controllers.createCreation;

import java.io.File;
import java.util.concurrent.ExecutionException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import main.java.application.AlertFactory;
import main.java.application.StringManipulator;
import main.java.controllers.Controller;
import main.java.tasks.ViewSearchsTask;

/**
 * Controller for AudioSearch
 * @author wcho400
 *
 */
public class AudioSearch extends Controller {

	@FXML
	private VBox _container;
	
	@FXML
	private ListView<String> _wikitTerm;

	@FXML
	private Button _enterBtn;

	@FXML
	private Button _mainMenuBtn;

	/**
	 * checks if any audio has been made and display possible wikit terms
	 * else return to main menu
	 */
	public void initialize() {
		
		ViewSearchsTask searchTask = new ViewSearchsTask();
		new Thread(searchTask).start();
		
		try {
			ObservableList<String> folderList = searchTask.get();
			
			if (folderList.isEmpty()) {
				_wikitTerm.setVisible(false);
				_enterBtn.setVisible(false);
				
				Text text = new Text("There are currently no audio available. Go back to the main menu to create audio first.");
				_container.getChildren().add(1, text);
				
				
			} else {
				_wikitTerm.setItems(folderList);
			}
			
			
		} catch (InterruptedException e) {
			// probably intended, don't do anything
		} catch (ExecutionException e) {
			Platform.runLater(() -> {
				new AlertFactory(AlertType.ERROR, "Error", "Execution Exception", "Could not display creations properly");
			});
		}
		
		// list out files generated and add into ListView
	}
	
	/**
	 * checks if audio files exists and pass the list of audio into the new scene
	 */
	@FXML
	private void enter() {

		//assume all made directors have SOMETHING in it
		String term = _wikitTerm.getSelectionModel().getSelectedItem();
		if (term!=null) {
			StringManipulator edit = new StringManipulator();
			term=edit.removeNumberedLines(term);
			
			// check if audio files exists
			File file = new File(System.getProperty("user.dir")+File.separator+"bin"+File.separator+"audio"+File.separator+term);
			
			if (file.isDirectory() && file.list().length>0) {
				
				_mainApp.displayCreateCreationChooseAudioScene(term);
				
			} else {
				
				new AlertFactory(AlertType.ERROR, "Error", "Audio files do not exist.", "You need to create audio files for this wikit term first.");
			
			}
		} else {
			new AlertFactory(AlertType.ERROR, "Error", "Audio wikit not selected.", "Please select a wikit search");
		}
	}

	/**
	 * return to main menu
	 */
	@FXML
	private void mainMenu() {
		_mainApp.displayMainMenuScene();
	}
}
