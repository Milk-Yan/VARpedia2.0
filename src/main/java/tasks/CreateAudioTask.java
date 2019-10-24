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
import java.util.Objects;

/**
 * Creates the audio for quiz and practice.
 *
 * @author Milk
 */
public class CreateAudioTask extends Task<Void> {
    private String _voice;
    private String _name;
    private String _term;
    private String _practiceText;
    private String _quizText;
    private Main _mainApp;
    private boolean _isPreview;
    private File _audioFolder;
    private Process _processPractice;
    private Process _processQuiz;
    private boolean _isValid = true;

    public CreateAudioTask(File audioFolder, String term, String name, String text, Main mainApp,
                           String voice, boolean isPreview) {
        _term = term;
        _name = name;
        _practiceText = text;
        _mainApp = mainApp;
        _voice = voice;
        _audioFolder = audioFolder;
        _isPreview = isPreview;

    }

    @Override
    protected Void call() {

        _audioFolder.mkdirs();

        // make practice audio
        _processPractice = makeAudioProcess(_practiceText, _audioFolder);

        if (!_isPreview && _isValid) {
            // make the quiz text from the practice text, removing the key word.
            _quizText = new StringManipulator().getQuizText(_practiceText, _term);

            // make quiz audio
            File testFolder =
                    new File(Folders.AUDIO_TEST_FOLDER.getPath() + File.separator + _term);
            testFolder.mkdirs();

            _processQuiz = makeAudioProcess(_quizText, testFolder);
        }

        return null;
    }

    private Process makeAudioProcess(String text, File folder) {
        Process process = null;
        try {
            process = new ProcessBuilder("bash", "-c",
                    // set voice
                    "echo \"(voice_" + _voice + ") "
                            // create utterance
                            + "(set! utt1 (Utterance Text \\\"" + text + "\\\")) "
                            // synthesise utterance
                            + "(utt.synth utt1) "
                            // save
                            + "(utt.save.wave utt1 \\\"" + folder.getPath() + File.separator + _name +
                            ".wav" + "\\\" \\`riff)\" | festival\n").start();
            try {
                process.waitFor();

            } catch (InterruptedException e) {
                cancel();
            }

            if (new StringManipulator().inputStreamToString(process.getErrorStream()).contains(
                    "SIOD ERROR")) {
                cancel();
                // run the cancelled method immediately in this method before continuing
                cancelled();
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error", "The voice " +
                            "synthesiser is unable to read this input.",
                            "Do not include non-English words.");
                });
            }

            if (process.exitValue() != 0) {
                cancel();
            }

        } catch (IOException e) {
            cancel();
        }

        return process;
    }

    @Override
    public void cancelled() {
        super.cancelled();
        _isValid = false;
        destroyProcess();
        deleteEmptyFolder();

    }

    /**
     * destroys the current process.
     */
    public void destroyProcess() {
        if (_processPractice != null) {
            _processPractice.destroy();
        }
        if (_processQuiz != null) {
            _processQuiz.destroy();
        }
    }


    @Override
    public void succeeded() {
        deleteEmptyFolder();

        if (!_isPreview) {
            Platform.runLater(() -> {
                Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Next",
                        "Would you like to make another audio?",
                        "Press 'OK'. Otherwise, press 'Cancel' to return to the main menu").getAlert();

                if (alert.getResult() == ButtonType.OK) {
                    //go to preview scene again
                    //NOT DONE YET
                    _mainApp.setAudioScene();
                } else {
                    _mainApp.displayMainMenuScene();
                }
            });
        }


    }

    private void deleteEmptyFolder() {
        if (_audioFolder != null && _audioFolder.exists() && Objects.requireNonNull(
                _audioFolder.listFiles()).length == 0) {
            _audioFolder.delete();
        }
    }


}
