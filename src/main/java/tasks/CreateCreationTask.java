package main.java.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import main.java.application.AlertFactory;
import main.java.application.WikiApplication;

public class CreateCreationTask extends Task<Void>{

	private String _name;
	private String _term;
	private ArrayList<String> _audioList;
	private ArrayList<String> _imageList;
	private WikiApplication _mainApp;
	
	private Process _audioMergeProcess;
	private Process _imageMergeProcess;
	private Process _mergeOverallProcess;
	
	public CreateCreationTask(String name, String term, ArrayList<String> audioList, ArrayList<String> imageList, WikiApplication mainApp) {
		_name = name;
		_term = term;
		_audioList = audioList;
		_imageList = imageList;
		_mainApp = mainApp;
		
	}
	
	@Override
	protected Void call() throws Exception {
		
		audioMerge();
		imageMerge();
		mergeOverall();
		
		return null;
	}
	
	private void audioMerge() {
		String s = File.separator;
		
		File tempFolder = new File(System.getProperty("user.dir") + s + "bin" + s + "tempAudio" + s + _term);
		tempFolder.mkdirs();

		// create string of all audio files
		String audioFolder = System.getProperty("user.dir") + s + "bin" + s +
						"audio" + s;
		String audioFilesFolder = audioFolder + _term + s;
		String audioFiles = "";

		for (String fileName:_audioList) {
			audioFiles = audioFiles.concat(audioFilesFolder + fileName + ".wav ");
		}

		try {
			_audioMergeProcess = new ProcessBuilder("bash", "-c", 
					"sox " + audioFiles + tempFolder + s + _name + ".wav"
						).start();
			try {
				_audioMergeProcess.waitFor();
				
			} catch (InterruptedException e) {
				// don't do anything
			}
			
		} catch (IOException e) {
			Platform.runLater(() -> {
				new AlertFactory(AlertType.ERROR, "Error", "I/O Exception", "Audio merge process exception.");
				_mainApp.displayMainMenuScene();
			});
		}
		
		if (_audioMergeProcess.exitValue() != 0) {
			Platform.runLater(() -> {
				new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The audio did not merge properly");
				_mainApp.displayMainMenuScene();
			});
		}
	}
	
	private void imageMerge() {
		String s = File.separator;
		
		String imageFilesFolder = System.getProperty("user.dir") + s + "bin" + s + "tempImages" + s + _term + s;
		
		String tempFolderPath = System.getProperty("user.dir") + s + "bin" + s + "tempVideo" + s + _term;
		File tempFolder = new File(tempFolderPath);
		tempFolder.mkdirs();
		
		int i = 0;
		
		for (String imageName:_imageList) {
			File image = new File(imageFilesFolder + imageName);
			File newImageName = new File(imageFilesFolder + "img" + i + ".jpg");
			image.renameTo(newImageName);
			
			i++;
		}
		
		try {
			_imageMergeProcess = new ProcessBuilder("bash", "-c", 
					// get length of audio file 
					"VIDEO_LENGTH=$(soxi -D " + System.getProperty("user.dir") + s + "bin" + s + "tempAudio" + 
					s + _term + s + _name + ".wav);" +
					// create slideshow from images with same length as audio, images change every 2 seconds, 30 fps
					"ffmpeg -framerate 1/2 -loop 1 -i " + imageFilesFolder + "img%01d.jpg -r 30 -t $VIDEO_LENGTH " +
					"-vf \"drawtext=fontfile=font.ttf:fontsize=200:fontcolor=white:"
					+ "x=(w-text_w)/2:y=(h-text_h)/2:text=\"" + _term +
					" -s 720x480" +
					// put video file in temp folder
					" -y " + tempFolderPath + s + _name + ".mp4" 
					).start();
			try {
				_imageMergeProcess.waitFor();
				
			} catch (InterruptedException e) {
				// don't do anything
			}
			
			if (_imageMergeProcess.exitValue() != 0) {
				Platform.runLater(() -> {
					new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The image did not merge properly");
					_mainApp.displayMainMenuScene();
				});
			}
			
		} catch (IOException e) {
			Platform.runLater(() -> {
				new AlertFactory(AlertType.ERROR, "Error", "I/O Exception", "Image merge process exception.");
				_mainApp.displayMainMenuScene();
			});
		}
	}
	
	private void mergeOverall() {
		
		String videoPath = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "tempVideo" 
							+ File.separator + _term + File.separator + _name + ".mp4";
		String audioPath = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "tempAudio" 
							+ File.separator + _term + File.separator + _name + ".wav";
		String creationPath = System.getProperty("user.dir") + File.separator + "bin" + File.separator + "creations" 
							+ File.separator + _name + ".mp4";
		try {
			_mergeOverallProcess = new ProcessBuilder("bash", "-c", 
					// get video
					"ffmpeg -i " + videoPath + " "  +
					// get audio
					"-i " + audioPath + " " +
					// combine
					"-strict -2 -y " + creationPath
					).start();
			
			try {
				_mergeOverallProcess.waitFor();
				
			} catch (InterruptedException e) {
				// don't do anything
			}
			
			
			if (_mergeOverallProcess.exitValue() != 0) {
				Platform.runLater(() -> {
					new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The video and image did not merge properly");
					_mainApp.displayMainMenuScene();
				});
			}
			
		} catch (IOException e) {
			Platform.runLater(() -> {
				new AlertFactory(AlertType.ERROR, "Error", "I/O Exception", "Overall merge process exception.");
				_mainApp.displayMainMenuScene();
			});
		}
	}

	@Override
	public void cancelled() {
		
		if (_audioMergeProcess != null) {
			_audioMergeProcess.destroy();
		}
		if (_imageMergeProcess != null) {
			_imageMergeProcess.destroy();
		}
		if (_mergeOverallProcess != null) {
			_mergeOverallProcess.destroy();
		}
	}
	
	@Override
	public void succeeded() {
		
		Platform.runLater(() -> {
//			new AlertFactory(AlertType.INFORMATION, "Complete", "Creation complete", "Let's go back to the main menu!");
//			_mainApp.displayMainMenuScene();
			Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Next", "Would you like to play your creation?",
					"Press 'OK'. Otherwise, press 'Cancel'").getAlert();
			if (alert.getResult() == ButtonType.OK) {
				//go to preview scene again
				//NOT DONE YET
				_mainApp.displayMainMenuScene();
				_mainApp.playVideo(_name);
			} else {
				_mainApp.displayMainMenuScene();
			}
		});
	}
	


}
