package wobble.gui;

import java.net.URL;
import java.util.Locale;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/** Represents one user or Wobble message in the conversation. */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 32;
    private static final double AVATAR_FRAME_SIZE = 36;
    private static final double MESSAGE_GAP = 8;
    private static final double MESSAGE_HORIZONTAL_SPACE = 32;
    private static final Image WOBBLE_AVATAR = loadImage("/images/wobble-avatar.png");
    private static final Image USER_AVATAR = loadImage("/images/user-avatar.png");

    private DialogBox(String text, String styleClass, Pos alignment, Image avatar,
            String avatarStyleClass) {
        this(createMessageLabel(text), styleClass, alignment, avatar, avatarStyleClass);
    }

    private DialogBox(Region message, String styleClass, Pos alignment, Image avatar,
            String avatarStyleClass) {
        message.getStyleClass().add(styleClass);
        StackPane avatarFrame = createAvatarFrame(avatar, avatarStyleClass);
        setAlignment(alignment);
        setSpacing(MESSAGE_GAP);
        setMaxWidth(Double.MAX_VALUE);
        message.maxWidthProperty().bind(Bindings.max(1,
                widthProperty().subtract(MESSAGE_HORIZONTAL_SPACE + AVATAR_FRAME_SIZE + MESSAGE_GAP)));
        HBox.setHgrow(message, Priority.NEVER);
        getStyleClass().add("dialog-box");
        if (alignment == Pos.TOP_LEFT) {
            getChildren().addAll(avatarFrame, message);
        } else {
            getChildren().addAll(message, avatarFrame);
        }
    }

    /** Creates a message displayed as sent by the user. */
    public static DialogBox userMessage(String text) {
        return new DialogBox(text, "user-message", Pos.TOP_RIGHT, USER_AVATAR, "user-avatar");
    }

    /** Creates a message displayed as sent by Wobble. */
    public static DialogBox botMessage(String text) {
        return new DialogBox(text, "bot-message", Pos.TOP_LEFT, WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Creates a Wobble error message with styling that draws attention. */
    public static DialogBox errorMessage(String text) {
        return new DialogBox(text, "error-message", Pos.TOP_LEFT, WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Creates one bot card with individually styled help headings and command formats. */
    public static DialogBox helpMessage(String text) {
        return new DialogBox(createHelpContent(text), "bot-message", Pos.TOP_LEFT,
                WOBBLE_AVATAR, "wobble-avatar");
    }

    /** Creates the structured content used inside the single help response card. */
    private static VBox createHelpContent(String text) {
        VBox content = new VBox(4);
        for (String line : text.split("\\R")) {
            if (line.isBlank()) {
                continue;
            }
            Label lineLabel = new Label(line.trim());
            lineLabel.setWrapText(true);
            if (isHeading(line)) {
                lineLabel.getStyleClass().add("help-heading");
                VBox.setMargin(lineLabel, new Insets(8, 0, 0, 0));
            } else if (line.startsWith("  ")) {
                lineLabel.getStyleClass().add("help-command");
            } else {
                lineLabel.getStyleClass().add("help-description");
            }
            content.getChildren().add(lineLabel);
        }
        return content;
    }

    /** Returns whether a help line is an uppercase section heading. */
    private static boolean isHeading(String line) {
        String trimmedLine = line.trim();
        return !trimmedLine.isEmpty() && trimmedLine.equals(trimmedLine.toUpperCase(Locale.ROOT));
    }

    /** Creates a regular message label with wrapping enabled. */
    private static Label createMessageLabel(String text) {
        Label message = new Label(text);
        message.setWrapText(true);
        return message;
    }

    /** Creates a compact circular avatar frame for a conversation participant. */
    private static StackPane createAvatarFrame(Image avatar, String styleClass) {
        ImageView avatarView = new ImageView(avatar);
        avatarView.setFitWidth(AVATAR_SIZE);
        avatarView.setFitHeight(AVATAR_SIZE);
        avatarView.setPreserveRatio(true);
        avatarView.setSmooth(true);
        avatarView.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        StackPane avatarFrame = new StackPane(avatarView);
        avatarFrame.setMinSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setPrefSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.setMaxSize(AVATAR_FRAME_SIZE, AVATAR_FRAME_SIZE);
        avatarFrame.getStyleClass().add(styleClass);
        return avatarFrame;
    }

    /** Loads a bundled avatar and fails early if the project resource is missing. */
    private static Image loadImage(String resourcePath) {
        URL imageUrl = DialogBox.class.getResource(resourcePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Missing Wobble avatar resource: " + resourcePath);
        }
        return new Image(imageUrl.toExternalForm());
    }
}
