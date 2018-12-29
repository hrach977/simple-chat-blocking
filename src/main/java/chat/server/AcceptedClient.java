package chat.server;

import chat.wrapper.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AcceptedClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptedClient.class);

    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Consumer<Message> consumer;

    public AcceptedClient(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();

       executor.execute(() -> {
            while (true) {
                try {
                    Message messageFromClient = Message.readFromStream(inputStream);
                    LOGGER.info("<< " + messageFromClient);
                    if (consumer != null) {
                        consumer.accept(messageFromClient);
                    }
                } catch (IOException e) {
                    LOGGER.error("failed to read the message. Exiting.");
                    break;
                }
            }

        });
    }

    public void onMessage(Consumer<Message> consumer) {
        this.consumer = consumer;
    }

    public void send(Message message) throws IOException {
        LOGGER.info(">> " + message);
        message.writeToStream(outputStream);
    }
}
