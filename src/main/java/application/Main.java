package main.java.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.controllers.createAudio.AudioNaming;
import main.java.controllers.createAudio.ChooseText;
import main.java.controllers.createCreation.ChooseAudio;
import main.java.controllers.createCreation.ChooseImages;
import main.java.controllers.createCreation.CreationNaming;
import main.java.controllers.quiz.Quiz;
import main.java.controllers.view.VideoPlayer;
import main.java.controllers.view.ViewCreations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main class (entry point) of the VARpedia. Extends JavaFX Application.
 * Facilitates changing of screens for the application, set ups, and cleans up temporary files.
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

    /**
     * Launches the main application. This is a JavaFX Application method that loads and initialises
     * the JavaFX Application Thread. An instance of this class is then constructed on the JavaFX
     * Application Thread.
     *
     * @param args The argument given by the system on startup.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for this application. This method is called immediately after this class
     * is loaded and constructed. This method is not called on the JavaFX Application Thread. Scenes
     * and Stages cannot be constructed in this method, but other JavaFX objects can be constructed.
     *
     * @param primaryStage The primary stage of the application
     */
    @Override
    public void start(Stage primaryStage) {

        _primaryStage = primaryStage;
        primaryStage.setTitle("VARpedia");

        createFolders();
        displayMainMenuScene();

    }

    /**
     * Initialises the folders required for this application.
     */
    private void createFolders() {

        // insert folders to create (if more need to be added) here
        Folders[] foldersToCreate = new Folders[]{Folders.CREATION_SCORE_NOT_MASTERED_FOLDER,
                Folders.CREATION_SCORE_MASTERED_FOLDER, Folders.AUDIO_PRACTICE_FOLDER,
                Folders.AUDIO_TEST_FOLDER, Folders.CREATION_PRACTICE_FOLDER,
                Folders.CREATION_TEST_FOLDER};

        for (Folders folderPath : foldersToCreate) {
            folderPath.getFile().mkdirs();
        }

    }

    // ---------------------------------------------------------------------------------------------
    // -----------------------------DISPLAY METHODS-------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Changes the primary stage to the main menu scene.
     */
    public void displayMainMenuScene() {
        // only clean the temporary files when the user goes back to the main screen, i.e. cancels
        // whatever they are doing.
        cleanUpTempFiles();

        _currentScene = new SceneFactory(SceneType.MainMenu, this).getScene();
        update();
    }

    /**
     * Changes the primary stage to search for a Wikipedia term.
     */
    public void displayCreateAudioSearchScene() {
        _currentScene = new SceneFactory(SceneType.CreateAudioSearch, this).getScene();
        update();
    }

    /**
     * Changes the primary stage to show the displays of search results as well as editing text
     * scene, to choose the text to include in the audio creation.
     *
     * @param term          The term searched.
     * @param searchResults The search results for the term.
     */
    public void displayCreateAudioChooseTextScene(String term, String searchResults) {

        // get the loading search results controller so that the term and
        // searchResults can be passed in
        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateAudioChooseText, this);

        ChooseText controller = (ChooseText) sceneFactory.getController();
        controller.setUp(term, searchResults);

        _currentScene = sceneFactory.getScene();
        _audioWikitScene = _currentScene;
        update();

    }

    /**
     * Set scene back to original Wikipedia search display
     */
    public void displayPreviousAudioScene() {
        _currentScene = _audioWikitScene;
        update();
    }

    /**
     * Changes the primary stage to allow the audio creation to be named.
     *
     * @param term       The term searched.
     * @param chosenText The selected text to be synthesised by the audio synthesiser.
     * @param voice      The voice to synthesise the audio.
     */
    public void displayCreateAudioNamingScene(String term, String chosenText, String voice) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateAudioNaming, this);
        AudioNaming controller = (AudioNaming) sceneFactory.getController();
        controller.setUp(term, chosenText, voice);

        _currentScene = sceneFactory.getScene();
        update();
    }

    /**
     * Changes the primary stage to show a list of existing Wikipedia terms that have audio files
     * created, so that they can be selected for the creation of a video creation.
     */
    public void displayCreateCreationSearchScene() {

        _currentScene = new SceneFactory(SceneType.CreateCreationSearch, this).getScene();
        update();

    }

    /**
     * Changes the primary stage to allow user to select and order the audio files that they want
     * to include in
     * their video creation.
     *
     * @param term The Wikipedia term searched for the creation.
     */
    public void displayCreateCreationChooseAudioScene(String term) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateCreationChooseAudio, this);
        ChooseAudio controller = (ChooseAudio) sceneFactory.getController();
        controller.setUp(term);

        _currentScene = sceneFactory.getScene();
        update();
    }

    /**
     * Changes the primary stage to allow creation to be named.
     *
     * @param term      The Wikipedia term of the video creation.
     * @param audioList The list of chosen audio for the video creation.
     * @param imageList The list of chosen images for the video creation.
     */
    public void displayCreateCreationNamingScene(String term, ArrayList<String> audioList,
                                                 ArrayList<String> imageList,
                                                 String musicSelection) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateCreationNaming, this);
        CreationNaming controller = (CreationNaming) sceneFactory.getController();
        controller.setUp(term, audioList, imageList, musicSelection);

        _currentScene = sceneFactory.getScene();
        update();
    }

    /**
     * Changes the primary stage to display 10 relevant images for the search term. Allows user to
     * choose images and order these images.
     *
     * @param term      The Wikipedia term for the creation.
     * @param audioList The list of chosen audio for the creation.
     */
    public void displayCreateCreationChooseImagesScene(String term, ArrayList<String> audioList,
                                                       String musicSelection) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.CreateCreationChooseImages, this);
        ChooseImages controller = (ChooseImages) sceneFactory.getController();
        controller.setUp(term, audioList, musicSelection);

        _currentScene = sceneFactory.getScene();
        update();
    }

    /**
     * Changes the primary stage to show the creations the user has created up to this point.
     */
    public void displayViewCreationsScene() {

        SceneFactory sceneFactory = new SceneFactory(SceneType.ViewCreations, this);
        ViewCreations controller = (ViewCreations) sceneFactory.getController();
        controller.setUp();

        _currentScene = sceneFactory.getScene();
        update();

    }

    /**
     * Creates a new stage to play the chosen video creation.
     *
     * @param videoFile The video file to play.
     */
    public void playVideo(File videoFile) {

        SceneFactory sceneFactory = new SceneFactory(SceneType.VideoPlayer, this);
        VideoPlayer controller = (VideoPlayer) sceneFactory.getController();
        controller.setUp(videoFile);


    }

    /**
     * Changes the primary stage
     *
     * @param includeMastered Whether to include the mastered terms or not in the quiz.
     */
    public void displayQuizScene(boolean includeMastered) {
        SceneFactory sceneFactory = new SceneFactory(SceneType.Quiz, this);
        Quiz controller = (Quiz) sceneFactory.getController();
        controller.setIncludeMastered(includeMastered);

        _currentScene = sceneFactory.getScene();
        update();
    }

    // ---------------------------------------------------------------------------------------------
    // -----------------------------HELPER METHODS--------------------------------------------------
    // ---------------------------------------------------------------------------------------------

    /**
     * Update the primary stage to show the current scene. Used whenever it is necessary to
     * change the primary stage.
     */
    private void update() {
        _primaryStage.setScene(_currentScene);
        _primaryStage.show();
    }

    /**
     * Clean up any temporary files created previously. These files are all inside the temporary
     * folder, so can just be deleted with bash forcefully and recursively.
     */
    private void cleanUpTempFiles() {
        File tempFolder = Folders.TEMP_FOLDER.getFile();

        try {
            new ProcessBuilder("bash", "-c", "rm -rf " + tempFolder).start();
        } catch (IOException e) {
            // don't do anything
        }
    }

}
