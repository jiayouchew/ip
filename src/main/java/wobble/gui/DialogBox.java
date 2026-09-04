package wobble.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Represents one user or Wobble message in the conversation. */
public class DialogBox extends HBox {
    private final Label message = new Label();

    private DialogBox(String text, boolean isBotMessage) {
        message.setText(text);
        message.setWrapText(true);
        message.getStyleClass().add(isBotMessage ? "bot-message" : "user-message");
        setAlignment(isBotMessage ? Pos.TOP_LEFT : Pos.TOP_RIGHT);
        getChildren().add(message);
    }

    /** Creates a message displayed as sent by the user. */
    public static DialogBox userMessage(String text) {
        return new DialogBox(text, false);
    }

    /** Creates a message displayed as sent by Wobble. */
    public static DialogBox botMessage(String text) {
        return new DialogBox(text, true);
    }
}
