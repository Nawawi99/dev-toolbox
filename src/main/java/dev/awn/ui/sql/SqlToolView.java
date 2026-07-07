package dev.awn.ui.sql;

import dev.awn.service.sql.SqlService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SqlToolView implements ToolView {
    private final SqlService service = new SqlService();
    private final VBox view = Ui.toolPage(title());
    private final TextField tableName = Ui.textField("Table name for JSON array to INSERT");
    private final TextArea input = Ui.textArea("Paste SQL or JSON array here");
    private final TextArea output = Ui.textArea("Output");
    private final Label status = Ui.status();

    public SqlToolView() {
        HBox editors = new HBox(10, input, output);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(editors, Priority.ALWAYS);
        view.getChildren().addAll(
                tableName,
                Ui.actions(
                        Ui.button("Format", () -> run(() -> service.format(input.getText()))),
                        Ui.button("Minify", () -> run(() -> service.minify(input.getText()))),
                        Ui.button("Capitalize Keywords", () -> run(() -> service.capitalize(input.getText()))),
                        Ui.button("Java String", () -> run(() -> service.toJavaString(input.getText()))),
                        Ui.button("Text Block", () -> run(() -> service.toTextBlock(input.getText()))),
                        Ui.button("Visualize SQL", () -> run(() -> service.visualizeStructure(input.getText()))),
                        Ui.button("JSON to INSERT", () -> run(() -> service.insertsFromJson(tableName.getText(), input.getText()))),
                        Ui.copyButton(output),
                        Ui.clearButton(this::clear)
                ),
                editors,
                status
        );
    }

    @Override
    public String title() {
        return "SQL Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void clear() {
        tableName.clear();
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
