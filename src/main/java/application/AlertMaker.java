package main.java.application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;

/**
 * Creates alerts and shows them.
 * @author Milk
 *
 */
public class AlertMaker {
	
	Alert _alert;

	/**
	 * Creates an alert for the user when called
	 * @param alertType
	 * @param title
	 * @param headerText
	 * @param contentText
	 */
	public AlertMaker(AlertType alertType, String title, String headerText, String contentText) {

		_alert = new Alert(alertType);
		if (title != null) {
			_alert.setTitle(title);
		}
		if (headerText != null) {
			_alert.setHeaderText(headerText);
		}
		if (contentText != null) {
			_alert.setContentText(contentText);
		}

		_alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		_alert.showAndWait();
	}
	
	/**
	 * 
	 * @return The alert created.
	 */
	public Alert getAlert() {
		return _alert;
	}
}
