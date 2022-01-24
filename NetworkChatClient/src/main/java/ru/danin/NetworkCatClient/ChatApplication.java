package ru.danin.NetworkCatClient;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ChatApplication extends Application {

    private static final String CONNECTION_ERROR_MESSAGE = "CONNECTION ERROR MESSAGE";
    private Stage primaryStage;
    private Stage authStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        ChatController controller = createChatDialog(stage);
        createAuthDialog();
        controller.initializeMessageHandler();
    }

    private void createAuthDialog() throws IOException {
        FXMLLoader authLoader = new FXMLLoader();
        authLoader.setLocation(getClass().getResource("authDialog.fxml"));
        AnchorPane authDialogPanel = authLoader.load();
        authStage = new Stage();
        authStage.initOwner(primaryStage);
        authStage.initModality(Modality.WINDOW_MODAL);

        authStage.setScene(new Scene(authDialogPanel));
        AuthController authController = authLoader.getController();
        authController.setClientChat(this);
        authController.initializeMessageHandler();
        authStage.showAndWait();
    }

    private ChatController createChatDialog(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("chat-view.fxml"));
        Parent load = fxmlLoader.load();
        Scene scene = new Scene(load);
        this.primaryStage.setTitle("chat GeekBrains");
        this.primaryStage.setScene(scene);
        ChatController controller = fxmlLoader.getController();

        controller.userList.getItems().addAll("UserName1", "UserName2","UserName3");
        stage.show();
        connectToServer(controller);
        return controller;
    }

    private void connectToServer(ChatController chatController) {

        boolean result = Network.getInstance().connect();
        if (!result) {
            System.err.println(CONNECTION_ERROR_MESSAGE);
            showErrorDialog(CONNECTION_ERROR_MESSAGE);
            return;
        }

        chatController.setApplication(this);
        this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Network.getInstance().close();
            }
        });
    }

    public void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }

    public Stage getAuthStage() {
        return authStage;
    }

    public Stage getChatStage() {
        return this.primaryStage;
    }
}