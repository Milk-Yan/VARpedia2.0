package main.java.tasks;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import main.java.application.Folders;

import java.io.File;
import java.util.Objects;

/**
 * Gets the list of current audio creations.
 *
 * @author Milk, OverCry
 */
public class ViewAudioTask extends Task<TreeItem<String>> {

    /**
     * Invoked when the Task is executed. Performs the background thread logic to get the list of
     * current audio and add them as children to the root audio folder.
     *
     * @return The root audio.
     */
    @Override
    protected TreeItem<String> call() {
        File audioFolder = Folders.AUDIO_PRACTICE_FOLDER.getFile();

        TreeItem<String> root = new TreeItem<>("Audio");
        root.setExpanded(true);

        // gets all folders in the audio folder
        File[] audioFolders = audioFolder.listFiles(File::isDirectory);

        for (File audioTermFolder : Objects.requireNonNull(audioFolders)) {

            TreeItem<String> term = new TreeItem<>(audioTermFolder.getName());
            root.getChildren().add(term);

            // gets all .wav files in the folder
            File[] audioFiles = audioTermFolder.listFiles((file) -> file.getName().contains(".wav"));

            for (File audioFile : Objects.requireNonNull(audioFiles)) {
                TreeItem<String> audio = new TreeItem<>(audioFile.getName());
                term.getChildren().add(audio);
            }

        }

        return root;
    }


}
