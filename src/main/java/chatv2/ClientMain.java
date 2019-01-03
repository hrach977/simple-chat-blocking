package chatv2;

import chatv2.client.Client;
import chatv2.wrapper.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class ClientMain {
    private static final Logger log = LoggerFactory.getLogger(ClientMain.class);
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 8080, "alice");

        try {
            //Client client = new Client("localhost", 8080, "alice");

            client.onMessageFromServer(messageFromServer -> {
                log.info("client << {}", messageFromServer);
            });

            client.send(MyMessage.userLoggedIn(System.currentTimeMillis(), client.getUsername()));

            while (true) {
                log.info("please enter the content for the message");

                String content = input.nextLine();
                MyMessage chatMessage = MyMessage.userSentGlobalMessage(System.currentTimeMillis(), client.getUsername(), content);
                client.send(chatMessage);

            }
        } finally {
            client.send(MyMessage.userLoggedOut(System.currentTimeMillis(), client.getUsername()));
        }
    }
}
