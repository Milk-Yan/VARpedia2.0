package main.java.application;

import java.io.File;

public enum Folders {

    BIN_FOLDER(System.getProperty("user.dir") + File.separator + "bin"),
    CREATIONS_FOLDER(BIN_FOLDER.getPath() + File.separator + "creations"),
    CREATION_PRACTICE_FOLDER(CREATIONS_FOLDER.getPath() + File.separator + "practice"),
    CREATION_TEST_FOLDER(CREATIONS_FOLDER.getPath() + File.separator + "test"),
    CREATION_SCORE_FOLDER(CREATION_TEST_FOLDER.getPath() + File.separator + "score"),
    CREATION_SCORE_NOT_MASTERED_FOLDER(CREATION_SCORE_FOLDER.getPath() + File.separator + "not-mastered"),
    CREATION_SCORE_MASTERED_FOLDER(CREATION_SCORE_FOLDER.getPath() + File.separator + "mastered"),
    AUDIO_FOLDER(BIN_FOLDER.getPath() + File.separator + "audio"),
    AUDIO_PRACTICE_FOLDER(AUDIO_FOLDER.getPath() + File.separator + "practice"),
    AUDIO_TEST_FOLDER(AUDIO_FOLDER.getPath() + File.separator + "test"),
    TEMP_FOLDER(BIN_FOLDER.getPath() + File.separator + ".temp"),
    TEMP_AUDIO_FOLDER(TEMP_FOLDER.getPath() + File.separator + "audio"),
    TEMP_AUDIO_PRACTICE_FOLDER(TEMP_AUDIO_FOLDER.getPath() + File.separator + "practice"),
    TEMP_AUDIO_TEST_FOLDER(TEMP_AUDIO_FOLDER.getPath() + File.separator + "test"),
    TEMP_IMAGES_FOLDER(TEMP_FOLDER.getPath() + File.separator + "images"),
    TEMP_VIDEO_FOLDER(TEMP_FOLDER.getPath() + File.separator + "video"),
    TEMP_VIDEO_PRACTICE_FOLDER(TEMP_VIDEO_FOLDER.getPath() + File.separator + "practice"),
    TEMP_VIDEO_TEST_FOLDER(TEMP_VIDEO_FOLDER.getPath() + File.separator + "test"),
    MUSIC_FOLDER(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + "music");

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

    public File getFile() {
        return new File(this.getPath());
    }
}
