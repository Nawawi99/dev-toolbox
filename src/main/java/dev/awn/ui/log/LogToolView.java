package dev.awn.ui.log;

import dev.awn.service.log.LogService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LogToolView implements ToolView {
    private final LogService service = new LogService();
    private final VBox view = Ui.toolPage(title());
    private final TextField keyword = Ui.textField("Keyword");
    private final ComboBox<String> level = new ComboBox<>();
    private final CheckBox prettyJson = new CheckBox("Pretty JSON log lines");
    private final TextArea input = Ui.textArea("Paste logs here");
    private final TextArea output = Ui.textArea("Filtered logs");
    private final Label status = Ui.status();

    public LogToolView() {
        level.getItems().addAll("ANY", "ERROR", "WARN", "INFO", "DEBUG", "TRACE");
        level.setValue("ANY");
        HBox filters = new HBox(8, keyword, level, prettyJson);
        HBox.setHgrow(keyword, Priority.ALWAYS);
        HBox editors = new HBox(10, input, output);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(editors, Priority.ALWAYS);
        view.getChildren().addAll(
                filters,
                Ui.actions(Ui.button("Filter", this::filter), Ui.copyButton(output), Ui.clearButton(this::clear)),
                editors,
                status
        );
    }

    @Override
    public String title() {
        return "Log Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void filter() {
        output.setText(service.filter(input.getText(), keyword.getText(), level.getValue(), prettyJson.isSelected()));
        Ui.ok(status, "Done.");
    }

    private void clear() {
        keyword.clear();
        input.clear();
        output.clear();
        status.setText("");
    }
}
