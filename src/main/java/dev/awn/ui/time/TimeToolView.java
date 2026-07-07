package dev.awn.ui.time;

import dev.awn.service.time.TimeService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TimeToolView implements ToolView {
    private final TimeService service = new TimeService();
    private final VBox view = Ui.toolPage(title());
    private final TextField input = Ui.textField("Timestamp, ISO-8601 date, or duration number");
    private final CheckBox millis = new CheckBox("Timestamp is milliseconds");
    private final ComboBox<String> unit = new ComboBox<>();
    private final TextArea output = Ui.textArea("Output");
    private final Label status = Ui.status();

    public TimeToolView() {
        unit.getItems().addAll("milliseconds", "seconds", "minutes", "hours", "days");
        unit.setValue("seconds");
        VBox.setVgrow(output, javafx.scene.layout.Priority.ALWAYS);
        view.getChildren().addAll(
                input,
                millis,
                unit,
                Ui.actions(
                        Ui.button("Unix to Date", () -> run(() -> service.unixToDate(input.getText(), millis.isSelected()))),
                        Ui.button("Date to Unix", () -> run(() -> service.dateToUnix(input.getText()))),
                        Ui.button("Parse ISO", () -> run(() -> service.parseIso(input.getText()))),
                        Ui.button("Convert Duration", () -> run(() -> service.duration(Long.parseLong(input.getText().trim()), unit.getValue()))),
                        Ui.copyButton(output),
                        Ui.clearButton(this::clear)
                ),
                output,
                status
        );
    }

    @Override
    public String title() {
        return "Time Toolkit";
    }

    @Override
    public Node view() {
        return view;
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
