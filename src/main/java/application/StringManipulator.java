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

/**
 * Assists with the creation and manipulation of strings.
 * @author Milk
 *
 */
public class StringManipulator {

	// breaks the string into sentences according to the English language.
	BreakIterator _iterator = BreakIterator.getSentenceInstance(Locale.ENGLISH);

	/**
	 * Separates the input string into lines and numbers the lines.
	 * @param inputStream the input string to process.
	 * @return the created numbered string.
	 */
	public String createNumberedText(String text) {
		// remove extra whitespaces at start
		if (text.length() > 1 && text.charAt(0) == text.charAt(1) && text.charAt(0) == 32) {
			text = text.substring(2);
		}

		_iterator.setText(text);
		int lineCount = 0;
		List<String> outputList = new ArrayList<String>();

		int start = _iterator.first();
		for (int end = _iterator.next(); end != BreakIterator.DONE; start = end, end = _iterator.next()) {
			outputList.add(Integer.toString(++lineCount) + ". " + text.substring(start,end) + "\n");
		}

		// join the list into one string
		String outputString = String.join("", outputList);

		// remove trailing newline
		outputString.substring(0, outputString.length()-2);

		return outputString;
	}

	/**
	 * Counts the number of lines of the input string and returns as int.
	 */
	public int countLines(String text) {

		return text.split("\r\n|\r|\n").length;
	}

	/**
	 * @return Only the sentences of the text that is chosen by the user.
	 */
	public String getChosenText(String text, int chosenNumber) {

		_iterator.setText(text);
		List<String> outputList = new ArrayList<String>();

		int start = _iterator.first();
		int count = 0;
		for (int end = _iterator.next(); end != BreakIterator.DONE; start = end, end = _iterator.next()) {

			count++;
			outputList.add(text.substring(start,end) + "\n");

			if (count == chosenNumber) {
				break;
			}

		}
		return String.join("", outputList).replace("\"", "\\\"");
	}

	public String removeNumberedLines(String text) {
		String newText = text.replaceAll("\\d+\\. ", "").replaceAll("\n", "");
		return newText;
	}
	
	public String inputStreamToString(InputStream inputStream) {
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
	
	public int countWords(String string) {
		String[] words = string.split("\\s+");
		return words.length;
	}
}
