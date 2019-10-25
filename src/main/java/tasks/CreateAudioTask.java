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
 * @author Milk, OverCry
 */
public class CreateAudioTask extends Task<Void> {
    private String _voice;
    private String _name;
    private String _term;
    private String _practiceText;
    private Main _mainApp;
    private boolean _isPreview;
    private File _audioFolder;
    private Process _processPractice;
    private Process _processQuiz;
    private boolean _isValid = true;

    /**
     * Create an audio using the BASH Festival voice synthesiser.
     * @param audioFolder The folder the audio will be in.
     * @param term The Wikipedia term of the audio.
     * @param name The name of the audio.
     * @param text The text to synthesis in the audio.
     * @param mainApp The main of the application. Used to switch scenes.
     * @param voice The voice to synthesise the audio with.
     * @param isPreview If the audio is a preview (temporary) or a creation (permanent).
     */
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

    /**
     * Invoked when the Task is executed. Performs the background thread logic to create the audio.
     */
    @Override
    protected Void call() {

        _audioFolder.mkdirs();

        // make practice audio
        _processPractice = makeAudioProcess(_practiceText, _audioFolder);

        if (!_isPreview && _isValid) {
            // make the quiz text from the practice text, removing the key word.
            String quizText = new StringManipulator().getQuizText(_practiceText, _term);

            // make quiz audio
            File testFolder =
                    new File(Folders.AUDIO_TEST_FOLDER.getPath() + File.separator + _term);
            testFolder.mkdirs();

            _processQuiz = makeAudioProcess(quizText, testFolder);
        }

        return null;
    }

    /**
     * Makes the audio process to create the audio.
     * @param text The text to synthesise.
     * @param folder The folder to save the audio to.
     * @return The process made.
     */
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

            // if the current voice is unable to synthesise this text.
            if (new StringManipulator().inputStreamToString(process.getErrorStream()).contains(
                    "SIOD ERROR")) {
                cancel();
                // run the cancelled method immediately in this method before continuing
                cancelled();
                Platform.runLater(() -> {
                    new AlertFactory(AlertType.ERROR, "Error", "The voice " +
                            "synthesiser is unable to read this input.",
                            "Change voices, or do not include non-English words.");
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

    /**
     * This method is called whenever the Task transforms to the cancelled state. It destroys all
     * processes and deletes empty folders before finishing.
     */
    @Override
    public void cancelled() {
        super.cancelled();
        _isValid = false;
        destroyProcess();
        deleteEmptyFolder();

    }

    /**
     * Destroys the current process.
     */
    public void destroyProcess() {
        if (_processPractice != null) {
            _processPractice.destroy();
        }
        if (_processQuiz != null) {
            _processQuiz.destroy();
        }
    }


    /**
     * This method is called when the Task transforms to the succeeded state. It deletes empty
     * folders if there are any, and asks the user if they would like to create any more audio.
     */
    @Override
    public void succeeded() {
        deleteEmptyFolder();

        if (!_isPreview) {
            Platform.runLater(() -> {
                Alert alert = new AlertFactory(AlertType.CONFIRMATION, "Next",
                        "Would you like to make another audio?",
                        "Press 'OK'. Otherwise, press 'Cancel' to return to the main menu").getAlert();
                if (alert.getResult() == ButtonType.OK) {
                    _mainApp.displayPreviousAudioScene();
                } else {
                    _mainApp.displayMainMenuScene();
                }
            });
        }
    }

    /**
     * Deletes empty folders that may be created in the process.
     */
    private void deleteEmptyFolder() {
        if (_audioFolder != null && _audioFolder.exists() && Objects.requireNonNull(
                _audioFolder.listFiles()).length == 0) {
            _audioFolder.delete();
        }
    }


}
