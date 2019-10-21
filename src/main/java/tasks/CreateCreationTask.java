package main.java.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.application.Main;
import main.java.application.StringManipulator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCreationTask extends Task<Void> {

    private String _name;
    private String _term;
    private ArrayList<String> _audioList;
    private ArrayList<String> _imageList;
    private String _musicSelection;
    private Main _mainApp;

    private Process _audioMergePracticeProcess;
    private Process _audioMergeTestProcess;
    private Process _imageMergePracticeProcess;
    private Process _imageMergeTestProcess;
    private Process _mergeOverallPracticeProcess;
    private Process _mergeOverallTestProcess;

    private File _creationOutputFolder;
    private File _creationScoreFolder;

    public CreateCreationTask(String name, String term, ArrayList<String> audioList,
                              ArrayList<String> imageList, String musicSelection, Main mainApp) {
        _name = name;
        _term = term;
        _audioList = audioList;
        _imageList = imageList;
        _mainApp = mainApp;
        _musicSelection = musicSelection;

    }

    @Override
    protected Void call() throws Exception {

        // create audio for practice
        String audioInputPracticeFolder =
                Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term;
        String audioOutputPracticeFolder =
                Folders.TEMP_AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term;
        audioMerge(audioInputPracticeFolder, audioOutputPracticeFolder, _audioMergePracticeProcess);

        // create audio for quiz
        String audioInputTestFolder = Folders.AUDIO_TEST_FOLDER.getPath() + File.separator + _term;
        String audioOutputTestFolder =
                Folders.TEMP_AUDIO_TEST_FOLDER.getPath() + File.separator + _term;
        audioMerge(audioInputTestFolder, audioOutputTestFolder, _audioMergeTestProcess);

        // merge image for practice
        String imageInputPracticeFolder =
                Folders.TEMP_IMAGES_FOLDER.getPath() + File.separator + _term;
        String videoOutputPracticeFolder =
                Folders.TEMP_VIDEO_PRACTICE_FOLDER.getPath() + File.separator + _term;
        imageMerge(imageInputPracticeFolder, videoOutputPracticeFolder,
                audioOutputPracticeFolder, _imageMergePracticeProcess, false);

        // merge image for quiz
        String imageInputTestFolder = Folders.TEMP_IMAGES_FOLDER.getPath() + File.separator + _term;
        String videoOutputTestFolder =
                Folders.TEMP_VIDEO_TEST_FOLDER.getPath() + File.separator + _term;
        imageMerge(imageInputTestFolder, videoOutputTestFolder, audioOutputTestFolder,
                _imageMergeTestProcess, true);


        // merge overall for practice
        String creationOutputPracticeFolder =
                Folders.CREATION_PRACTICE_FOLDER.getPath() + File.separator + _term;
        mergeOverall(audioOutputPracticeFolder, videoOutputPracticeFolder,
                creationOutputPracticeFolder, _mergeOverallPracticeProcess);

        // merge overall for quiz
        String creationOutputTestFolder =
                Folders.CREATION_TEST_FOLDER.getPath() + File.separator + _term;
        mergeOverall(audioOutputTestFolder, videoOutputTestFolder, creationOutputTestFolder,
                _mergeOverallTestProcess);

        return null;
    }

    private void audioMerge(String audioInputFolder, String audioOutputFolder,
                            Process audioMergeProcess) {

        File tempFolder =
                new File(audioOutputFolder);
        tempFolder.mkdirs();

        // create string of all audio files
        String audioFiles = "";

        for (String fileName : _audioList) {
            audioFiles = audioFiles.concat(audioInputFolder + File.separator + fileName + ".wav ");
        }

        try {
            audioMergeProcess = new ProcessBuilder("bash", "-c",
                    "sox " + audioFiles + tempFolder + File.separator + _name + ".wav"
            ).start();

            try {
                audioMergeProcess.waitFor();

            } catch (InterruptedException e) {
                // don't do anything
            }

        } catch (IOException e) {
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error", "I/O Exception", "Audio merge process " +
                        "exception.");
                _mainApp.displayMainMenuScene();
            });
        }

        if (audioMergeProcess.exitValue() != 0) {
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The audio did not " +
                        "merge properly");
                _mainApp.displayMainMenuScene();
            });
        }
    }

    private void imageMerge(String imageInputFolder, String videoOutputFolder,
                            String audioOutputFolder, Process imageMergeProcess, boolean isTest) {

        File tempFolder = new File(videoOutputFolder);
        tempFolder.mkdirs();

        int i = 0;
        for (String imageName : _imageList) {
            File image = new File(imageInputFolder + File.separator + imageName);
            File newImageName = new File(imageInputFolder + File.separator + "img" + i + ".jpg");
            image.renameTo(newImageName);

            i++;
        }

        try {
            if (isTest) {
                imageMergeProcess = new ProcessBuilder("bash", "-c",
                        "VIDEO_LENGTH=$(soxi -D " + audioOutputFolder + File.separator + _name +
                                ".wav);" +
                                // create slideshow from images with same length as audio, images
                                // change
                                // every 2 seconds, 30 fps
                                "ffmpeg -framerate 1/2 -loop 1 -i " + imageInputFolder +
                                File.separator +
                                "img%01d.jpg -r 30 -t $VIDEO_LENGTH  -vf " +
                                "\"scale=w=600:h=400:force_original_aspect_ratio=1,pad=600:400:" +
                                "(ow-iw)/2:(oh-ih)/2\" -s 600x400" +
                                // put video file in temp folder
                                " -y " + videoOutputFolder + File.separator + _name +
                                ".mp4").start();
            } else {
                imageMergeProcess = new ProcessBuilder("bash", "-c",
                        // get length of audio file
                        "VIDEO_LENGTH=$(soxi -D " + audioOutputFolder + File.separator + _name +
                                ".wav);" +
                                // create slideshow from images with same length as audio, images
                                // change every 2 seconds, 30 fps
                                "ffmpeg -framerate 1/2 -loop 1 -i " + imageInputFolder +
                                File.separator + "img" +
                                "%01d.jpg -r 30 -t $VIDEO_LENGTH " +
                                "-vf \"scale=w=600:h=400:force_original_aspect_ratio=1," +
                                "pad=600:400:(ow-iw)/2:(oh-ih)/2, drawtext=fontfile=font" +
                                ".ttf:fontsize=100:fontcolor=white:"
                                +
                                "x=(w-text_w)/2:y=(h-text_h-20):text=" + _term + ", drawbox=y" +
                                "=ih-h:color=black@0.5:width=iw:height=100:t=fill\"" +
                                // put video file in temp folder
                                " -y " + videoOutputFolder + File.separator + _name +
                                ".mp4"
                ).start();
            }

            try {
                imageMergeProcess.waitFor();

            } catch (InterruptedException e) {
                // don't do anything
            }

            if (imageMergeProcess.exitValue() != 0) {
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The image did " +
                            "not merge properly");
                    _mainApp.displayMainMenuScene();
                });
                System.out.println(new StringManipulator().inputStreamToString(imageMergeProcess.getErrorStream()));
            }

        } catch (IOException e) {
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error", "I/O Exception", "Image merge process " +
                        "exception.");
                _mainApp.displayMainMenuScene();
            });
        }
    }

    private void mergeOverall(String audioInputFolder, String videoInputFolder,
                              String creationOutputFolder, Process mergeOverallProcess) {

        File creationFolder = new File(creationOutputFolder);
        creationFolder.mkdirs();

        try {
            if (_musicSelection == null) {
                mergeOverallProcess = new ProcessBuilder("bash", "-c",
                        // get video
                        "ffmpeg -i " + videoInputFolder + File.separator + _name + ".mp4 " +
                                // get audio
                                "-i " + audioInputFolder + File.separator + _name + ".wav " +
                                // combine
                                "-strict -2 -y " + creationOutputFolder + File.separator + _name +
                                ".mp4"
                ).start();
            } else {
                String bgm =
                        (Folders.MUSIC_FOLDER.getPath() + File.separator + _musicSelection);
                mergeOverallProcess = new ProcessBuilder("bash", "-c",
                        // get video
                        "ffmpeg -i " + videoInputFolder + File.separator + _name + ".mp4 " +
                                // get audio
                                "-i " + audioInputFolder + File.separator + _name + ".wav " +
                                // get background music
                                "-stream_loop -1 -i \"" + bgm + "\" -filter_complex amerge -ac 1 " +
                                // combine
                                "-shortest -strict -2 -y " + creationOutputFolder + File.separator + _name +
                                ".mp4"
                ).start();
            }


            try {
                mergeOverallProcess.waitFor();

            } catch (InterruptedException e) {
                // don't do anything
            }


            if (mergeOverallProcess.exitValue() != 0) {
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The video and " +
                            "image did not merge properly");
                    _mainApp.displayMainMenuScene();
                });
            } else {
                // create a score file for the term if it doesn't exist already
                File scoreNotMastered =
                        new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath() + File.separator +
                                _term + ".txt");
                File scoreMastered =
                        new File(Folders.CREATION_SCORE_MASTERED_FOLDER.getPath() + File.separator +
                                _term + ".txt");
                if (!scoreNotMastered.exists() && !scoreMastered.exists()) {
                    createNewScoreFile(scoreNotMastered);
                }
            }

        } catch (IOException e) {
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error", "I/O Exception",
                        "Overall merge process exception.");
                _mainApp.displayMainMenuScene();
            });
        }


    }

    private void createNewScoreFile(File newFile) {
        List<String> zero = Arrays.asList("0");
        try {
            new File(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getPath()).mkdirs();

            newFile.createNewFile();
            Files.write(Paths.get(newFile.getPath()), zero, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelled() {
        super.cancelled();

        Process[] listOfProcesses = {_audioMergePracticeProcess, _audioMergeTestProcess,
                _imageMergePracticeProcess, _imageMergeTestProcess, _mergeOverallPracticeProcess,
                _mergeOverallTestProcess};
        for (Process process : listOfProcesses) {
            if (process != null && process.isAlive()) {
                process.destroy();
            }
        }

        // delete folder if creation didn't go properly
        if (_creationOutputFolder.exists() && _creationOutputFolder.listFiles().length == 0) {
            _creationOutputFolder.delete();
        }
    }

    @Override
    public void succeeded() {

        Platform.runLater(() -> {
//			new AlertFactory(AlertType.INFORMATION, "Complete", "Creation complete", "Let's go
//			back to the main menu!");
//			_mainApp.displayMainMenuScene();
            Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Next",
                    "Would you like to play your creation?",
                    "Press 'OK'. Otherwise, press 'Cancel'").getAlert();
            if (alert.getResult() == ButtonType.OK) {
                //go to preview scene again
                //NOT DONE YET
                _mainApp.displayMainMenuScene();
                File creationFile =
                        new File(Folders.CREATION_PRACTICE_FOLDER.getPath() + File.separator + _term +
                                File.separator + _name + ".mp4");
                _mainApp.playVideo(creationFile);
            } else {
                _mainApp.displayMainMenuScene();
            }
        });
    }


}
