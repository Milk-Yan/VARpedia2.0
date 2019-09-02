package application;

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
public class SearchTask extends Task<Void>{

	private String _term;
	private int _lineCount;
	private String _source;
	private int _lineNumber;
	private boolean _isInvalid = false;

	public SearchTask(String searchTerm) {
		_term = searchTerm;
	}

	@Override
	protected Void call() throws Exception {
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
				updateMessage(createMessage(process.getInputStream()));
			}

			
		} catch (IOException e) {
			new AlertMaker(AlertType.ERROR, "Error encountered", "I/O Exception", e.getStackTrace().toString());
		}
		
		return null;
	}
	
	/**
	 * @return the line count of the text of the searched term.
	 */
	protected int lineCount() {
		return _lineCount;
	}
	
	/**
	 * creates and numbers the inputstream into a string.
	 * @param inputStream the input stream of the process.
	 * @return the created string.
	 */
	private String createMessage(InputStream inputStream) {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
		_source = makeString(inputStream);
		iterator.setText(_source);
		_lineCount = 1;
		List<String> outputList = new ArrayList<String>();
		
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			
			if (start == 0) {
				outputList.add(Integer.toString(_lineCount++) + ". " + _source.substring(2,end) + "\n");
			} else {
				outputList.add(Integer.toString(_lineCount++) + ". " + _source.substring(start,end) + "\n");
			}	
		}
		_lineCount--;
		
		if (outputList.get(0).contains(":^(")) {
			_isInvalid = true;
			cancel();
		}

		return String.join("", outputList);
	}
	
	/**
	 * @return if the term searched is invalid
	 */
	protected boolean isInvalid() {
		return _isInvalid;
	}
	
	/**
	 * @return Only the number of sentences of the text that is chosen by the user.
	 */
	protected String getChosenText() {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);
		iterator.setText(_source);
		List<String> outputList = new ArrayList<String>();
		int start = iterator.first();
		int count = 0;
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			
			count++;
			if (start == 0) {
				outputList.add(_source.substring(2,end) + "\n");
			} else {
				outputList.add(_source.substring(start,end) + "\n");
			}
			
			if (count == _lineNumber) {
				break;
			}
			
		}
		return String.join("", outputList).replace("\"", "\\\"");
	}
	
	/**
	 * @param lineNumber sets number of lines of the chosen text.
	 */
	protected void setLineNumber(int lineNumber) {
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
