package ru.danin.NetworkCatClient;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.function.Consumer;

public class ChatController {

    private ChatApplication application;

    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private Button sendButton;
    @FXML
    public ListView<String> userList;

    public void sendMessage() {
        textArea.appendText(DateFormat.getDateTimeInstance().format(new Date()));
        textArea.appendText(System.lineSeparator());
        textField.setFocusTraversable(true);
        String message = textField.getText().trim();
        if (message.isEmpty()) {
            textField.clear();
            return;
        }

        String sender = null;
        if (!userList.getSelectionModel().isEmpty()) {
            sender = userList.getSelectionModel().getSelectedItem();
        }
        try {
            message = sender != null ? String.join(":", sender, message) : message;
            Network.getInstance().sendMessage(message);
        } catch (IOException e) {
            application.showErrorDialog("ошибка передачи данных по сети");
            e.printStackTrace();
        }
        appendMessageToChat("Я", message);
    }

    private void appendMessageToChat(String sender, String message) {
        if (sender != null) {
            textArea.appendText(sender + ":");
            textArea.appendText(System.lineSeparator());
        }
        textArea.appendText(message);
        textArea.appendText(System.lineSeparator());
        textArea.appendText(System.lineSeparator());
        textField.setFocusTraversable(true);
        textField.clear();
    }


    public void setApplication(ChatApplication application) {
        this.application = application;
    }

    public void initializeMessageHandler() {
        Network.getInstance().waitMessage(new Consumer<String>() {
            @Override
            public void accept(String message) {
                appendMessageToChat("Cервер", message);
            }
        });
    }
}