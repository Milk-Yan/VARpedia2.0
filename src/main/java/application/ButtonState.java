package main.java.application;

/**
 * enum to determine type of button.
 */
public enum ButtonState {

    EDIT("Edit Text"),
    SAVE("Save Text"),
    RESET("Reset to Default Text"),
    CANCEL("Cancel Editing"),
    PAUSE("Pause"),
    PLAY("Play");


    private String _text;

    ButtonState(String text) {
        this._text = text;
    }

    /**
     * @return Message of the button for the specified type.
     */
    public String getText() {
        // string is immutable so ok to send like this
        return _text;
    }

}
