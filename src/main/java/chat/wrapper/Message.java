package chat.wrapper;

//import client.ChatMessageProto;

import client.proto.ChatMessageProto;

import java.io.*;

public class Message {
    private ChatMessageProto.ChatMessage chatMessage;

    public Message(ChatMessageProto.ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }

    public Message(long time, String sender, String content){
        this.chatMessage = ChatMessageProto.ChatMessage.newBuilder()
                .setTime(time)
                .setSender(sender)
                .setContent(content)
                .build();
    }

    public static Message readFromStream(InputStream inputStream) throws IOException {
        return new Message(ChatMessageProto.ChatMessage.parseDelimitedFrom(inputStream));
    }

    public void writeToStream(OutputStream outputStream) throws IOException {
        chatMessage.writeDelimitedTo(outputStream);
    }

    @Override
    public String toString(){
        return chatMessage.toString();
    }
}
