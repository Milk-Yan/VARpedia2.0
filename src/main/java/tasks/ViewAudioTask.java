package main.java.tasks;

import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;
import main.java.application.Folders;

import java.io.File;

public class ViewAudioTask extends Task<TreeItem<String>> {

    private TreeItem<String> _root;

    @Override
    protected TreeItem<String> call() throws Exception {
        File audioFolder =
                new File(Folders.AudioFolder.getPath());

        _root = new TreeItem<String>("Audio");
        _root.setExpanded(true);

        // gets all folders in the audio folder
        File[] audioFolders = audioFolder.listFiles((file) -> {
            if (file.isDirectory()) {
                return true;
            } else {
                return false;
            }
        });


        for (File audioTermFolder : audioFolders) {

            TreeItem<String> term = new TreeItem<String>(audioTermFolder.getName());
            _root.getChildren().add(term);

            // gets all .wav files in the folder
            File[] audioFiles = audioTermFolder.listFiles((file) -> {
                if (file.getName().contains(".wav")) {
                    return true;
                } else {
                    return false;
                }
            });

            for (File audioFile : audioFiles) {
                TreeItem<String> audio = new TreeItem<String>(audioFile.getName());
                term.getChildren().add(audio);
            }

        }


        return _root;
    }


}
