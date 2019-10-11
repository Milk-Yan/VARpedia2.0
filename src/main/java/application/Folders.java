package main.java.application;

import java.io.File;

public enum Folders {

    BinFolder(System.getProperty("user.dir") + File.separator + "bin"),
    CreationsFolder(BinFolder.getPath() + File.separator + "creations"),
    CreationPracticeFolder(CreationsFolder.getPath() + File.separator + "practice"),
    CreationTestFolder(CreationsFolder.getPath() + File.separator + "test"),
    CreationScoreFolder(CreationTestFolder.getPath() + File.separator + "score"),
    AudioFolder(BinFolder.getPath() + File.separator + "audio"),
    AudioPracticeFolder(AudioFolder.getPath() + File.separator + "practice"),
    AudioTestFolder(AudioFolder.getPath() + File.separator + "test"),
    TempFolder(BinFolder.getPath() + File.separator + ".temp"),
    TempAudioFolder(TempFolder.getPath() + File.separator + "audio"),
    TempImagesFolder(TempFolder.getPath() + File.separator + "images"),
    TempVideoFolder(TempFolder.getPath() + File.separator + "video");

    private String _path;

    Folders(String path) {
        _path = path;
    }

    /**
     * @return Path of the folder.
     */
    public String getPath() {
        // string is immutable so ok to send like this
        return _path;
    }
}
