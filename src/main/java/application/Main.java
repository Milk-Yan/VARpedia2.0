package main.java.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllers.createAudio.AudioNaming;
import main.java.controllers.createAudio.ChooseText;
import main.java.controllers.createCreation.ChooseAudio;
import main.java.controllers.createCreation.ChooseImages;
import main.java.controllers.createCreation.CreationNaming;
import main.java.controllers.loading.*;
import main.java.controllers.view.VideoPlayer;
import main.java.controllers.view.ViewCreations;
import main.java.tasks.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Main class of the Main. Extends JavaFX Application.
 * Allows user to create and manage movie creations from Wikipedia
 * entries of the user's choice. Enjoy!
 *
 * @author Milk, OverCry
 */
public class Main extends Application {

    private Scene _currentScene;
    private Scene _audioWikitScene;
    private Stage _primaryStage;

    // ---------------------------------------------------------------------------------------------
    // -----------------------------INITIALISATION--------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        _primaryStage = primaryStage;
        primaryStage.setTitle("Wiki Application");

        createFolders();
        displayMainMenuScene();

    }

    /**
     * Initialises folders if they do not already exist.
     */
    private void createFolders() {
        String s = File.separator;
        new File(System.getProperty("user.dir") + s + "bin" + s + "creations").mkdirs();
        new File(System.getProperty("user.dir") + s + "bin" + s + "audio").mkdirs();
    }

    // ---------------------------------------------------------------------------------------------
    // ----------------------DISPLAY METHODS (MAIN)-------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Set the current scene back to the main menu
     */
    public void displayMainMenuScene() {
        // only clean the temporary files when the user goes back to the main screen, i.e. cancels
        // whatever they are doing.
        cleanUpTempFiles();

        _currentScene = new SceneFactory(SceneType.MainMenu, this).getScene();
        update();
    }

    // ---------------------------------------------------------------------------------------------
    // ----------------------DISPLAY METHODS (AUDIO)------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Change the scene to search for a wikit term
     */
    public void displayCreateAudioSearchScene() {

        _currentScene = new SceneFactory(SceneType.CreateAudioSearch, this).getScene();
        update();
    }

    /**
     * Set scene to show the displays of search results as well as editing editing scene
     *
     * @param term
     * @param searchResults
     */
    public void displayCreateAudioChooseTextScene(String term, String searchResults) {

        // get the loading search results controller so that the term and
        // searchResults can be passed in
        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateAudioChooseText, this);

        ChooseText controller = (ChooseText) sceneFactory.getController();
        controller.setUp(term, searchResults);

        _currentScene = sceneFactory.getScene();
        //test might not work

        _audioWikitScene = _currentScene;
        update();

    }

    /**
     * Set scene to allow the audio creation to be named
     *
     * @param term
     * @param chosenText
     * @param voice
     */
    public void displayCreateAudioNamingScene(String term, String chosenText, String voice) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateAudioNaming, this);
        AudioNaming controller = (AudioNaming) sceneFactory.getController();
        controller.setUp(term, chosenText, voice);

        _currentScene = sceneFactory.getScene();
        update();
    }

    // ---------------------------------------------------------------------------------------------
    // ----------------------DISPLAY METHODS (CREATION)---------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Set scene to show a list of existing wikit searches
     */
    public void displayCreateCreationSearchScene() {

        _currentScene = new SceneFactory(SceneType.CreateCreationSearch, this).getScene();
        update();

    }

    /**
     * Set scene to show all existing audio files of the wikit search
     *
     * @param term
     */
    public void displayCreateCreationChooseAudioScene(String term) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateCreationChooseAudio, this);
        ChooseAudio controller = (ChooseAudio) sceneFactory.getController();
        controller.setUp(term);

        _currentScene = sceneFactory.getScene();
        update();
    }

    /**
     * Set scene to allow creation to be named
     *
     * @param term
     * @param audioList
     * @param imageList
     */
    public void displayCreateCreationNamingScene(String term, ArrayList<String> audioList,
                                                 ArrayList<String> imageList) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateCreationNaming, this);
        CreationNaming controller = (CreationNaming) sceneFactory.getController();
        controller.setUp(term, audioList, imageList);

        _currentScene = sceneFactory.getScene();
        update();
    }

    /**
     * Set seen to display images relevant to the search term
     *
     * @param term
     * @param audioList
     */
    public void displayCreateCreationChooseImagesScene(String term, ArrayList<String> audioList) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateCreationChooseImages, this);
        ChooseImages controller = (ChooseImages) sceneFactory.getController();
        controller.setUp(term, audioList);

        _currentScene = sceneFactory.getScene();
        update();
    }

    // ---------------------------------------------------------------------------------------------
    // ----------------------DISPLAY METHODS (VIEW)-------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * set seen to show the creations the user has created up to this point
     */
    public void displayViewCreationsScene() {

        SceneFactory sceneFactory = new SceneFactory(SceneType.ViewCreations, this);
        ViewCreations controller = (ViewCreations) sceneFactory.getController();
        controller.setUp();

        _currentScene = sceneFactory.getScene();
        update();

    }

    /**
     * Creates a new stage and plays the video.
     *
     * @param name
     */
    public void playVideo(String name) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.VideoPlayer, this);
        VideoPlayer controller = (VideoPlayer) sceneFactory.getController();
        controller.setUp(name);


    }

    // ---------------------------------------------------------------------------------------------
    // -----------------------------HELPER METHODS--------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Update the primary stage to show the current scene.
     */
    private void update() {
        _primaryStage.setScene(_currentScene);
        _primaryStage.show();
    }

    /**
     * clean up any temporary files created previously
     */
    private void cleanUpTempFiles() {
        File tempImagesFolder =
                new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
                        + "tempImages");
        File tempAudioFolder =
                new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
                        + "tempAudio");
        File tempVideoFolder =
                new File(System.getProperty("user.dir") + File.separator + "bin" + File.separator
                        + "tempVideo");

        if (tempImagesFolder.exists()) {
            for (File imageFolder : Objects.requireNonNull(tempImagesFolder.listFiles())) {
                for (File image : Objects.requireNonNull(imageFolder.listFiles())) {
                    image.delete();
                }
                imageFolder.delete();
            }
            tempImagesFolder.delete();
        }

        if (tempAudioFolder.exists()) {
            for (File audioFolder : Objects.requireNonNull(tempAudioFolder.listFiles())) {
                for (File audio : Objects.requireNonNull(audioFolder.listFiles())) {
                    audio.delete();
                }
                audioFolder.delete();
            }
            tempAudioFolder.delete();
        }

        if (tempVideoFolder.exists()) {
            for (File videoFolder : Objects.requireNonNull(tempVideoFolder.listFiles())) {
                for (File video : Objects.requireNonNull(videoFolder.listFiles())) {
                    video.delete();
                }
                videoFolder.delete();
            }
            tempVideoFolder.delete();
        }
    }

    // ---------------------------------------------------------------------------------------------
    // ----------------------DISPLAY PAST SCENES ---------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Set scene back to original wikit search display
     */
    public void setAudioScene() {
        _currentScene = _audioWikitScene;
        update();
    }
}
