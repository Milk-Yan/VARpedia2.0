package main.java.application;

public enum SceneType {

	MainMenu("/main/resources/application/Main.fxml"),
	Create("/main/resources/application/Create.fxml"), 
	View("/main/resources/application/View.fxml"), 
	Loading("/main/resources/application/Loading.fxml"), 
	Naming("/main/resources/application/Naming.fxml"),
	SearchResults("/main/resources/application/SearchResults.fxml"),
	VideoPlayer("/main/resources/application/VideoPlayer.fxml");
	
	private String _address;
	
	SceneType(String address) {
		this._address = address;
	}
	
	public String getAddress() {
		// string is immutable so ok to send like this
		return _address;
	}

}
