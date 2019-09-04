package main.java.application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Creates alerts.
 * @author Milk
 *
 */
public class AlertMaker {
	
	Alert _alert;

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

		_alert.showAndWait();
	}
	
	public Alert getAlert() {
		return _alert;
	}
}
