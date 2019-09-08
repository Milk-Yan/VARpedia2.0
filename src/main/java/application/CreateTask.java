package main.java.application;

import java.io.IOException;

import javafx.concurrent.Task;

/**
 * Creates the video creation.
 * @author Milk
 *
 */
public class CreateTask extends Task<Void>{

	String _name;
	String _term;
	String _text;
	int _lineNumber;
	Process _process;

	public CreateTask(String name,String term, String text) {
		_term = term;
		_name = name;
		_text = text;
	}

	@Override
	protected Void call() {

		try {
			_process = new ProcessBuilder("/bin/bash", "-c", 
					// create audio (festival) file
					  "echo \""+_text+"\" | text2wave -o ./bin/audio/\"" + _name + "\".wav;"
					// get length of audio file
					+ "VIDEO_LENGTH=$(soxi -D ./bin/audio/\"" + _name + "\".wav);" 
					// create video with term and no sound, same duration as audio
					+ "ffmpeg -f lavfi -i color=c=black:s=500x300:d=$VIDEO_LENGTH -vf "
					+ "\"drawtext=fontfile=Midnight Bangkok.ttf:fontsize=100: "
					+ "fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text=\"" + _term + "\"\" "
					+ "-y \"./bin/videos/\"" + _name + "\".mp4\" &>/dev/null;"
					// combine audio and video
					+ "ffmpeg -i ./bin/videos/\"" + _name + "\".mp4 -i ./bin/audio/\"" + _name + "\".wav"
					+ " -strict -2 -y ./bin/creations/\"" + _name + "\".mp4 &>/dev/null").start();
			
			try {
				_process.waitFor();
				
			} catch (InterruptedException e) {
				// don't do anything
			}
			
			if (_process.exitValue() != 0) {
				this.cancel();
				_process.destroy();
			}
			
		} catch (IOException e) {
			this.cancel();
			_process.destroy();
		}
		
		
		return null;
	}
	
	/**
	 * destroys the current process.
	 */
	public void destroy() {
		_process.destroy();
	}


}
