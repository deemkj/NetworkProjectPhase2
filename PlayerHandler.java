/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author deemkj
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PlayerHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;  //(server to player)
    private BufferedReader in; // (player to server)
    private Room room;
    private String playerName;

    public PlayerHandler(Socket socket, Room room) {
        this.socket = socket;
        this.room = room;
        try {
            out = new PrintWriter(socket.getOutputStream(), true); //server sending message to player (server to player)
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            playerName = in.readLine();  // receiving the player's name

            room.addConnectedPlayer(this);  // add player to the main room
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public void sendMessage(String message) { // messages from server to player
        out.println(message);
    }
public String receiveMessage() throws IOException {
    return in.readLine();
}

   @Override
public void run() {
    try {
        String message;
        while ((message = in.readLine()) != null) {
            if (message.equals("ENTER_WAITING_ROOM")) {
                boolean added = room.tryAddToWaitingRoom(this);
                if (!added) {
                    sendMessage("ROOM_FULL");
                }
            } else if (message.startsWith("PLAYER_RESPONSE:")) {
                // تمرير الرد إلى الغرفة لمعالجته من قبل GameLogic
                String response = message.replace("PLAYER_RESPONSE:", "").trim();
                room.handlePlayerResponse(this, response);
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}







}
