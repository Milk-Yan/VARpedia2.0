package main.java.tasks;

import java.io.IOException;

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
			_process = new ProcessBuilder("bash", "-c",
					"echo \"" + _previewText + "\" | festival --tts").start();
		} else {
			//make it with voice
//			generatePreview();
//			_process = new ProcessBuilder("bash", "-c",
//					"festival -b .preview.scm").start();
			
			_process = new ProcessBuilder("bash", "-c",
					"echo -e \"(voice_"+_voice+") ;; \n(SayText \\\""+
			_previewText+"\\\" )\" | festival -i &>/dev/null").start();
			
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
	
	/**
//	 * method generates scm file for festival to play
//	 * @throws Exception
//	 */
//	private void generatePreview() throws Exception {
//		_process = new ProcessBuilder("bash", "-c",
//				"echo -e \"(voice_"+_voice+") ;; \n(SayText \\\""+
//		_previewText+"\\\" )\" > .preview.scm").start();
//		
//	}
}
