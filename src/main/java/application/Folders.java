package main.java.application;

import java.io.File;

public enum Folders {

    BinFolder(System.getProperty("user.dir") + File.separator + "bin"),
    CreationsFolder(BinFolder.getPath() + File.separator + "creations"),
    CreationPracticeFolder(CreationsFolder.getPath() + File.separator + "practice"),
    CreationTestFolder(CreationsFolder.getPath() + File.separator + "test"),
    CreationScoreFolder(CreationTestFolder.getPath() + File.separator + "score"),
    CreationScoreNotMasteredFolder(CreationScoreFolder.getPath() + File.separator + "not-mastered"),
    CreationScoreMasteredFolder(CreationScoreFolder.getPath() + File.separator + "mastered"),
    AudioFolder(BinFolder.getPath() + File.separator + "audio"),
    AudioPracticeFolder(AudioFolder.getPath() + File.separator + "practice"),
    AudioTestFolder(AudioFolder.getPath() + File.separator + "test"),
    TempFolder(BinFolder.getPath() + File.separator + ".temp"),
    TempAudioFolder(TempFolder.getPath() + File.separator + "audio"),
    TempAudioPracticeFolder(TempAudioFolder.getPath() + File.separator + "practice"),
    TempAudioTestFolder(TempAudioFolder.getPath() + File.separator + "test"),
    TempImagesFolder(TempFolder.getPath() + File.separator + "images"),
    TempVideoFolder(TempFolder.getPath() + File.separator + "video"),
    TempVideoPracticeFolder(TempVideoFolder.getPath() + File.separator + "practice"),
    TempVideoTestFolder(TempVideoFolder.getPath() + File.separator + "test");

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
