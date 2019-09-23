package main.java.tasks;

import javafx.concurrent.Task;

public class PreviewAudioTask extends Task<Void>{

	private String _voice=null;
	private String _previewText;
	Process _process;

	//This is for the replay with no voice selected
	public PreviewAudioTask(String previewText) {
		_previewText = previewText;
	}

	@Override
	protected Void call() throws Exception {

		if (_voice==null) {
			System.out.println("No Voice");
			_process = new ProcessBuilder("bash", "-c",
					"echo \"" + _previewText + "\" | text2wave -o").start();
		} else {
			//make it with voice
			System.out.println("Voice");
			//set back to null
			_voice=null;
		}

		return null;
	}

	public PreviewAudioTask(String previewText, String voice){
		_previewText=previewText;
		_voice=voice;

	}
	
	@Override
	public void cancelled() {
		_process.destroy();
	}
	
}
