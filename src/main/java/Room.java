/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author deemkj
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Room {
    private static final int MAX_Players = 4;
    private List<PlayerHandler> waitingRoom = new ArrayList<>();
    private List<PlayerHandler> mainRoomPlayers = new ArrayList<>();
    private boolean gameStarted = false;
    private GameLogic gameLogic;
    private ScheduledExecutorService timerScheduler;
    private boolean timerStarted = false; 
    private Map<String, Integer> scores = new HashMap<>();


    public synchronized void addConnectedPlayer(PlayerHandler player) {
        mainRoomPlayers.add(player);
        updateAllConnectedPlayers();
    }

    public synchronized boolean tryAddToWaitingRoom(PlayerHandler player) {
    if (waitingRoom.size() < MAX_Players) {
        waitingRoom.add(player);
        scores.put(player.getPlayerName(), 0);
        updateWaitingRoomPlayers();

        if (waitingRoom.size() == MAX_Players && !gameStarted) {
            startGame();
        } else if (waitingRoom.size() >= 2 && !gameStarted && !timerStarted) {
            startTimer();
        }

        return true;
    } else {
        player.sendMessage("ROOM_FULL");
        return false;
    }
}

    private void startTimer() {
        timerScheduler = Executors.newScheduledThreadPool(1);
        timerStarted = true;
        timerScheduler.scheduleAtFixedRate(new Runnable() {
            int timeLeft = 30;

            @Override
            public void run() {
                if (timeLeft > 0 && !gameStarted) {
                    broadcastToRoom(waitingRoom, "WTIMER:" + timeLeft);
                    timeLeft--;
                } else {
                    timerScheduler.shutdownNow();
                    if (!gameStarted && waitingRoom.size() >= 2) {
                        startGame();
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void startGame() {
    gameStarted = true;
    if (timerScheduler != null && !timerScheduler.isShutdown()) {
        timerScheduler.shutdownNow(); 
    }
    gameLogic = new GameLogic(new ArrayList<>(waitingRoom)); 
    broadcastToRoom(waitingRoom, "GAME_PLAYERS_UPDATE:" + getPlayersList(waitingRoom));
    new Thread(gameLogic::startGame).start();
}

    public void handlePlayerResponse(PlayerHandler player, String response) {
        if (gameLogic != null) {
            gameLogic.processPlayerResponse(player, response);
        }
    }

    private void updateAllConnectedPlayers() {
        StringBuilder playerList = new StringBuilder("MAIN_ROOM: ");
        for (PlayerHandler player : mainRoomPlayers) {
            playerList.append(player.getPlayerName()).append(",");
        }
        broadcastToAllPlayers(playerList.toString());
    }

    private void updateWaitingRoomPlayers() {
         if (gameStarted) {
        return; 
    }
        StringBuilder playerList = new StringBuilder("WAITING_ROOM: ");
        for (PlayerHandler player : waitingRoom) {
            playerList.append(player.getPlayerName()).append(",");
        }
        broadcastToRoom(waitingRoom, playerList.toString());
    }

    private void broadcastToAllPlayers(String message) {
        for (PlayerHandler player : mainRoomPlayers) {
            player.sendMessage(message);
        }
    }

    private void broadcastToRoom(List<PlayerHandler> room, String message) {
        for (PlayerHandler player : room) {
            player.sendMessage(message);
        }
    }
    ///////////////////////////
  
    
    
   public synchronized void removePlayerFromGame(PlayerHandler player) {
      
    
    if (waitingRoom.contains(player)) {
        waitingRoom.remove(player);
        scores.remove(player.getPlayerName()); 
          broadcastScores();
          gameLogic.broadcastScores(); 
        updateWaitingRoomPlayers();
    }

    if (mainRoomPlayers.contains(player)) {
        mainRoomPlayers.remove(player);
        updateAllConnectedPlayers();
    }

    if (gameLogic != null) {
        gameLogic.removePlayer(player);
              
    }

    System.out.println("Player " + player.getPlayerName() + " left the game.");
    System.out.println("Remaining players in waiting room: " + waitingRoom.size());
    System.out.println("Remaining players in main room: " + mainRoomPlayers.size());


    broadcastToAllPlayers("PLAYER_LEFT:" + player.getPlayerName());
    broadcastToRoom(waitingRoom, "GAME_PLAYERS_UPDATE:" + getPlayersList(waitingRoom));

    if (waitingRoom.size() == 1 && gameStarted) {
        PlayerHandler lastPlayer = waitingRoom.get(0);
        broadcastToAllPlayers("GAME_OVER: Only one player remains. " + lastPlayer.getPlayerName() + " wins!");
        gameLogic.endGameWithWinner(lastPlayer.getPlayerName());
        waitingRoom.clear();
        gameStarted = false;
    } else if (waitingRoom.isEmpty() && gameStarted) {
        broadcastToAllPlayers("GAME_OVER: All players have left. Game stopped.");
        gameLogic.endGameWithoutWinner();
        gameStarted = false;
    }
}

private String getPlayersList(List<PlayerHandler> players) {
    if (players.isEmpty()) {
        return ""; 
    }
    
    StringBuilder playerList = new StringBuilder();
    for (PlayerHandler player : players) {
        playerList.append(player.getPlayerName()).append(",");
    }
    

    return playerList.substring(0, playerList.length() - 1);
}
public void broadcastScores() {
    StringBuilder scoresMessage = new StringBuilder("SCORES:");
    for (PlayerHandler player : waitingRoom) {
        String playerName = player.getPlayerName();
        int score = scores.getOrDefault(playerName, 0); 
        scoresMessage.append(playerName)
                     .append("=")
                     .append(score)
                     .append(",");
    }
    broadcastToRoom(waitingRoom, scoresMessage.toString());
}





}