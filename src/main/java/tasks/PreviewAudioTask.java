package main.java.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;
import main.java.application.AlertMaker;

public class PreviewAudioTask extends Task<Void>{

	private String _voice;
	private String _previewText;
	Process _process;

	@Override
	protected Void call() throws Exception {


		_process = new ProcessBuilder("bash", "-c",
				"echo -e \"(voice_"+_voice+") ;; \\n(SayText \\\""+
						_previewText+"\\\")\" | festival -i ").start();

		if (_process.exitValue() != 0) {
			Platform.runLater(() -> {
				new AlertMaker(AlertType.ERROR, "Error", "Process failed", "Could not preview.");
			});
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
