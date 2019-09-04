package main.java.application;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;


public class SceneMaker {
	
	private Scene _scene;

	public SceneMaker(SceneType sceneType) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(this.getClass().getResource(sceneType.getAddress()));
		try {
			Parent layout = loader.load();
			_scene = new Scene(layout);
		} catch (IOException e) {
			new AlertMaker(AlertType.ERROR, "IOException", "Oops", "Something wrong happened when making the scene. Sorry :(");
		}
		
	}
	
	public Scene getScene() {
		return _scene;
	}
}
