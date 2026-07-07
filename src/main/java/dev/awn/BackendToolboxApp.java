package dev.awn;

import dev.awn.ui.AppView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;

public class BackendToolboxApp extends Application {
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new AppView().root(), 1280, 820);
        scene.getStylesheets().add(BackendToolboxApp.class.getResource("/styles.css").toExternalForm());
        stage.setTitle("Backend Toolbox");
        InputStream icon = BackendToolboxApp.class.getResourceAsStream("/icons/backend-toolbox.png");
        if (icon != null) {
            stage.getIcons().add(new Image(icon));
        }
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
