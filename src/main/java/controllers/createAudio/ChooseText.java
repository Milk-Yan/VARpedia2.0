package main.java.controllers.createAudio;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import main.java.application.AlertFactory;
import main.java.application.Folders;
import main.java.application.StringManipulator;
import main.java.controllers.Controller;
import main.java.tasks.CreateAudioTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Controller for ChooseText.fxml. Displays Wikipedia search results.
 *
 * @author Milk, OverCry
 */
public class ChooseText extends Controller {

    private StringManipulator _manipulator = new StringManipulator();
    private String _term;
    private String _sourceString;

    //the actual name
    private ArrayList<String> _voices;

    //the displayed name
    private ArrayList<String> _voiceName;

    private MediaPlayer _audioPlayer;

    @FXML private Text _message;
    @FXML private TextArea _searchResults;
    @FXML private TextArea _chosenText;
    @FXML private Label _wordLimit;
    @FXML private ChoiceBox<String> _voiceSelection;
    @FXML private Button _previewBtn;
    @FXML private Button _createBtn;
    @FXML private Button _chooseBtn;
    @FXML private Button _returnBtn;

    /**
     * Initialises the searchResults TextArea and also the number of lines displayed to user.
     */
    public void setUp(String term, String searchResults) {
        _term = term;
        _sourceString = searchResults.trim();
        _wordLimit.setTextFill(Color.GREEN);

        _message.setText("Search results for " + _term + ": ");
        _searchResults.setText(_sourceString);
        setDisable(true);

        // generate list of different voices
        listOfVoices();
        _voiceSelection.setItems(FXCollections.observableArrayList(_voiceName));
        _voiceSelection.getSelectionModel().selectFirst();

    }

    /**
     * Converts highlighted text from the search to be used in the audio.
     */
    @FXML private void searchToChosen() {
        String highlightedText = _searchResults.getSelectedText();

        if (highlightedText.trim().isEmpty()) {
            _chooseBtn.setDisable(true);
        } else {
            _chooseBtn.setDisable(false);
            _chosenText.appendText(highlightedText);
            updateCount();
        }
    }

    /**
     * Passes chosen text to be made into an audio.
     * Checks if the text is valid for creation.
     */
    @FXML private void create() {

        stopCurrentPreview();

        //creates the audio
        int index = _voiceSelection.getSelectionModel().getSelectedIndex();
        // remove all special characters
        String chosenText = _chosenText.getText().replaceAll("[^0-9 a-z.A-Z]", "");

        // clear the chosen text so if user comes back...
        _chosenText.clear();

        _mainApp.displayCreateAudioNamingScene(_term, chosenText, _voices.get(index));

    }

    /**
     * Reset any edits of the Wikipedia search results.
     */
    @FXML private void reset() {
        _searchResults.setText(_sourceString);
    }

    /**
     * Plays the current chosen text using the chosen voice.
     */
    @FXML private void preview() {

        stopCurrentPreview();

        File tempAudioFolder = Folders.TEMP_AUDIO_FOLDER.getFile();

        String chosenText = _chosenText.getText().trim().replaceAll("[^0-9 a-z.A-Z]", "");
        int index = _voiceSelection.getSelectionModel().getSelectedIndex();

        CreateAudioTask task = new CreateAudioTask(tempAudioFolder, _term, "previewAudio",
                chosenText,
                _mainApp,_voices.get(index), true);

        new Thread(task).start();

        task.setOnSucceeded(succeededEvent -> {
            Media audio = new Media(Paths.get(tempAudioFolder + File.separator + "previewAudio" +
                    ".wav").toUri().toString());
            _audioPlayer = new MediaPlayer(audio);
            _audioPlayer.play();
        });

    }

    /**
     * Stops the current preview is there is any.
     */
    private void stopCurrentPreview() {
        if (_audioPlayer != null && _audioPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            _audioPlayer.stop();
        }
    }

    /**
     * Get the list of current Festival voices.
     */
    private void listOfVoices() {
        _voices = new ArrayList<>();
        _voiceName = new ArrayList<>();

        Process process;
        try {
            // get the list of voices from festival
            process = new ProcessBuilder("bash", "-c", "echo \"(voice.list)\" | festival -i"
                    + " | grep \"festival> (\" | cut -d \"(\" -f2 | cut -d \")\" -f1").start();

            InputStream stdout = process.getInputStream();
            StringManipulator manipulator = new StringManipulator();
            String lineOfVoices = manipulator.inputStreamToString(stdout);

            if (manipulator.countWords(lineOfVoices) > 0) {
                String[] voicesOrder = lineOfVoices.split(" ");

                for (String voice:voicesOrder) {
                    _voices.add(voice);
                    if (isRegisteredVoice(voice)) {
                        // if it is a registered voice, give it a nicer name
                        _voiceName.add(VoiceName.getName(voice));
                    } else {
                        _voiceName.add(voice);
                    }
                }

            } else {
                new AlertFactory(AlertType.ERROR, "Error", "No available voices", "Please install" +
                        " some festival voices");
            }

        } catch (IOException e) {
            new AlertFactory(AlertType.ERROR, "Error", "Cannot preview", "Could not preview with " +
                    "this voice.");
        }

    }

    /**
     * Checks if the voice is registered.
     * @param voice The voice to check.
     * @return If the voice is registered.
     */
    private boolean isRegisteredVoice(String voice) {
        for (VoiceName voiceName:VoiceName.values()) {
            // the voice provided is in the form of its system name, so that's what we have to
            // compare.
            if (voice.equals((voiceName.getSystemName()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Reset chosen text to empty string.
     */
    @FXML private void selectReset() {
        _chosenText.setText("");
        updateCount();
    }

    /**
     * Displays the amount of words in the chosen text section.
     * Indicates when the limit of words has been reached.
     */
    private void updateCount() {
        String content = _chosenText.getText().trim();
        int wordNumber = _manipulator.countWords(content);
        String wordCount = String.valueOf(wordNumber);

        if (content.equals("")) {
            _wordLimit.setTextFill(Color.DEEPPINK);
            _wordLimit.setText("Nothing here yet!");
            setDisable(true);
        } else if (wordNumber == 40) {
            _wordLimit.setTextFill(Color.DARKRED);
            _wordLimit.setText("At the limit!");
            setDisable(false);
        } else if (wordNumber > 40) {
            _wordLimit.setTextFill(Color.RED);
            _wordLimit.setText("Over the limit :( (" + wordCount + ")");
            setDisable(true);
        } else if (wordNumber > 30) {
            _wordLimit.setTextFill(Color.ORANGE);
            _wordLimit.setText("Near the limit (" + wordCount + ")");
            setDisable(false);
        } else if (wordNumber < 6) {
            _wordLimit.setTextFill(Color.RED);
            _wordLimit.setText("A bit short! (" + wordCount + ")");
            setDisable(true);
        } else {
            _wordLimit.setTextFill(Color.GREEN);
            _wordLimit.setText("You're Good! (" + wordCount + ")");
            setDisable(false);
        }
    }

    /**
     * Disable buttons.
     * @param disable if to disable.
     */
    private void setDisable(boolean disable) {
        if (disable) {
            _previewBtn.setDisable(true);
            _createBtn.setDisable(true);
        } else {
            _previewBtn.setDisable(false);
            _createBtn.setDisable(false);
        }
    }
    /**
     * Check the number of words in the chosen text
     */
    @FXML private void editCount() {
        removeSpacesOnly();
        updateCount();
        _returnBtn.setText("Quit");
    }

    /**
     * removes spaces if there are only spaces in the selected text
     */
    private void removeSpacesOnly(){
        if (_chosenText.getText().equals(" ")){
            _chosenText.setText("");
        }
    }

    /**
     * Functionality of the main menu button. Stops the current preview before going back to the
     * main menu.
     */
    @FXML private void mainMenuPress() {
        stopCurrentPreview();
        if (_returnBtn.getText().equals("Quit")){
            _returnBtn.setText("Are you sure?");
        } else if (!_returnBtn.getText().equals("Quit")) {
            mainMenu();
        }
    }

    /**
     * Enum for VoiceName. Has the customised names of all registered voices.
     */
    private enum VoiceName {
        ROBOTIC("kal_diphone"),
        NZ_MALE("akl_nz_jdt_diphone");

        private String _systemName;

        VoiceName(String systemName) {
            _systemName = systemName;
        }

        /**
         * @return The system name of the voice
         */
        public String getSystemName() {
            return _systemName;
        }

        /**
         * @return The customised name of the voice
         */
        public String getName() {
            return this.toString();
        }

        /**
         * Gives the customised name of the voice depending on the type of voice given.
         * @param voice The voice name given.
         * @return The customised name of the voice.
         */
        public static String getName(String voice) {
            for (VoiceName voiceName:values()) {
                if (voice.equals(voiceName.getSystemName())) {
                    return voiceName.name();
                }
            }

            // if there is no customised voice, just return the system voice.
            return voice;
        }
    }


}
