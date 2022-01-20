package ru.danin.NetworkCatClient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.util.function.Consumer;

public class AuthController {
    public static final String AUTH_COMMAND = "/auth";
    public static final String AUTH_OK_COMMAND = "/authOK";

    @FXML
    private javafx.scene.control.Button authButton;
    @FXML
    private javafx.scene.control.TextField loginField;
    @FXML
    private PasswordField PassField;

    private ChatApplication clientChat;

    @FXML
    public void executeAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = PassField.getText();

        if (login == null || login.isBlank() || password == null || login.isBlank()) {
            clientChat.showErrorDialog("Логин и пароль должны быть указаны");
            return;
        }
        String authCommandMessage = String.format("%S %S %S", AUTH_COMMAND, login, password);

        try {
            Network.getInstance().sendMessage(authCommandMessage);

        } catch (IOException e) {
            clientChat.showErrorDialog("Ошибка передачи данных по сети");
            e.printStackTrace();
        }

    }

    public void initializeMessageHandler() {
        Network.getInstance().waitMessage(new Consumer<String>() {
            @Override
            public void accept(String message) {
                if (message.startsWith(AUTH_OK_COMMAND)) {
                    String[] parts = message.split(" ");
                    String userName = parts[1];
                    Thread.currentThread().interrupt();
                    Platform.runLater(() -> {
                        clientChat.getChatStage().setTitle(userName);
                        clientChat.getAuthStage().close();
                    });
                } else {
                    Platform.runLater(() -> {
                        clientChat.showErrorDialog("Пользователя с таким логином и паролем не существует");
                    });
                }
            }
        });
    }

    public void setClientChat(ChatApplication clientChat) {
        this.clientChat = clientChat;
    }
}
