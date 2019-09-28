package main.java.tasks;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

/**
 * Controller to view the already-made creations
 * @author wcho400
 *
 */
public class ViewSearchsTask extends Task<ObservableList<String>>{

	String s = File.separator;
	
	@Override
	protected ObservableList<String> call() throws Exception {
		File folder = new File(System.getProperty("user.dir")+s+"bin"+s+"audio");

		File[] arrayOfFolders = folder.listFiles((file) -> {
				return true;
		});
		
		List<File> listOfFolders = Arrays.asList(arrayOfFolders);
		Collections.sort(listOfFolders);
		
		ObservableList<String> outputList = FXCollections.observableArrayList();
		int lineNumber = 1;

//		 only gives the name of the file, and gives a number as 
//		 indication of the number of current creations.
		for (File file: arrayOfFolders) {
			String name = file.getName().replace(".mp4", "");
			outputList.add(lineNumber + ". " + name + "\n");
			lineNumber++;
		}
		
		return outputList;

	}

	
}
