package chat;

import chat.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class ClientMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        //Client client = new Client("localhost", 8080);

        System.out.println("please enter host");
        String host = input.nextLine();
        System.out.println("please enter port");
        int port = input.nextInt();

        Client client = new Client(host, port);

        client.onMessageFromServer(messageFromServer -> {
            LOGGER.info("client << {}", messageFromServer);
        });
        while (true) {
            LOGGER.info("please enter your message");
            String message = new Scanner(System.in).nextLine();
            client.send(message);
        }
    }
}
