package main.java.application;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class ImageScraperTask extends Task<Void>{

	int _totalNumberOfImages;
	
	@Override
	protected Void call() throws Exception {
		
		Process process = new ProcessBuilder("bash", "-c", "").start();
		
		
		
		Platform.runLater(() -> {
			
		}); 
		
		return null;
	}
	
	protected int getTotalNumberOfImages() {
		return _totalNumberOfImages;
	}
	
	

}
