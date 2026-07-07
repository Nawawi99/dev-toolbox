package dev.awn.ui.components;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class Ui {
    private Ui() {
    }

    public static VBox toolPage(String title) {
        VBox page = new VBox(12);
        page.getStyleClass().add("tool-page");
        Label heading = new Label(title);
        heading.getStyleClass().add("tool-heading");
        page.getChildren().add(heading);
        return page;
    }

    public static TextArea textArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setWrapText(false);
        area.getStyleClass().add("code-area");
        VBox.setVgrow(area, Priority.ALWAYS);
        return area;
    }

    public static TextField textField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        return field;
    }

    public static FlowPane actions(Button... buttons) {
        FlowPane box = new FlowPane(8, 8, buttons);
        box.getStyleClass().add("actions");
        return box;
    }

    public static Button button(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(event -> action.run());
        return button;
    }

    public static Button copyButton(TextInputControl source) {
        Button button = button("Copy", () -> copy(source.getText()));
        button.getStyleClass().add("copy-button");
        return button;
    }

    public static Button clearButton(Runnable action) {
        Button button = button("Clear", action);
        button.getStyleClass().add("clear-button");
        return button;
    }

    public static void copy(String value) {
        ClipboardContent content = new ClipboardContent();
        content.putString(value == null ? "" : value);
        Clipboard.getSystemClipboard().setContent(content);
    }

    public static Label status() {
        Label label = new Label();
        label.getStyleClass().add("status");
        label.setWrapText(true);
        return label;
    }

    public static void ok(Label status, String message) {
        status.setText(message);
        status.getStyleClass().remove("error");
    }

    public static void error(Label status, Exception exception) {
        status.setText(exception.getMessage() == null ? "Unable to process input." : exception.getMessage());
        if (!status.getStyleClass().contains("error")) {
            status.getStyleClass().add("error");
        }
    }
}
