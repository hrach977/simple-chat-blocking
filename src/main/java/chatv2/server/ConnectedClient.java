package chatv2.server;

import chatv2.wrapper.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ConnectedClient {
    private static final Logger log = LoggerFactory.getLogger(ConnectedClient.class);
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private Consumer<MyMessage> consumer;

    public ConnectedClient(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();

        executor.execute(() -> {
            while (true) {
                try {
                    MyMessage messageFromClient = MyMessage.readFromStream(inputStream);
                    log.debug("<< " + messageFromClient);
                    if (consumer != null) {
                        consumer.accept(messageFromClient);
                    }
                } catch (IOException e) {
                    log.error("failed to read the message. Exiting.", e);
                    break;
                }
            }
        });
    }

    public void onMessage(Consumer<MyMessage> consumer) {
        this.consumer = consumer;
    }

    public void send(MyMessage message) throws IOException {
        log.debug(">> " + message);
        message.writeToStream(outputStream);
    }
}
