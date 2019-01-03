package chat;

import chat.client.Client;
import chat.wrapper.Message;
import client.proto.ChatMessageProto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class ClientMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientMain.class);
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        //Client client = new Client("localhost", 8080);


        System.out.println("please enter your username");
        String username = input.nextLine();
        System.out.println("please enter host");
        String host = input.nextLine();
        System.out.println("please enter port");
        int port = input.nextInt();


        Client client = new Client(host, port, username);

        client.onMessageFromServer(messageFromServer -> {
            LOGGER.info("client << {}", messageFromServer);
        });

        while (true) {
            LOGGER.info("please enter the content for the message");

            String content = input.nextLine();
            Message chatMessage = new Message(System.currentTimeMillis(), username, content);
            client.send(chatMessage);

        }
    }
}
