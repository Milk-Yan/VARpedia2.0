package main.java.application;

/**
 * Constant type names and addresses so easy to change.
 * @author Milk
 *
 */
public enum SceneType {

	MainMenu("/main/resources/application/MainMenu.fxml"),
	CreateAudioSearch("/main/resources/application/CreateAudioSearch.fxml"), 
	LoadingSearchResults("/main/resources/application/LoadingSearchResults.fxml"), 
	CreateAudioSearchResults("/main/resources/application/CreateAudioSearchResults.fxml"),
	CreateAudioNaming("/main/resources/application/CreateAudioNaming.fxml"),
	LoadingCreateAudio("/main/resources/application/LoadingCreateAudio.fxml"),
	CreateAudioPreview("/main/resources/application/CreateAudioPreview.fxml"),
	
	View("/main/resources/application/View.fxml"), 
	VideoPlayer("/main/resources/application/VideoPlayer.fxml");
	
	private String _address;
	
	SceneType(String address) {
		this._address = address;
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
