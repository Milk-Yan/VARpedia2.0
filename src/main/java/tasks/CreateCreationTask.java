package main.java.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;
import main.java.application.WikiApplication;

public class CreateCreationTask extends Task<Void>{

	private String _term;
	private ObservableList<String> _audioList;
	private WikiApplication _mainApp;
	private Scene _previousScene;
	
	private int _totalImageNumber;
	private int _wantedImageNumber;
	
	private Process _imageScrapeProcess;
	private Process _audioMergeProcess;
	private Process _createVideoProcess;
	private Process _mergeOverallProcess;
	
	public CreateCreationTask(String term, ObservableList<String> audioList, int imageNumber, WikiApplication mainApp, Scene previousScene) {
		_term = term;
		_audioList = audioList;
		_mainApp = mainApp;
		_previousScene = previousScene;
		
		_wantedImageNumber = imageNumber;
	}
	@Override
	protected Void call() throws Exception {
		
		// make temp folder to store images
		String s = File.separator;
		new File(System.getProperty("user.dir") + s + "bin" + s + "temp").mkdirs();
		
		imageScrape();
		
		createVideo();
		mergeOverall();
		
		
		
		
		List<String> totalImageNumber = Files.readAllLines(Paths.get(System.getProperty("user.dir") + s + "bin" + s + "temp" + s + "urlNumber.txt"));
		_totalImageNumber = Integer.parseInt(totalImageNumber.get(0));
		
		return null;
	}
	
	private void imageScrape() {
		
		String s = File.separator;
		
		try {
			_imageScrapeProcess = new ProcessBuilder("bash", "-c", 
				
					// save the total number of images in a variable
					"urls=$(" +
					// grab the html from the url
					"curl www.flickr.com/search/?text=" + _term + " | " +
					// find the image URLs from the html
					"grep -oh live.staticflickr.com/.*jpg);" + 
					// count the number of images
					"totalImageNumber=$(wc -l $urls);" + 
					// compare wanted number and total number
					"if [ " + _wantedImageNumber + "-gt $totalImageNumber ];" +
					"then;" +
						"return $totalImageNumber;" +
					"else;" +
						// download the wanted number of images
						"wantedUrls=$($urls -head -n " + _wantedImageNumber + ")" +
						"wget -i $wantedUrls -P " + System.getProperty("user.dir") +
						 s +"bin" + s + "temp;" +
					"fi"
					).start();
			
			if (_imageScrapeProcess.exitValue() != 0) {
				Platform.runLater(() -> {
					new AlertMaker(AlertType.ERROR, "Error", "Not enough images", "There are only " 
								+ _imageScrapeProcess.exitValue() + " images available for this term.");
					
				}); 
				return;
			} else {
				audioMerge();
			}
			
		} catch (IOException e) {
			Platform.runLater(() -> {
				new AlertMaker(AlertType.ERROR, "Error", "I/O Exception", "Image scraping process exception.");
			});
		}
		
	}
	
	private void audioMerge() {
		
		String s = File.separator;
		
		// create string of all audio files
		String audioFolder = System.getProperty("user.dir") + s + "bin" + s +
						"audio" + s;
		String audioFilesFolder = audioFolder + _term + s;
		String audioFiles = new String();
		
		for (String fileName:_audioList) {
			audioFiles.concat(audioFilesFolder + fileName + ".wav");
		}
		
		try {
			_audioMergeProcess = new ProcessBuilder("bash", "-c", 
					"sox " + audioFiles + " " + audioFolder + _name + ".wav"
						).start();
			
		} catch (IOException e) {
			Platform.runLater(() -> {
				new AlertMaker(AlertType.ERROR, "Error", "I/O Exception", "Audio merge process exception.");
			});
		}
	}
	
	private void createVideo() {
		
	}
	
	private void mergeOverall() {
		
	}

	@Override
	public void cancelled() {
		if (_imageScrapeProcess != null) {
			_imageScrapeProcess.destroy();
		}
		if (_audioMergeProcess != null) {
			_audioMergeProcess.destroy();
		}
		if (_createVideoProcess != null) {
			_createVideoProcess.destroy();
		}
		if (_mergeOverallProcess != null) {
			_mergeOverallProcess.destroy();
		}
	}
	
	@Override
	public void succeeded() {
		// to be moved inside imageScrape
		if (_wantedImageNumber > _totalImageNumber) {
			
			this.cancel();
			
			Platform.runLater(() -> {
				new AlertMaker(AlertType.ERROR, "Error", "Not enough images", "There are not enough images for this creation to proceed");
				_mainApp.displayPreviousScene(_previousScene);
			});

			
		} else {
			//imageScraper.
		}
	}
	


}
