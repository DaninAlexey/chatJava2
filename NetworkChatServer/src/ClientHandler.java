import Auth.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    public static final String AUTH_OK = "/authOK";
    public static final String AUTH_COMMAND = "/AUTH";
    private final Socket clientSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final MyServer server;
    private User user;

    public ClientHandler(MyServer server, Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.server = server;
    }

    public void handle() throws IOException {
        inputStream = new DataInputStream(clientSocket.getInputStream());
        outputStream = new DataOutputStream(clientSocket.getOutputStream());
        new Thread(() -> {
            try {
                user = authenticate();
                readMessage();
            } catch (IOException e) {
                System.err.println("Failed to process message from client");
                e.printStackTrace();
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    System.err.println("Failed to close connection");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private User authenticate() throws IOException {
        while (true) {
            String message = inputStream.readUTF();
            if (message.startsWith(AUTH_COMMAND)) {
                String[] parts = message.split(" ");
                String login = parts[1];
                String password = parts[2];

                User user = server.getAuthService().getUserByLoginAndPassword(login, password);


                if (user != null) {
                    sendMessage(String.format("%s %s", AUTH_OK, user.getUserName()));
                    server.subscribe(this);
                    return user;
                }
            }
        }
    }

    public void readMessage() throws IOException {
        while (true) {
            String message = inputStream.readUTF().trim();
            System.out.println("message = " + this.user.getUserName() + " " + message);
            if (message.startsWith("/end")) {
                return;
            } else {
                processMessage(message);
            }
        }
    }

    public User getUser() {
        return user;
    }

    private void processMessage(String message) throws IOException {
        if (message.startsWith("/")) {
            String parts[] = message.split(" ");
            String userName = parts[0].substring(1);
            User user = this.server.getAuthService().getUserByUserName(userName);
            if(user != null && !user.equals(this.user))
                this.server.privateMessage(message, user);
        }
        else
            this.server.broadcastMessage(message, this);
    }

    public void sendMessage(String message) throws IOException {
        this.outputStream.writeUTF(message);
    }

    private void closeConnection() throws IOException {
        server.unsubscribe(this);
        clientSocket.close();
    }
}