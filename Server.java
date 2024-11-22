/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author deemkj
 */
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 1234;
    private static final String IP = "192.168.100.213"; 
    private Room room = new Room();

    public void start() {
        try {
            InetAddress ipAddress = InetAddress.getByName(IP);
            ServerSocket serverSocket = new ServerSocket(PORT, 50, ipAddress);
            System.out.println("Server started on IP " + IP + " and port " + PORT);

            while (true) { 
                Socket clientSocket = serverSocket.accept();
                PlayerHandler playerHandler = new PlayerHandler(clientSocket, room);
                new Thread(playerHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
