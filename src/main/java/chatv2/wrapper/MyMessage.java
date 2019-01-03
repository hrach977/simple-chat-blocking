package chatv2.wrapper;

import clientv2.proto.ChatMessage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MyMessage {
    private ChatMessage.ChatMsg chatMsg;

    public MyMessage(ChatMessage.ChatMsg chatMsg) {
        this.chatMsg = chatMsg;
    }

    public static ChatMessage.ChatMsg serverStatus(long time, String serverStatus) {
        return ChatMessage.ChatMsg.newBuilder()
                .setTime(time)
                .setServerStatus(
                        ChatMessage.ChatMsg.ServerStatus.newBuilder()
                                .setStatus(serverStatus)
                                .build()
                )
                .build();
    }

    public static ChatMessage.ChatMsg userLoggedIn(long time, String username) {
        return ChatMessage.ChatMsg.newBuilder()
                .setTime(time)
                .setUserLoggedIn(
                        ChatMessage.ChatMsg.UserLoggedIn.newBuilder()
                                .setUserName(username)
                                .build()
                )
                .build();
    }

    public static ChatMessage.ChatMsg userLoggedOut(long time, String username) {
        return ChatMessage.ChatMsg.newBuilder()
                .setTime(time)
                .setUserLoggedOut(
                        ChatMessage.ChatMsg.UserLoggedOut.newBuilder()
                        .setUserName(username)
                        .build()
                ).build();
    }

    public static ChatMessage.ChatMsg userSentGlobalMessage(long time, String username, String message) {
        return ChatMessage.ChatMsg.newBuilder()
                .setTime(time)
                .setUserSentGlobalMessage(
                        ChatMessage.ChatMsg.UserSentGlobalMessage.newBuilder()
                                .setUserName(username)
                                .setMessage(message)
                                .build()
                ).build();
    }

    public static MyMessage readFromStream(InputStream inputStream) throws IOException {
        return new MyMessage(ChatMessage.ChatMsg.parseDelimitedFrom(inputStream));
    }

    public void writeToStream(OutputStream outputStream) throws IOException {
        chatMsg.writeDelimitedTo(outputStream);
    }

    @Override
    public String toString() {
        return chatMsg.toString();
    }
}
