package main.java.application;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;

/**
 * Creates JavaFX Alerts and shows them.
 *
 * @author Milk, OverCry
 */
public class AlertFactory {

    private Alert _alert;

    /**
     * Creates an alert for the user when called
     *
     * @param alertType The type of the alert.
     * @param title The title of the alert.
     * @param header The header of the alert.
     * @param content The content of the alert.
     */
    public AlertFactory(AlertType alertType, String title, String header, String content) {

        _alert = new Alert(alertType);

        // if the input is null, just don't set it
        if (title != null) {
            _alert.setTitle(title);
        }
        if (header != null) {
            _alert.setHeaderText(header);
        }
        if (content != null) {
            _alert.setContentText(content);
        }

        _alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        _alert.showAndWait();
    }

    /**
     * @return The JavaFX Alert created.
     */
    public Alert getAlert() {
        return _alert;
    }
}
