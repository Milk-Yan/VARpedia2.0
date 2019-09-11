package main.java.tasks;

import javafx.concurrent.Task;

public class PreviewAudioTask extends Task<Void>{

	private String _previewText;
	Process _process;
	
	public PreviewAudioTask(String previewText) {
		_previewText = previewText;
	}

	@Override
	protected Void call() throws Exception {
		
		_process = new ProcessBuilder("/bin/bash", "-c",
				"echo \""+_previewText+"\" | text2wave -o").start();
		
		return null;


	}
	
	@Override
	public void cancelled() {
		_process.destroy();
	}
	
}
