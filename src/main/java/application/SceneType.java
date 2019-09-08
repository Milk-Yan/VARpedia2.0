package main.java.application;

/**
 * Constant type names and addresses so easy to change.
 * @author Milk
 *
 */
public enum SceneType {

	MainMenu("/main/resources/application/Main.fxml"),
	Create("/main/resources/application/Create.fxml"), 
	View("/main/resources/application/View.fxml"), 
	Loading("/main/resources/application/Loading.fxml"), 
	Naming("/main/resources/application/Naming.fxml"),
	SearchResults("/main/resources/application/SearchResults.fxml"),
	VideoPlayer("/main/resources/application/VideoPlayer.fxml"),
	Preview("/main/resources/application/Preview.fxml");
	
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
