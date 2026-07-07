package dev.awn.ui.json;

import dev.awn.service.json.JsonService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class JsonToolView implements ToolView {
    private final JsonService service = new JsonService();
    private final VBox view = Ui.toolPage(title());
    private final TextArea input = Ui.textArea("Paste JSON or a JSON string here");
    private final TextArea secondInput = Ui.textArea("Optional second JSON for compare");
    private final TextArea output = Ui.textArea("Output");
    private final Label status = Ui.status();

    public JsonToolView() {
        HBox editors = new HBox(10, input, secondInput, output);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(secondInput, Priority.ALWAYS);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(editors, Priority.ALWAYS);
        view.getChildren().addAll(
                Ui.actions(
                        Ui.button("Pretty", () -> run(() -> service.pretty(input.getText()))),
                        Ui.button("Minify", () -> run(() -> service.minify(input.getText()))),
                        Ui.button("Validate", this::validate),
                        Ui.button("Sort Keys", () -> run(() -> service.sortKeys(input.getText()))),
                        Ui.button("Escape", () -> run(() -> service.escapeString(input.getText()))),
                        Ui.button("Unescape", () -> run(() -> service.unescapeString(input.getText()))),
                        Ui.button("Compare", () -> run(() -> service.diff(input.getText(), secondInput.getText()))),
                        Ui.copyButton(output),
                        Ui.clearButton(this::clear)
                ),
                editors,
                status
        );
    }

    @Override
    public String title() {
        return "JSON Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void validate() {
        try {
            service.validate(input.getText());
            output.setText("Valid JSON.");
            Ui.ok(status, "JSON is valid.");
        } catch (Exception e) {
            Ui.error(status, e);
        }
    }

    private void clear() {
        input.clear();
        secondInput.clear();
        output.clear();
        status.setText("");
    }

    private void run(Action action) {
        try {
            output.setText(action.execute());
            Ui.ok(status, "Done.");
        } catch (Exception e) {
            Ui.error(status, e);
        }
    }

    @FunctionalInterface
    private interface Action {
        String execute() throws Exception;
    }
}
