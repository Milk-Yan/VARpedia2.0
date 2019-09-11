package main.java.tasks;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class ImageScraperTask extends Task<Void>{

	int _totalNumberOfImages;
	int _wantedImageNumber;
	
	protected ImageScraperTask(int imageNumber) {
		_wantedImageNumber = imageNumber;
	}
	@Override
	protected Void call() throws Exception {
		
		// make temp folder to store images
		new File("./bin/temp").mkdirs();
		
		String term = WikiApplication.getInstance().getCurrentTerm();
		String s = File.separator;
		
		Process process = new ProcessBuilder("bash", "-c", 
				// save the total number of images in a variable
				"urls=$(" +
				// grab the html from the url
				"curl www.flickr.com/search/?text=" + term + " | " +
				// find the image URLs from the html
				"grep -oh live.staticflickr.com/.*jpg);" + 
				
				// count the number of images
				"totalImageNumber=$(wc -l $urls);" + 
				// compare wanted number and total number
				"if [ " + _wantedImageNumber + "-gt $totalImageNumber ];" +
				"then;" +
					"return 99;" +
				"else;" +
					// download the wanted number of images
					"wantedUrls=$($urls -head -n " + _wantedImageNumber + ")" +
					"wget -i $wantedUrls -P " + System.getProperty("user.dir") +
					 s +"bin" + s + "temp;" +
				"fi").start();
		
		
		List<String> totalImageNumber = Files.readAllLines(Paths.get(System.getProperty("user.dir") + s + "bin" + s + "temp" + s + "urlNumber.txt"));
		_totalNumberOfImages = Integer.parseInt(totalImageNumber.get(0));
		
		return null;
	}
	
	protected int getTotalNumberOfImages() {
		return _totalNumberOfImages;
	}
	
	protected void downloadImages(int numberImages) {
		
	}
	
	

}
