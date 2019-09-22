package main.java.tasks;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;
import main.java.application.WikiApplication;

public class ScrapeImagesTask extends Task<Void>{

	private String _term;
	private Process _imageScrapeProcess;
	private File _imageFolder;
	private WikiApplication _mainApp;
	private ObservableList<String> _audioList;

	public ScrapeImagesTask(String term, WikiApplication mainApp, ObservableList<String> audioList) {
		_term = term;
		_mainApp = mainApp;
		_audioList = audioList;
	}

	@Override
	protected Void call() throws Exception {
		String s = File.separator;

		try {
			_imageFolder = new File(System.getProperty("user.dir") + s + "bin" + s + "tempImages" + s + _term);
			_imageFolder.mkdirs();
			
			_imageScrapeProcess = new ProcessBuilder("bash", "-c", 

					// save the total number of images in a variable
					"urls=$(" +
					// grab the html from the url
					"curl www.flickr.com/search/?text=" + _term + " | " +
					// find the image URLs from the html
					"grep -oh live.staticflickr.com/.*jpg);" + 
					// get the first 10 images (or less) from the urls
					"wget -i $(urls -head -n 10) -P " + System.getProperty("user.dir") +
					s + "bin" + s + "tempImages" + s + _term
					).start();

			if (_imageScrapeProcess.exitValue() != 0) {
				Platform.runLater(() -> {
					new AlertMaker(AlertType.ERROR, "Error", "Couldn't get images", "An error occurred while"
							+ "trying to get the images.");

				}); 
			} 

		} catch (IOException e) {
			Platform.runLater(() -> {
				new AlertMaker(AlertType.ERROR, "Error", "I/O Exception", "Image scraping process exception.");
			});
		}


		return null;
	}

	@Override
	public void cancelled() {
		// destroy process
		_imageScrapeProcess.destroy();
		
		// remove temp images recursively
		for (File image:_imageFolder.listFiles()) {
			image.delete();
		}
		
		_imageFolder.delete();
		
		Platform.runLater(() -> {
			_mainApp.displayMainMenuScene();
		});
	}
	
	@Override
	public void succeeded() {
		Platform.runLater(() -> {
			_mainApp.displayCreateCreationChooseImagesScene(_term, _audioList);
		}); 
	}

}
