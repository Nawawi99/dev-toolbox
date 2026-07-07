package dev.awn.ui.encoding;

import dev.awn.service.encoding.EncodingService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class EncodingToolView implements ToolView {
    private final EncodingService service = new EncodingService();
    private final VBox view = Ui.toolPage(title());
    private final TextArea input = Ui.textArea("Input");
    private final TextField bcryptHash = Ui.textField("BCrypt hash for verification");
    private final TextArea output = Ui.textArea("Output");
    private final Label status = Ui.status();

    public EncodingToolView() {
        HBox editors = new HBox(10, input, output);
        HBox.setHgrow(input, Priority.ALWAYS);
        HBox.setHgrow(output, Priority.ALWAYS);
        VBox.setVgrow(editors, Priority.ALWAYS);
        view.getChildren().addAll(
                bcryptHash,
                Ui.actions(
                        Ui.button("Base64 Encode", () -> run(() -> service.base64Encode(input.getText()))),
                        Ui.button("Base64 Decode", () -> run(() -> service.base64Decode(input.getText()))),
                        Ui.button("URL Encode", () -> run(() -> service.urlEncode(input.getText()))),
                        Ui.button("URL Decode", () -> run(() -> service.urlDecode(input.getText()))),
                        Ui.button("HTML Escape", () -> run(() -> service.htmlEscape(input.getText()))),
                        Ui.button("HTML Unescape", () -> run(() -> service.htmlUnescape(input.getText()))),
                        Ui.button("Hex Encode", () -> run(() -> service.hexEncode(input.getText()))),
                        Ui.button("Hex Decode", () -> run(() -> service.hexDecode(input.getText()))),
                        Ui.button("SHA-256", () -> run(() -> service.hash("SHA-256", input.getText()))),
                        Ui.button("SHA-512", () -> run(() -> service.hash("SHA-512", input.getText()))),
                        Ui.button("MD5", () -> run(() -> service.hash("MD5", input.getText()))),
                        Ui.button("BCrypt Hash", () -> run(() -> service.bcrypt(input.getText()))),
                        Ui.button("Verify BCrypt", () -> run(() -> String.valueOf(service.verifyBcrypt(input.getText(), bcryptHash.getText())))),
                        Ui.copyButton(output),
                        Ui.clearButton(this::clear)
                ),
                editors,
                status
        );
    }

    @Override
    public String title() {
        return "Encoding Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void clear() {
        input.clear();
        bcryptHash.clear();
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
