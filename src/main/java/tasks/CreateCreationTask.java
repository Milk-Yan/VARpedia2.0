package main.java.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.application.Main;

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

    private ArrayList<Process> _listOfProcesses = new ArrayList<>();
    private ArrayList<File> _listOfOutputFolders = new ArrayList<>();

    public CreateCreationTask(String name, String term, ArrayList<String> audioList,
                              ArrayList<String> imageList, String musicSelection, Main mainApp) {
        _name = name;
        _term = term;
        _audioList = audioList;
        _imageList = imageList;
        _mainApp = mainApp;
        _musicSelection = musicSelection;

        _listOfOutputFolders.add(Folders.CREATION_PRACTICE_FOLDER.getFile());
        _listOfOutputFolders.add(Folders.CREATION_TEST_FOLDER.getFile());
        _listOfOutputFolders.add(Folders.CREATION_SCORE_NOT_MASTERED_FOLDER.getFile());
    }

    @Override
    protected Void call() throws Exception {

        // create audio for practice
        String audioInputPracticeFolder =
                Folders.AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term;
        String audioOutputPracticeFolder =
                Folders.TEMP_AUDIO_PRACTICE_FOLDER.getPath() + File.separator + _term;
        audioMerge(audioInputPracticeFolder, audioOutputPracticeFolder);

        // create audio for quiz
        String audioInputTestFolder = Folders.AUDIO_TEST_FOLDER.getPath() + File.separator + _term;
        String audioOutputTestFolder =
                Folders.TEMP_AUDIO_TEST_FOLDER.getPath() + File.separator + _term;
        audioMerge(audioInputTestFolder, audioOutputTestFolder);

        // merge image for practice
        String imageInputPracticeFolder =
                Folders.TEMP_IMAGES_FOLDER.getPath() + File.separator + _term;
        String videoOutputPracticeFolder =
                Folders.TEMP_VIDEO_PRACTICE_FOLDER.getPath() + File.separator + _term;
        imageMerge(imageInputPracticeFolder, videoOutputPracticeFolder,
                audioOutputPracticeFolder, false);

        // merge image for quiz
        String imageInputTestFolder = Folders.TEMP_IMAGES_FOLDER.getPath() + File.separator + _term;
        String videoOutputTestFolder =
                Folders.TEMP_VIDEO_TEST_FOLDER.getPath() + File.separator + _term;
        imageMerge(imageInputTestFolder, videoOutputTestFolder, audioOutputTestFolder, true);


        // merge overall for practice
        String creationOutputPracticeFolder =
                Folders.CREATION_PRACTICE_FOLDER.getPath() + File.separator + _term;
        mergeOverall(audioOutputPracticeFolder, videoOutputPracticeFolder,
                creationOutputPracticeFolder);

        // merge overall for quiz
        String creationOutputTestFolder =
                Folders.CREATION_TEST_FOLDER.getPath() + File.separator + _term;
        mergeOverall(audioOutputTestFolder, videoOutputTestFolder, creationOutputTestFolder);

        return null;
    }

    private void audioMerge(String audioInputFolder, String audioOutputFolder) {

        File tempFolder =
                new File(audioOutputFolder);
        tempFolder.mkdirs();

        // create string of all audio files
        String audioFiles = "";

        for (String fileName : _audioList) {
            audioFiles = audioFiles.concat(audioInputFolder + File.separator + fileName + ".wav ");
        }

        try {
            Process audioMergeProcess = new ProcessBuilder("bash", "-c",
                    "sox " + audioFiles + tempFolder + File.separator + _name + ".wav"
            ).start();

            _listOfProcesses.add(audioMergeProcess);

            try {
                audioMergeProcess.waitFor();

            } catch (InterruptedException e) {
                cancel();
            }

            if (audioMergeProcess.exitValue() != 0) {
                cancel();
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The audio did not " +
                            "merge properly");
                    _mainApp.displayMainMenuScene();
                });
            }

        } catch (IOException e) {
            cancel();
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error", "I/O Exception", "Audio merge process " +
                        "exception.");
                _mainApp.displayMainMenuScene();
            });
        }


    }

    private void imageMerge(String imageInputFolder, String videoOutputFolder,
                            String audioOutputFolder, boolean isTest) {

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
            Process imageMergeProcess;
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

            _listOfProcesses.add(imageMergeProcess);

            try {
                imageMergeProcess.waitFor();

            } catch (InterruptedException e) {
                cancel();
            }

            if (imageMergeProcess.exitValue() != 0) {
                cancel();
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error", "Process failed", "The image did " +
                            "not merge properly");
                    _mainApp.displayMainMenuScene();
                });
            }

        } catch (IOException e) {
            cancel();
            Platform.runLater(() -> {
                new AlertFactory(AlertType.ERROR, "Error", "I/O Exception", "Image merge process " +
                        "exception.");
                _mainApp.displayMainMenuScene();
            });
        }
    }

    private void mergeOverall(String audioInputFolder, String videoInputFolder,
                              String creationOutputFolder) {

        File creationFolder = new File(creationOutputFolder);
        creationFolder.mkdirs();

        try {
            Process mergeOverallProcess;
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

            _listOfProcesses.add(mergeOverallProcess);


            try {
                mergeOverallProcess.waitFor();

            } catch (InterruptedException e) {
                cancel();
            }


            if (mergeOverallProcess.exitValue() != 0) {
                cancel();
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
            cancel();
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

        for (Process process : _listOfProcesses) {
            if (process != null && process.isAlive()) {
                process.destroy();
                // remove the process from the list of current processes
                _listOfProcesses.remove(process);
            }
        }

        // delete folder if creation didn't go properly
        for (File folder:_listOfOutputFolders) {
            if (folder.exists() && folder.listFiles().length == 0) {
                folder.delete();
            }
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
