package main.java.tasks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import main.java.application.Folders;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Gets the current Wikipedia terms that have an audio creation.
 *
 * @author Milk, OverCry
 */
public class ViewAudioTermsTask extends Task<ObservableList<String>> {

    /**
     * Invoked when the Task is executed. Performs the background thread logic to get the list of
     * current terms that have audio creations.
     * @return The list of terms that have audio creations.
     */
    @Override
    protected ObservableList<String> call() {
        File folder = Folders.AUDIO_PRACTICE_FOLDER.getFile();

        File[] arrayOfFolders = folder.listFiles((file) -> true);

        List<File> listOfFolders = Arrays.asList(arrayOfFolders);
        Collections.sort(listOfFolders);

        ObservableList<String> outputList = FXCollections.observableArrayList();
        int lineNumber = 1;

        // only gives the name of the file, and gives a number as indication of the number of
        // current creations.
        for (File file : arrayOfFolders) {
            String name = file.getName().replace(".mp4", "");
            outputList.add(lineNumber + ". " + name + "\n");
            lineNumber++;
        }

        return outputList;

    }


}
