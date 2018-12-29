package chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final ServerSocket serverSocket;
    private final List<AcceptedClient> clients = new ArrayList<>();

    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void startAcceptingConnections() throws IOException {
        while (true) {
            LOGGER.info("waiting for a connection");
            Socket clientSocket = serverSocket.accept();

            LOGGER.info("accepted a client from {}", clientSocket.getInetAddress());

            AcceptedClient client = new AcceptedClient(clientSocket);
            client.onMessage(message -> {
                sendToEveryone(message);
            });

            clients.add(client);
        }
    }

    public void sendToEveryone(String message) {
        clients.forEach(client -> {
            try {
                client.send(message);
            } catch (IOException e) {
                LOGGER.error("failed to send to one of the clients", e);
            }
        });
    }
}
