package main.java.application;

import java.io.File;

/**
 * The SceneType enum is used to specify the addresses of the FXML files for the scene.
 *
 * @author Milk, OverCry
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
     * @return Address of the FXML for the SceneType.
     */
    public String getAddress() {
        return _address;
    }

}
