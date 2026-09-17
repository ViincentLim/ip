package dude.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Displays one message in the GUI conversation.
 */
public class DialogBox extends HBox {
    /**
     * Visual categories used to distinguish conversation participants.
     */
    public enum Kind {
        /** Command typed by the user. */
        USER,
        /** Normal response from DUDE. */
        APP,
        /** Response describing invalid input. */
        ERROR
    }

    /**
     * Creates a styled conversation message.
     *
     * @param text Message to display.
     * @param kind Message category.
     */
    public DialogBox(String text, Kind kind) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(message, Priority.ALWAYS);
        setAlignment(kind == Kind.USER ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        getStyleClass().addAll("dialog-box", kind.name().toLowerCase());
        getChildren().add(message);
    }
}
