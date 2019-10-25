package main.java.tasks;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import main.java.application.Folders;

import java.io.File;

/**
 * Searches and returns the list of current creations.
 *
 * @author Milk, OverCry
 */
public class ViewCreationsTask extends Task<TreeItem<String>> {

    /**
     * Invoked when the Task is executed. Performs the background thread logic to get the list of
     * current video creations and add them as children to the root creation folder.
     * @return The root creation.
     */
    @Override
    protected TreeItem<String> call() {

        File folder =
                Folders.CREATION_PRACTICE_FOLDER.getFile();

        TreeItem<String> root = new TreeItem<>("Creations");
        root.setExpanded(true);

        // get all creations in the creation folder
        File[] creationFolders = folder.listFiles(File::isDirectory);

        for (File creationFolder: creationFolders) {
            TreeItem<String> term = new TreeItem<>(creationFolder.getName());
            root.getChildren().add(term);

            // get all mp4 files in the folder
            File[] creationFiles = creationFolder.listFiles((file -> file.getName().contains(".mp4")));

            for (File creationFile:creationFiles) {
                TreeItem<String> creation = new TreeItem<>(creationFile.getName());
                term.getChildren().add(creation);
            }
        }

        return root;


    }


}
