package chat.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class Client {
    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Consumer<String> messageConsumer;


    public Client(String host, int port) throws IOException {
        LOGGER.info("initiating a connection with {}:{}", host, port);
        this.socket = new Socket(host, port);
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());


        executor.execute(() -> {
          //  new ClientReader()
            LOGGER.info("listening for messages");

            while (true) {
                try {
                    String messageFromServer = inputStream.readUTF();
                    LOGGER.debug("<< {}", messageFromServer);
                    if(messageConsumer != null){
                        messageConsumer.accept(messageFromServer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void send(String message) throws IOException {
        LOGGER.debug(">> " + message);
        outputStream.writeUTF(message);
    }


    public void onMessageFromServer(Consumer<String> consumer) {
        this.messageConsumer = consumer;
    }


}
