package main.java.tasks;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import main.java.application.Folders;

import java.io.File;

/**
 * Searches and returns the list of current creations.
 *
 * @author Milk
 */
public class ViewCreationsTask extends Task<TreeItem<String>> {

    private TreeItem<String> _root;

    @Override
    protected TreeItem<String> call() {

        File folder =
                new File(Folders.CreationPracticeFolder.getPath());

        _root = new TreeItem<String>("Creations");
        _root.setExpanded(true);

        // get all creations in the creation folder
        File[] creationFolders = folder.listFiles((file) -> {
            if (file.isDirectory()) {
                return true;
            } else {
                return false;
            }
        });

        for (File creationFolder: creationFolders) {
            TreeItem<String> term = new TreeItem<>(creationFolder.getName());
            _root.getChildren().add(term);

            // get all mp4 files in the folder
            File[] creationFiles = creationFolder.listFiles((file -> {
                if (file.getName().contains(".mp4")) {
                    return true;
                } else {
                    return false;
                }
            }));

            for (File creationFile:creationFiles) {
                TreeItem<String> creation = new TreeItem<>(creationFile.getName());
                term.getChildren().add(creation);
            }
        }

        return _root;


    }


}
