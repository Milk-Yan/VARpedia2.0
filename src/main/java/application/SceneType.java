package main.java.application;

import java.io.File;

/**
 * Constant type names and addresses so easy to change.
 *
 * @author Milk
 */

public enum SceneType {

    MainMenu("/main/resources/application/MainMenu.fxml"),
    CreateAudioSearch("/main/resources/application/createAudio/TermSearch.fxml"),
    CreateAudioChooseText("/main/resources/application/createAudio/ChooseText.fxml"),
    CreateAudioNaming("/main/resources/application/createAudio/AudioNaming.fxml"),
    CreateCreationSearch("/main/resources/application/createCreation/AudioSearch.fxml"),
    CreateCreationChooseAudio("/main/resources/application/createCreation/ChooseAudio.fxml"),
    CreateCreationNaming("/main/resources/application/createCreation/CreationNaming.fxml"),
    ViewCreations("/main/resources/application/view/ViewCreations.fxml"),
    VideoPlayer("/main/resources/application/view/VideoPlayer.fxml"),
    CreateCreationChooseImages("/main/resources/application/createCreation/ChooseImages.fxml");

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
