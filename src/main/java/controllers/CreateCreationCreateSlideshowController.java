package main.java.controllers;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;

public class CreateCreationCreateSlideshowController extends Controller{

	@FXML
	private Text _enquiryText;
	
	@FXML
	private TextField _imageNumber;
	
	@FXML
	private Button _createBtn;
	
	@FXML
	private Button _mainMenuBtn;
	
	@FXML
	private void create() {
		String imageNumberStr = _imageNumber.getText();
		int imageNumber = checkValidity(imageNumberStr);
		if (imageNumber != -1) {
			Task<Void> imageScraper = new ImageScraperTask(imageNumber);
			WikiApplication.getInstance().displayLoadingScene(imageScraper);
			new Thread(imageScraper).start();
			
			imageScraper.setOnSucceeded(succeededEvent -> {
				if (imageNumber > ((ImageScraperTask) imageScraper).getTotalNumberOfImages()) {
					imageScraper.cancel();
					new AlertMaker(AlertType.ERROR, "Error", "Not enough images", "There are not enough images for this search term to proceed");
					WikiApplication.getInstance().displayPreviousCreateCreationCreateSlideShowScene();
				} else {
					//imageScraper.
				}
			});
		}
	}
	
	@FXML
	private void mainMenu() {
		WikiApplication.getInstance().displayMainMenu();
	}
	
	private int checkValidity(String numberStr) {
		try {
			int number = Integer.parseInt(numberStr);
			if (number > 0 && number <= 10) {
				return number;
			}
		} catch (NumberFormatException e) {
			// will return -1 anyway
		}
		return -1;
	}
}
