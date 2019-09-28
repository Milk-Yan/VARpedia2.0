package main.java.tasks;

import java.io.File;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

public class ViewAudioTask extends Task<TreeItem<String>>{
	
	private TreeItem<String> _root;

	@Override
	protected TreeItem<String> call() throws Exception {
		File audioFolder = new File(System.getProperty("user.dir")+File.separator+"bin"+File.separator+"audio");
		
		_root = new TreeItem<String>("Audio");
		_root.setExpanded(true);
		
		for (File audioTermFolder:audioFolder.listFiles()) {
			
			TreeItem<String> term = new TreeItem<String>(audioTermFolder.getName());
			_root.getChildren().add(term);
			
			for (File audioFile:audioTermFolder.listFiles()) {
				TreeItem<String> audio = new TreeItem<String>(audioFile.getName());
				term.getChildren().add(audio);
			}
			
		}
		
				
				
		return _root;
	}
	

}
