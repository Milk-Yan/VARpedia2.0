package main.java.tasks;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.*;
import javafx.application.Platform;
import javafx.concurrent.Task;
import main.java.application.Folders;
import main.java.application.Main;
import main.java.application.StringManipulator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class GetImagesTask extends Task<Void> {

    private String _term;
    private File _imageFolder;
    private Main _mainApp;
    private ArrayList<String> _audioList;
    private String _musicSelection;

    public GetImagesTask(String term, Main mainApp, ArrayList<String> audioList,
                         String musicSelection) {
        _term = term;
        _mainApp = mainApp;
        _audioList = audioList;
        _musicSelection = musicSelection;
    }

    @Override
    protected Void call() throws Exception {
        String s = File.separator;
        _imageFolder =
                new File(Folders.TEMP_IMAGES_FOLDER.getPath() + s + _term);
        _imageFolder.mkdirs();

        try {
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
                    File outputFile = new File(_imageFolder, filename);
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

    @Override
    public void cancelled() {

        // delete previously made folder
        if (_imageFolder != null && _imageFolder.exists() && Objects.requireNonNull(
                _imageFolder.listFiles()).length == 0) {
            _imageFolder.delete();
        }

        Platform.runLater(() -> {
            _mainApp.displayMainMenuScene();
        });
    }

    @Override
    public void succeeded() {
        Platform.runLater(() -> {
            _mainApp.displayCreateCreationChooseImagesScene(_term, _audioList, _musicSelection);
        });
    }

    public String getAPIKey(String keyType) {
        String config = System.getProperty("user.dir")
                + System.getProperty("file.separator") + "flickr-api-keys.txt";
        File keyFile = new File(config);

        String key = new StringManipulator().readFromFile(keyFile, keyType);

        return key;
    }

}
