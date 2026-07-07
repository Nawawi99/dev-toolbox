package dev.awn.ui.compose;

import dev.awn.service.compose.DockerComposeService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class DockerComposeToolView implements ToolView {
    private final DockerComposeService service = new DockerComposeService();
    private final VBox view = Ui.toolPage(title());
    private final TextArea input = Ui.textArea("Paste docker-compose YAML here");
    private final TextArea output = Ui.textArea("Services, ports, duplicates, and dependencies");
    private final Label status = Ui.status();

    public DockerComposeToolView() {
        HBox editors = new HBox(10, input, output);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(editors, Priority.ALWAYS);
        view.getChildren().addAll(
                Ui.actions(Ui.button("Analyze", this::analyze), Ui.copyButton(output), Ui.clearButton(this::clear)),
                editors,
                status
        );
    }

    @Override
    public String title() {
        return "Docker Compose Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void analyze() {
        try {
            output.setText(service.analyze(input.getText()));
            Ui.ok(status, "Compose YAML analyzed.");
        } catch (Exception e) {
            Ui.error(status, e);
        }
    }

    private void clear() {
        input.clear();
        output.clear();
        status.setText("");
    }
}
