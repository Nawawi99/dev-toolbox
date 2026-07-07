package dev.awn.ui;

import dev.awn.ui.compose.DockerComposeToolView;
import dev.awn.ui.encoding.EncodingToolView;
import dev.awn.ui.html.HtmlToolView;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AppView {
    private final BorderPane root = new BorderPane();
    private final VBox nav = new VBox(6);
    private final VBox navItems = new VBox(6);
    private final List<Button> navButtons = new ArrayList<>();
    private final List<ToolView> tools = List.of(
            new JsonToolView(),
            new JwtToolView(),
            new YamlToolView(),
            new SqlToolView(),
            new HtmlToolView(),
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
        Label title = new Label("Dev Toolbox");
        title.getStyleClass().add("app-title");
        nav.getChildren().add(title);

        TextField search = new TextField();
        search.setPromptText("Search tools");
        search.getStyleClass().add("nav-search");
        Button clearSearch = new Button("x");
        clearSearch.getStyleClass().add("search-clear-button");
        clearSearch.setOnAction(event -> search.clear());
        HBox searchBox = new HBox(6, search, clearSearch);
        searchBox.getStyleClass().add("search-box");
        HBox.setHgrow(search, Priority.ALWAYS);
        nav.getChildren().add(searchBox);

        for (ToolView tool : tools) {
            Button button = new Button(tool.title());
            button.getStyleClass().add("nav-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(event -> select(tool));
            button.setUserData(tool);
            navButtons.add(button);
            navItems.getChildren().add(button);
        }
        search.textProperty().addListener((ignored, oldValue, newValue) -> filterTools(newValue));
        nav.getChildren().add(navItems);

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

    private void filterTools(String query) {
        String normalized = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        navItems.getChildren().setAll(navButtons.stream()
                .filter(button -> ((ToolView) button.getUserData()).title().toLowerCase(Locale.ROOT).contains(normalized))
                .toList());
    }
}
