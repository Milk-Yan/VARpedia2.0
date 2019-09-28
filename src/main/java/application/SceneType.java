package main.java.application;

import java.io.File;

/**
 * Constant type names and addresses so easy to change.
 * @author Milk
 *
 */

public enum SceneType {

	MainMenu("/main/resources/application/MainMenu.fxml"),
	CreateAudioSearch("/main/resources/application/CreateAudioSearch.fxml"), 
	LoadingSearchResults("/main/resources/application/LoadingSearchResults.fxml"), 
	CreateAudioChooseText("/main/resources/application/CreateAudioChooseText.fxml"),
	CreateAudioNaming("/main/resources/application/CreateAudioNaming.fxml"),
	LoadingCreateAudio("/main/resources/application/LoadingCreateAudio.fxml"),
	CreateCreationSearch("/main/resources/application/CreateCreationSearch.fxml"),
	CreateCreationChooseAudio("/main/resources/application/CreateCreationChooseAudio.fxml"),
	CreateCreationCreateSlideshow("/main/resources/application/CreateCreationCreateSlideshow.fxml"),
	LoadingCreateCreation("/main/resources/application/LoadingCreateCreation.fxml"),
	CreateCreationNaming("/main/resources/application/CreateCreationNaming.fxml"),
	LoadingViewCreations("/main/resources/application/LoadingViewCreations.fxml"),
	ViewCreations("/main/resources/application/ViewCreations.fxml"),
	VideoPlayer("/main/resources/application/VideoPlayer.fxml"),
	LoadingScrapingImages("/main/resources/application/LoadingScrapingImages.fxml"),
	CreateCreationChooseImages("/main/resources/application/CreateCreationChooseImages.fxml");
	
	private String _address;
	
	SceneType(String address) {
		this._address = address.replaceAll("/", File.separator);
	}
	
	/**
	 * 
	 * @return Address of the fxml for the SceneType.
	 */
	public String getAddress() {
		// string is immutable so ok to send like this
		return _address;
	}

}
