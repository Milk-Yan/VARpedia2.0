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
    LoadingSearchResults("/main/resources/application/loading/SearchTerm.fxml"),
    CreateAudioChooseText("/main/resources/application/createAudio/ChooseText.fxml"),
    CreateAudioNaming("/main/resources/application/createAudio/AudioNaming.fxml"),
    LoadingCreateAudio("/main/resources/application/loading/CreateAudio.fxml"),
    CreateCreationSearch("/main/resources/application/createCreation/AudioSearch.fxml"),
    CreateCreationChooseAudio("/main/resources/application/createCreation/ChooseAudio.fxml"),
    LoadingCreateCreation("/main/resources/application/loading/CreateCreation.fxml"),
    CreateCreationNaming("/main/resources/application/createCreation/CreationNaming.fxml"),
    LoadingViewCreations("/main/resources/application/loading/GetCreations.fxml"),
    ViewCreations("/main/resources/application/view/ViewCreations.fxml"),
    VideoPlayer("/main/resources/application/view/VideoPlayer.fxml"),
    LoadingScrapingImages("/main/resources/application/loading/GetImages.fxml"),
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
