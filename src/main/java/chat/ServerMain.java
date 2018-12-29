package chat;

import chat.server.Server;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) throws IOException {
//        Scanner input = new Scanner(System.in);
//        System.out.println("enter port");
//        int port = input.nextInt();
        Server server = new Server(8080);
        server.startAcceptingConnections();
    }
}
