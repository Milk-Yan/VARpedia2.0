package main.java.application;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.concurrent.Task;
import javafx.scene.control.ListView;

/**
 * Searches and returns the list of current creations.
 * @author Milk
 *
 */
public class ViewTask extends Task<Void>{

	private ListView<String> _currentCreations;
	
	@Override
	protected Void call(){
		
		File folder = new File("./bin/creations");
		
		File[] arrayOfCreations = folder.listFiles((file) -> {
			if (file.getName().contains(".mp4") ) {
				return true;
			} else {
				return false;
			}
		});
		
		// sorts the list of creations in alphabetical order.
		List<File> listOfCreations = Arrays.asList(arrayOfCreations);
		Collections.sort(listOfCreations);

		ListView<String> outputList = new ListView<String>();
		int lineNumber = 1;

		// only gives the name of the file, and gives a number as 
		// indication of the number of current creations.
		for (File file: listOfCreations) {
			String name = file.getName().replace(".mp4", "");
			outputList.getItems().add(lineNumber + ". " + name + "\n");
			lineNumber++;
		}

		_currentCreations = outputList;
		
		return null;
	}
	
	protected ListView<String> getCurrentCreations() {
		return _currentCreations;
	}
	

}
