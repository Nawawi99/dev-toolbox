package dev.awn.ui.regex;

import dev.awn.service.regex.RegexService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RegexToolView implements ToolView {
    private final RegexService service = new RegexService();
    private final VBox view = Ui.toolPage(title());
    private final TextField regex = Ui.textField("Regex");
    private final TextField replacement = Ui.textField("Replacement preview");
    private final TextArea text = Ui.textArea("Test text");
    private final TextArea output = Ui.textArea("Matches, groups, or replacement preview");
    private final Label status = Ui.status();

    public RegexToolView() {
        HBox editors = new HBox(10, text, output);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(editors, Priority.ALWAYS);
        view.getChildren().addAll(
                regex,
                replacement,
                Ui.actions(
                        Ui.button("Find Matches", () -> run(() -> service.matches(regex.getText(), text.getText()))),
                        Ui.button("Replacement Preview", () -> run(() -> service.replacementPreview(regex.getText(), replacement.getText(), text.getText()))),
                        Ui.button("Java Regex String", () -> run(() -> service.javaString(regex.getText()))),
                        Ui.copyButton(output),
                        Ui.clearButton(this::clear)
                ),
                editors,
                status
        );
    }

    @Override
    public String title() {
        return "Regex Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void clear() {
        regex.clear();
        replacement.clear();
        text.clear();
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
