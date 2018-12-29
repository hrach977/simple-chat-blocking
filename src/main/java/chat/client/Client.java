package chat.client;

import chat.wrapper.Message;
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
    private final InputStream inputStream;
    private final OutputStream outputStream;
    //private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Thread readerThread;
    private final String username;

    private Consumer<Message> messageConsumer;


    public Client(String host, int port, String username) throws IOException {
        LOGGER.info("initiating a connection with {}:{}", host, port);
        this.socket = new Socket(host, port);
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.username = username;


        this.readerThread = new Thread(() -> {
            LOGGER.info("listening for messages");

            while (true) {
                try {
                    //String messageFromServer = inputStream.readUTF();
                    Message messageFromServer = Message.readFromStream(inputStream);
                    LOGGER.info("<< {}", messageFromServer);
                    if(messageConsumer != null){
                        messageConsumer.accept(messageFromServer);
                    }
                } catch (IOException e) {
                    LOGGER.error("failed to read a message", e);
                    break;
                }
            }
        });
        this.readerThread.start();
    }

    public void send(Message message) throws IOException {
        LOGGER.info(">> " + message);
        message.writeToStream(outputStream);
    }


    public void onMessageFromServer(Consumer<Message> consumer) {
        this.messageConsumer = consumer;
    }

}
