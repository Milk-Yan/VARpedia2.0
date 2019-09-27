package main.java.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

	public String readFromFile(File file, String keyType) {
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String line;
			while ( (line = br.readLine()) != null ) {
				if (line.trim().startsWith(keyType)) {
					br.close();
					return line.substring(line.indexOf("=")+1).trim();
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Couldn't find " + keyType +" in config file "+file.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return null;
		
	}
}
