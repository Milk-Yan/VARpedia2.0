package main.java.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import main.java.controllers.Controller;

import java.io.IOException;

/**
 * Loads the scenes with FXMLLoader.
 *
 * @author Milk
 */
public class SceneFactory {

    private Scene _scene;
    private Controller _controller;

    public SceneFactory(SceneType sceneType, Main mainApp) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(sceneType.getAddress()));

        try {

            Parent layout = loader.load();

            // give the controller the main app
            _controller = loader.getController();
            _controller.setMainApplication(mainApp);

            _scene = new Scene(layout);

        } catch (IOException e) {
            new AlertFactory(AlertType.ERROR, "IOException", "Oops", "Something wrong happened " +
                    "when making the scene. Sorry :(");
            e.printStackTrace();
            mainApp.displayMainMenuScene();
        }

    }

    /**
     * @return Scene loaded.
     */
    public Scene getScene() {
        return _scene;
    }

    /**
     * @return Controller of scene loaded.
     */
    public Controller getController() {
        return _controller;
    }
}
