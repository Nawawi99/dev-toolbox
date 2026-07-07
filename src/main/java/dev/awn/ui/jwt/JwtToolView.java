package dev.awn.ui.jwt;

import dev.awn.service.jwt.JwtService;
import dev.awn.ui.ToolView;
import dev.awn.ui.components.Ui;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class JwtToolView implements ToolView {
    private final JwtService service = new JwtService();
    private final VBox view = Ui.toolPage(title());
    private final TextArea token = Ui.textArea("Paste JWT here");
    private final TextArea header = Ui.textArea("Header");
    private final TextArea payload = Ui.textArea("Payload");
    private final TextArea signature = Ui.textArea("Signature");
    private final Label status = Ui.status();

    public JwtToolView() {
        HBox decoded = new HBox(10, header, payload, signature);
        HBox.setHgrow(header, Priority.ALWAYS);
        HBox.setHgrow(payload, Priority.ALWAYS);
        HBox.setHgrow(signature, Priority.ALWAYS);
        VBox.setVgrow(token, Priority.ALWAYS);
        VBox.setVgrow(decoded, Priority.ALWAYS);
        view.getChildren().addAll(
                Ui.actions(Ui.button("Decode", this::decode), Ui.copyButton(payload), Ui.clearButton(this::clear)),
                token,
                decoded,
                status
        );
    }

    @Override
    public String title() {
        return "JWT Toolkit";
    }

    @Override
    public Node view() {
        return view;
    }

    private void decode() {
        try {
            JwtService.DecodedJwt jwt = service.decode(token.getText());
            header.setText(jwt.header());
            payload.setText(jwt.payload());
            signature.setText(jwt.signature());
            String expiry = jwt.expiresAt().isBlank() ? "No exp claim." : "Expires: " + jwt.expiresAt() + ". ";
            String issued = jwt.issuedAt().isBlank() ? "" : "Issued: " + jwt.issuedAt() + ". ";
            Ui.ok(status, issued + expiry + (jwt.expired() ? "Token is expired." : "Token is not expired."));
        } catch (Exception e) {
            Ui.error(status, e);
        }
    }

    private void clear() {
        token.clear();
        header.clear();
        payload.clear();
        signature.clear();
        status.setText("");
    }
}
