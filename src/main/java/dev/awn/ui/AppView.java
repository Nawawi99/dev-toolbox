package dev.awn.ui;

import dev.awn.ui.compose.DockerComposeToolView;
import dev.awn.ui.encoding.EncodingToolView;
import dev.awn.ui.json.JsonToolView;
import dev.awn.ui.jwt.JwtToolView;
import dev.awn.ui.log.LogToolView;
import dev.awn.ui.regex.RegexToolView;
import dev.awn.ui.sql.SqlToolView;
import dev.awn.ui.time.TimeToolView;
import dev.awn.ui.yaml.YamlToolView;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class AppView {
    private final BorderPane root = new BorderPane();
    private final VBox nav = new VBox(6);
    private final List<ToolView> tools = List.of(
            new JsonToolView(),
            new JwtToolView(),
            new YamlToolView(),
            new SqlToolView(),
            new RegexToolView(),
            new TimeToolView(),
            new EncodingToolView(),
            new LogToolView(),
            new DockerComposeToolView()
    );

    public AppView() {
        root.getStyleClass().add("app-root");
        nav.getStyleClass().add("sidebar");
        nav.setPadding(new Insets(18, 12, 18, 12));
        Label title = new Label("Backend Toolbox");
        title.getStyleClass().add("app-title");
        nav.getChildren().add(title);

        for (ToolView tool : tools) {
            Button button = new Button(tool.title());
            button.getStyleClass().add("nav-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(event -> select(tool));
            nav.getChildren().add(button);
        }

        VBox.setVgrow(nav, Priority.ALWAYS);
        root.setLeft(nav);
        select(tools.getFirst());
    }

    public Parent root() {
        return root;
    }

    private void select(ToolView tool) {
        root.setCenter(tool.view());
    }
}
