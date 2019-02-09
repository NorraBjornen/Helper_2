package sample.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.KeyCode;
import sample.Main;
import sample.Model.NetWork;

public class AuthController {
    @FXML
    private Button Auth;
    @FXML
    private TextField Name;

    private String name;

    public void initialize() {
        Name.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        Name.setOnKeyPressed(e ->{
            if (e.getCode().equals(KeyCode.ENTER)) {
                name = Name.getText();
                String message = "[disp~" + name + "]";
                NetWork.get().send(message);
                Main.stage.setScene(Main.scene1);
                Main.stage.setFullScreen(true);
                Main.stage.setFullScreenExitHint("");
            }
        });

        Auth.setOnMouseClicked(e -> {
            name = Name.getText();
            String message = "[disp~" + name + "]";
            NetWork.get().send(message);
            Main.stage.setScene(Main.scene1);
            Main.stage.setFullScreen(true);
            Main.stage.setFullScreenExitHint("");
        });
    }
}
