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
 * Searches and returns the list of current creations.
 *
 * @author Milk
 */
public class ViewCreationsTask extends Task<ObservableList<String>> {

    @Override
    protected ObservableList<String> call() {

        File folder =
                new File(Folders.CreationsFolder.getPath());

        File[] arrayOfCreations = folder.listFiles((file) -> {
            if (file.getName().contains(".mp4")) {
                return true;
            } else {
                return false;
            }
        });

        // sorts the list of creations in alphabetical order.
        List<File> listOfCreations = Arrays.asList(arrayOfCreations);
        Collections.sort(listOfCreations);

        ObservableList<String> outputList = FXCollections.observableArrayList();
        int lineNumber = 1;

        // only gives the name of the file, and gives a number as
        // indication of the number of current creations.
        for (File file : listOfCreations) {
            String name = file.getName().replace(".mp4", "");
            outputList.add(lineNumber + ". " + name + "\n");
            lineNumber++;
        }

        return outputList;
    }


}
