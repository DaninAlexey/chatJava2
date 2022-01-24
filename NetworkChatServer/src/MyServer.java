import Auth.AuthService;
import Auth.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MyServer {

    private static final List<ClientHandler> clients = new ArrayList<>();
    private AuthService authService;

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server has been started");
            authService = new AuthService();
            while (true) {
                waitingAndProcessClientConnection(serverSocket);
            }
        } catch (IOException e) {
            System.out.println("Filed to bind port " + port);
            e.printStackTrace();
        }
    }

    private void waitingAndProcessClientConnection(ServerSocket serverSocket) throws IOException {
        System.out.println("Waiting new client connection");
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client has been connected");
        ClientHandler clientHandler = new ClientHandler(this, clientSocket);
        clientHandler.handle();

    }

    public void broadcastMessage(String message, ClientHandler sender) throws IOException {
        for (ClientHandler client : clients) {
            if(client != sender)
                client.sendMessage(message);
        }
    }

    public void privateMessage(String message, User recipient) throws IOException {
        for (ClientHandler client : clients) {
            if(client.getUser().equals(recipient))
                client.sendMessage(message);
        }
    }

    public void subscribe(ClientHandler clientHandler)
    {
        this.clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler)
    {
        this.clients.remove(clientHandler);
    }

    public AuthService getAuthService() {
        return authService;
    }
}
