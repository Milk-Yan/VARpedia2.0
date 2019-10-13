package main.java.application;

import java.io.File;

/**
 * Constant type names and addresses so easy to change.
 *
 * @author Milk
 */

public enum SceneType {

    MainMenu("/main/resources/fxml/MainMenu.fxml"),
    CreateAudioSearch("/main/resources/fxml/createAudio/TermSearch.fxml"),
    CreateAudioChooseText("/main/resources/fxml/createAudio/ChooseText.fxml"),
    CreateAudioNaming("/main/resources/fxml/createAudio/AudioNaming.fxml"),
    CreateCreationSearch("/main/resources/fxml/createCreation/AudioSearch.fxml"),
    CreateCreationChooseAudio("/main/resources/fxml/createCreation/ChooseAudio.fxml"),
    CreateCreationNaming("/main/resources/fxml/createCreation/CreationNaming.fxml"),
    ViewCreations("/main/resources/fxml/view/ViewCreations.fxml"),
    VideoPlayer("/main/resources/fxml/view/VideoPlayer.fxml"),
    Quiz("/main/resources/fxml/quiz/Quiz.fxml"),
    CreateCreationChooseImages("/main/resources/fxml/createCreation/ChooseImages.fxml");

    private String _address;

    SceneType(String address) {
        this._address = address.replaceAll("/", File.separator);
    }

    /**
     * @return Address of the fxml for the SceneType.
     */
    public String getAddress() {
        // string is immutable so ok to send like this
        return _address;
    }

}
