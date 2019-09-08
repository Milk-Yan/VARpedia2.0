package main.java.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;

/**
 * Searches for terms uses bash wikit on a new thread.
 * @author Milk
 *
 */
public class SearchTask extends Task<String>{

	private String _term;
	private int _lineCount;
	private String _sourceText;
	private int _lineNumber;
	private boolean _isInvalid = false;

	public SearchTask(String searchTerm) {
		_term = searchTerm;
	}

	@Override
	protected String call() throws Exception {
		try {
			Process process = new ProcessBuilder("bash", "-c", "wikit \"" + _term + "\"").start();
			try {
				process.waitFor();
			} catch (InterruptedException e) {
				// don't do anything
			}

			if (process.exitValue() != 0) {
				new AlertMaker(AlertType.ERROR, "Error encountered", "Problem finding term.", makeString(process.getErrorStream()));
			} else {
				String text = makeString(process.getInputStream());
				_sourceText = text;
				
				StringManipulator manipulator = new StringManipulator();
				String numberedText = manipulator.createNumberedText(text);
				
				return numberedText;
			}

			
		} catch (IOException e) {
			new AlertMaker(AlertType.ERROR, "Error encountered", "I/O Exception", e.getStackTrace().toString());
			WikiApplication.getInstance().displayMainMenu();
		}
		
		return null;
	}
	
	/**
	 * @return the line count of the text of the searched term.
	 */
	public int lineCount() {
		return _lineCount;
	}
	
	
	
	/**
	 * @return if the term searched is invalid
	 */
	public boolean isInvalid() {
		return _isInvalid;
	}
	
	

	public void updateText(String editted){
		_sourceText=editted;
	}

	/**
	 * @param lineNumber sets number of lines of the chosen text.
	 */
	public void setLineNumber(int lineNumber) {
		_lineNumber = lineNumber;
	}

	/**
	 * Makes streams depending on the input stream
	 * @param inputStream the input stream of the process.
	 * @return the created string.
	 */
	private String makeString(InputStream inputStream) {

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
		String result = bufferedReader.lines().collect(Collectors.joining("\n"));
		try {
			bufferedReader.close();
			//inputStreamReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}
}
