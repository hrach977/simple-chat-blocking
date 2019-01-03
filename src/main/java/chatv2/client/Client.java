package chatv2.client;

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

public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String username;
    private Consumer<MyMessage> messageConsumer;

    public Client(String host, int port, String username) throws IOException {
        log.info("initiating a connection with {}:{}", host, port);
        this.socket = new Socket(host, port);
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.username = username;


        executor.execute(() -> {
            log.info("listening for messages");

            while (true) {
                try {
                    MyMessage messageFromServer = MyMessage.readFromStream(inputStream);
                    log.debug("<< {}", messageFromServer);
                    if(messageConsumer != null){
                        messageConsumer.accept(messageFromServer);
                    }
                } catch (IOException e) {
                    log.error("failed to read a message", e);
                    break;
                }
            }
        });
    }

    public void onMessageFromServer(Consumer<MyMessage> consumer) {
        this.messageConsumer = consumer;
    }

    public void send(MyMessage message) throws IOException {
        log.debug(">> " + message);
        message.writeToStream(outputStream);
    }
}
