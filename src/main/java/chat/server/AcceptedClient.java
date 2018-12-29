package chat.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AcceptedClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcceptedClient.class);

    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private Consumer<String> consumer;

    public AcceptedClient(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());

        executor.execute(() -> {
            while (true) {
                try {
                    String messageFromClient = inputStream.readUTF();
                    LOGGER.debug("<< " + messageFromClient);
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

    public void onMessage(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    public void send(String message) throws IOException {
        LOGGER.debug(">> " + message);
        outputStream.writeUTF(message);
    }
}
