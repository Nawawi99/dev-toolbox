package dev.awn.ui.html;

import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class HtmlToolView implements ToolView {
    private final VBox view = Ui.toolPage(title());
    private final TextArea input = Ui.textArea("Paste self-contained HTML here");
    private final WebView preview = new WebView();
    private final Label status = Ui.status();

    public HtmlToolView() {
        preview.getStyleClass().add("html-preview");
        SplitPane split = new SplitPane(input, preview);
        split.setDividerPositions(0.48);
        VBox.setVgrow(split, Priority.ALWAYS);
        view.getChildren().addAll(
                Ui.actions(
                        Ui.button("Render", this::render),
                        Ui.button("Expand Preview", this::expandPreview),
                        Ui.clearButton(this::clear)
                ),
                split,
                status
        );
        input.setText("""
                <!doctype html>
                <html>
                <head>
                  <style>
                    body { font-family: system-ui; padding: 32px; color: #1f2937; }
                    .box { border: 1px solid #d1d5db; border-radius: 8px; padding: 20px; }
                  </style>
                </head>
                <body>
                  <div class="box">
                    <h1>Dev Toolbox</h1>
                    <p>Paste HTML on the left and render it here.</p>
                  </div>
                </body>
                </html>
                """);
        render();
    }

    @Override
    public String title() {
        return "HTML Viewer";
    }

    @Override
    public Node view() {
        return view;
    }

    private void render() {
        preview.getEngine().loadContent(input.getText());
        Ui.ok(status, "HTML rendered locally.");
    }

    private void expandPreview() {
        WebView expanded = new WebView();
        expanded.getEngine().loadContent(input.getText());
        Stage stage = new Stage();
        stage.setTitle("HTML Preview");
        stage.setScene(new Scene(expanded, 1000, 760));
        stage.show();
    }

    private void clear() {
        input.clear();
        preview.getEngine().loadContent("");
        status.setText("");
    }
}
