package main.java.application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

/**
 * Controller for functionality of Main.fxml
 * @author Milk
 *
 */
public class MainController {

	@FXML
	private Text _welcomeText;
	
	@FXML
	private Text _infoText;
	
	@FXML
	private Text _enquiryText;
	
	@FXML
	private Button _createButton;
	
	@FXML
	private Button _viewButton;
	
	@FXML
	private void create() {
		WikiApplication.getInstance().displayCreateScene();
	}
	
	@FXML
	private void view() {
		WikiApplication.getInstance().displayViewScene();
	}
}
