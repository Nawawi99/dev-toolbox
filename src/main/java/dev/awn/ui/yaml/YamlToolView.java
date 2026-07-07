package dev.awn.ui.yaml;

import dev.awn.service.yaml.YamlService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class YamlToolView implements ToolView {
    private final YamlService service = new YamlService();
    private final VBox view = Ui.toolPage(title());
    private final TextArea input = Ui.textArea("Paste YAML or JSON here");
    private final TextArea output = Ui.textArea("Output");
    private final Label status = Ui.status();

    public YamlToolView() {
        HBox editors = new HBox(10, input, output);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(editors, Priority.ALWAYS);
        view.getChildren().addAll(
                Ui.actions(
                        Ui.button("Validate", this::validate),
                        Ui.button("Format YAML", () -> run(() -> service.format(input.getText()))),
                        Ui.button("YAML to JSON", () -> run(() -> service.yamlToJson(input.getText()))),
                        Ui.button("JSON to YAML", () -> run(() -> service.jsonToYaml(input.getText()))),
                        Ui.copyButton(output),
                        Ui.clearButton(this::clear)
                ),
                editors,
                status
        );
    }

    @Override
    public String title() {
        return "YAML Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void validate() {
        try {
            service.validate(input.getText());
            output.setText("Valid YAML.");
            Ui.ok(status, "YAML is valid.");
        } catch (Exception e) {
            Ui.error(status, e);
        }
    }

    private void clear() {
        input.clear();
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
