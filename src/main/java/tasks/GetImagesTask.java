package main.java.tasks;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.application.Main;
import main.java.application.StringManipulator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Gets the images for the Wikipedia term on the Flickr API.
 */
public class GetImagesTask extends Task<Void> {

    private String _term;
    private Main _mainApp;
    private ArrayList<String> _audioList;
    private String _musicSelection;

    /**
     * Gets images from the FlickrAPI.
     * @param term The Wikipedia term to search.
     * @param mainApp The main of the application, used for switching between scenes.
     * @param audioList The list of audio to include in the finished product.
     * @param musicSelection The music selected to include in the finished product.
     */
    public GetImagesTask(String term, Main mainApp, ArrayList<String> audioList,
                         String musicSelection) {
        _term = term;
        _mainApp = mainApp;
        _audioList = audioList;
        _musicSelection = musicSelection;
    }

    /**
     * Invoked when the Task is executed. Performs the background thread logic to get the images
     * from the Flickr API. Credits to Nasser and Catherine for most of this code.
     */
    @Override
    protected Void call() {

        File imageFolder = new File(Folders.TEMP_IMAGES_FOLDER.getPath() + File.separator + _term);
        imageFolder.mkdirs();

        try {
            // get the api key from text files
            String apiKey = getAPIKey("apiKey");
            String sharedSecret = getAPIKey("sharedSecret");

            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

            String query = _term;
            int resultsPerPage = 10;
            int page = 0;

            PhotosInterface photos = flickr.getPhotosInterface();
            SearchParameters params = new SearchParameters();
            params.setSort(SearchParameters.RELEVANCE);
            params.setMedia("photos");
            params.setText(query);

            PhotoList<Photo> results = photos.search(params, resultsPerPage, page);

            for (Photo photo : results) {
                if (isCancelled()) {
                    break;
                }
                try {
                    BufferedImage image = photos.getImage(photo, Size.LARGE);
                    String filename =
                            query.trim().replace(' ', '-') + "-" + System.currentTimeMillis() +
                                    "-" + photo.getId() + ".jpg";
                    File outputFile = new File(imageFolder, filename);
                    ImageIO.write(image, "jpg", outputFile);
                } catch (FlickrException fe) {
                    System.err.println("Ignoring image " + photo.getId() + ": " + fe.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method is called whenever the Task transforms to the cancelled state. It tells the
     * user and goes back to the main menu.
     */
    @Override
    public void cancelled() {
        super.cancelled();

        Platform.runLater(() -> {
            new AlertFactory(Alert.AlertType.ERROR, "Error", "Could not get Flickr images",
                    "Check your internet connection or try again later.");
            _mainApp.displayMainMenuScene();
        });
    }

    /**
     * This method is called when the Task transforms to the succeeded state. It switches scenes
     * to allow the user to select the images they want.
     */
    @Override
    public void succeeded() {
        Platform.runLater(() -> {
            _mainApp.displayCreateCreationChooseImagesScene(_term, _audioList, _musicSelection);
        });
    }

    /**
     * Get the API key from the text file.
     * @param keyType The type of key to look for.
     * @return The key string.
     */
    private String getAPIKey(String keyType) {
        String config = System.getProperty("user.dir")
                + System.getProperty("file.separator") + "flickr-api-keys.txt";
        File keyFile = new File(config);

        return new StringManipulator().readFromFile(keyFile, keyType);
    }

}
