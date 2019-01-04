package chatv2.server;

import chatv2.wrapper.MyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
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
    //private final String username;

//    public void setNickname(String username) {
//        this.username = username;
//    }

    public ConnectedClient(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();

        executor.execute(() -> {
            try {
                boolean flag = true;
                while (flag) {
                    try {
//                        if (socket.isClosed()) {
//                            flag = false;
//                        }
                        MyMessage messageFromClient = MyMessage.readFromStream(inputStream);
//                    if (messageFromClient == null) {
//                        send(MyMessage.userLoggedOut(System.currentTimeMillis(), "bgo"));
//                        break;
//                    }
                        // if (socket.isClosed())
//                        if (messageFromClient.getContent().equalsIgnoreCase("bye")) {
//                            send(MyMessage.userLoggedOut(System.currentTimeMillis(), messageFromClient.getUsername()));
//                            break;
//                        }

                        log.debug("<< " + messageFromClient);
                        if (messageFromClient.getContent().equalsIgnoreCase("bye")) {
                           // break; // the loop still continues  WHY?????
                            flag = false;
                            break;
                        }
                        if (consumer != null  && !socket.isClosed()) {  //check also socket.isClosed
                            consumer.accept(messageFromClient);
                        } //else {
//                            break;
//                        }
                    } catch (IOException e) {
                        log.error("failed to read the message. Exiting.", e);
                        consumer.accept(MyMessage.failureMessage(System.currentTimeMillis(), e.getMessage()));
                        break;

                    }
                }
            } finally {
                closeResources(inputStream, outputStream, socket);
            }
        });
    }

    public void closeResources(Closeable... resources) {
        System.out.println("CLOSING THE RESOURCES");
        for (Closeable resource : resources) {
            try {
                resource.close();
            } catch (IOException e) {
                log.error("failed while closing", e);
            }
        }
    }

    public void onMessage(Consumer<MyMessage> consumer) {
        this.consumer = consumer;
    }

    public void send(MyMessage message) throws IOException {
        log.debug(">> " + message);
        message.writeToStream(outputStream);
    }

    public Socket getSocket() {
        return socket;
    }
}
