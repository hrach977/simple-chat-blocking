package chatv2.server;

import chatv2.wrapper.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    //several consumers for each of the events (UserLoggedIn, UserSentGlobalMessage etc)
//    private BiConsumer<Long, String> serverStatusConsumer;
//    private BiConsumer<Long, String> userLoggedInConsumer;
//    private BiConsumer<Long, String> userLoggedOutConsumer;
    private final ServerSocket serverSocket;
    private final List<ConnectedClient> clients = new ArrayList<>();


    public Server(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public void startAcceptingConnections() throws IOException {
        while (true) {
            log.info("waiting for a connection");
            Socket clientSocket = serverSocket.accept();

            log.info("accepted a client from {}", clientSocket.getInetAddress());

            ConnectedClient client = new ConnectedClient(clientSocket);
            //broadcastMessage(MyMessage.userLoggedIn(System.currentTimeMillis(), client.getusername));
            client.onMessage(this::broadcastMessage);

            //maybe try getting the messages right from there, so that to be able to manage the list of the connected clients

            clients.add(client);
        }
    }

    private void broadcastMessage(MyMessage message) {
        clients.forEach(client -> {
            try {
                System.out.printf("SOCKET STATUS: " + client.getSocket().isClosed());
                if (client.getSocket().isClosed()) {
                    clients.remove(client);
                } else {
                    client.send(message);
                }
//                if (message.getContent().equalsIgnoreCase("bye")) {
//                    continue;
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

//    public void onUserLoggedIn(BiConsumer<Long, String> consumer) {
//        userLoggedInConsumer = consumer;
//    }
//
//    public void onUserLoggedOut(BiConsumer<Long, String> consumer) {
//        userLoggedOutConsumer = consumer;
//    }

    //public void onServerStatus


}
