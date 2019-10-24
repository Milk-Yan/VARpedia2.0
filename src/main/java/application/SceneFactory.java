package main.java.application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import main.java.controllers.Controller;

import java.io.IOException;

/**
 * Initialises JavaFX scenes for a specified scene. Loads the scenes with FXMLLoader.
 *
 * @author Milk, OverCry
 */
public class SceneFactory {

    private Scene _scene;
    private Controller _controller;

    /**
     * @param sceneType Type of JavaFX scene to create. Will contain the path of the scene.
     * @param mainApp The app required to change scenes.
     */
    public SceneFactory(SceneType sceneType, Main mainApp) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource(sceneType.getAddress()));

        try {

            Parent layout = loader.load();

            // give the controller the main app so it can switch between scenes in the application.
            _controller = loader.getController();
            _controller.setMainApplication(mainApp);

            _scene = new Scene(layout);

        } catch (IOException e) {
            new AlertFactory(AlertType.ERROR, "IOException", "Oops", "Something wrong happened " +
                    "when making the scene. Sorry :( Going back to the main menu now.");
            mainApp.displayMainMenuScene();
        }

    }

    /**
     * @return JavaFX scene loaded from FXMLLoader.
     */
    public Scene getScene() {
        return _scene;
    }

    /**
     * @return Controller of JavaFX scene loaded.
     */
    public Controller getController() {
        return _controller;
    }
}
