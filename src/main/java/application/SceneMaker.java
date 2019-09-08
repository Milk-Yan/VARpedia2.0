package main.java.application;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;

/**
 * Loads the scenes with FXMLLoader.
 * @author Milk
 *
 */
public class SceneMaker {
	
	private Scene _scene;

	public SceneMaker(SceneType sceneType) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource(sceneType.getAddress()));
		try {
			Parent layout = loader.load();
			_scene = new Scene(layout);
		} catch (IOException e) {
			e.printStackTrace();
			new AlertMaker(AlertType.ERROR, "IOException", "Oops", "Something wrong happened when making the scene. Sorry :(");
			WikiApplication.getInstance().displayMainMenu();
		}
		
	}
	
	/**
	 * 
	 * @return Scene loaded.
	 */
	public Scene getScene() {
		return _scene;
	}
}
