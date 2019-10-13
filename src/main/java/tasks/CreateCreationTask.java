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
                              ArrayList<String> imageList, Main mainApp) {
        _name = name;
        _term = term;
        _audioList = audioList;
        _imageList = imageList;
        _mainApp = mainApp;

    }

    @Override
    protected Void call() throws Exception {

        // create audio for practice
        String audioInputPracticeFolder = Folders.AudioPracticeFolder.getPath() + File.separator + _term;
        String audioOutputPracticeFolder =
                Folders.TempAudioPracticeFolder.getPath() + File.separator + _term;
        audioMerge(audioInputPracticeFolder, audioOutputPracticeFolder, _audioMergePracticeProcess);

        // create audio for quiz
        String audioInputTestFolder = Folders.AudioTestFolder.getPath() + File.separator + _term;
        String audioOutputTestFolder =
                Folders.TempAudioTestFolder.getPath() + File.separator + _term;
        audioMerge(audioInputTestFolder, audioOutputTestFolder, _audioMergeTestProcess);

        // merge image for practice
        String imageInputPracticeFolder =
                Folders.TempImagesFolder.getPath() + File.separator + _term;
        String videoOutputPracticeFolder =
                Folders.TempVideoPracticeFolder.getPath() + File.separator + _term;
        imageMerge(imageInputPracticeFolder, videoOutputPracticeFolder,
                audioOutputPracticeFolder, _imageMergePracticeProcess, false);

        // merge image for quiz
        String imageInputTestFolder = Folders.TempImagesFolder.getPath() + File.separator + _term;
        String videoOutputTestFolder =
                Folders.TempVideoTestFolder.getPath() + File.separator + _term;
        imageMerge(imageInputTestFolder, videoOutputTestFolder, audioOutputTestFolder,
                _imageMergeTestProcess, true);


        // merge overall for practice
        String creationOutputPracticeFolder =
                Folders.CreationPracticeFolder.getPath() + File.separator + _term;
        mergeOverall(audioOutputPracticeFolder, videoOutputPracticeFolder,
                creationOutputPracticeFolder, _mergeOverallPracticeProcess);

        // merge overall for quiz
        String creationOutputTestFolder =
                Folders.CreationTestFolder.getPath() + File.separator + _term;
        mergeOverall(audioOutputTestFolder, videoOutputTestFolder, creationOutputTestFolder, _mergeOverallTestProcess);

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
                        // create slideshow from images with same length as audio, images change
                        // every 2 seconds, 30 fps
                            "ffmpeg -framerate 1/2 -loop 1 -i " + imageInputFolder + File.separator +
                                "img%01d.jpg -r 30 -t $VIDEO_LENGTH -s 600x400" +
                        // put video file in temp folder
                        " -y " + videoOutputFolder + File.separator + _name + ".mp4").start();
            } else {
                imageMergeProcess = new ProcessBuilder("bash", "-c",
                        // get length of audio file
                        "VIDEO_LENGTH=$(soxi -D " + audioOutputFolder + File.separator +  _name +
                                ".wav);" +
                                // create slideshow from images with same length as audio, images
                                // change every 2 seconds, 30 fps
                                "ffmpeg -framerate 1/2 -loop 1 -i " + imageInputFolder + File.separator + "img" +
                                "%01d.jpg -r 30 -t $VIDEO_LENGTH " +
                                "-vf \"drawtext=fontfile=font.ttf:fontsize=200:fontcolor=white:"
                                + "x=(w-text_w)/2:y=(h-text_h):box=1:boxcolor=black@0.5:boxborderw=0" +
                                ".5:text=\"" + _term + " -s 600x400" +
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
                System.out.println(new StringManipulator().inputStreamToString(imageMergeProcess.getErrorStream()));
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The image did " +
                            "not merge properly");
                    _mainApp.displayMainMenuScene();
                });
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
            mergeOverallProcess = new ProcessBuilder("bash", "-c",
                    // get video
                    "ffmpeg -i " + videoInputFolder + File.separator + _name + ".mp4 " +
                            // get audio
                            "-i " + audioInputFolder + File.separator + _name + ".wav " +
                            // combine
                            "-strict -2 -y " + creationOutputFolder + File.separator + _name +
                            ".mp4"
            ).start();

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
                        new File(Folders.CreationScoreNotMasteredFolder.getPath() + File.separator + _term + ".txt");
                File scoreMastered =
                        new File(Folders.CreationScoreMasteredFolder.getPath() + File.separator + _term + ".txt");
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
            new File(Folders.CreationScoreNotMasteredFolder.getPath()).mkdirs();

            newFile.createNewFile();
            Files.write(Paths.get(newFile.getPath()), zero, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cancelled() {

        Process[] listOfProcesses = {_audioMergePracticeProcess, _audioMergeTestProcess,
                _imageMergePracticeProcess, _imageMergeTestProcess, _mergeOverallPracticeProcess,
                _mergeOverallTestProcess};
        for (Process process: listOfProcesses) {
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
                        new File(Folders.CreationPracticeFolder.getPath() + File.separator + _term + File.separator + _name + ".mp4");
                _mainApp.playVideo(creationFile);
            } else {
                _mainApp.displayMainMenuScene();
            }
        });
    }



}
