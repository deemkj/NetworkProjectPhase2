/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author deemkj
 */
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import javax.swing.SwingUtilities;

public class GameLogic {
    private long startTime; 

    private static final int MAX_SCORE = 10;
    private static final int GAME_DURATION_SECONDS = 120; 
    private static final int ROUND_DELAY_SECONDS = 5; 

    private List<PlayerHandler> players;
    private Map<String, Integer> scores = new HashMap<>();
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean gameRunning = false;
    private boolean responseReceived = false; 

    public GameLogic(List<PlayerHandler> players) {
        this.players = players;
        for (PlayerHandler player : players) {
            scores.put(player.getPlayerName(), 0); // Initialize scores
        }
    }
public void startGame() {
    startTime = System.currentTimeMillis(); 
    gameRunning = true;
    broadcastToAllPlayers("GAME_START");
    broadcastScores();
    System.out.println("Game has started. Players notified.");
    startTimer();
    nextRound();
}

private void startTimer() {
    scheduler.scheduleAtFixedRate(() -> {
        if (gameRunning) {
            int timeLeft = GAME_DURATION_SECONDS - (int) ((System.currentTimeMillis() - startTime) / 1000);
            if (timeLeft <= 0) {
                endGameWithoutWinner();
            } else {
                broadcastToAllPlayers("TIMER:" + timeLeft);
            }
        }
    }, 1, 1, TimeUnit.SECONDS); 

    scheduler.schedule(() -> {
        if (gameRunning) {
            endGameWithoutWinner();
        }
    }, GAME_DURATION_SECONDS, TimeUnit.SECONDS);
}

    
    private void nextRound() {
        if (!gameRunning) return;

        responseReceived = false; 
        currentLetter = (char) ('A' + new Random().nextInt(26)); 
        broadcastToAllPlayers("NEW_LETTER:" + currentLetter);
        System.out.println("New letter: " + currentLetter);

        scheduler.schedule(() -> {
            if (gameRunning) {
                nextRound();
            }
        }, ROUND_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    private void listenForResponses(char correctLetter) {
        for (PlayerHandler player : players) {
            new Thread(() -> {
                try {
                    String response = player.receiveMessage(); 
                    if (gameRunning && !responseReceived && response.equalsIgnoreCase(String.valueOf(correctLetter))) {
                        responseReceived = true; 
                        updateScore(player.getPlayerName());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private void updateScore(String playerName) {
        int newScore = scores.get(playerName) + 1;
        scores.put(playerName, newScore);
        broadcastScores(); 
        broadcastToAllPlayers("SCORE_UPDATE:" + playerName + ":" + newScore);
        System.out.println("Score updated for " + playerName + ": " + newScore);

        if (newScore >= MAX_SCORE) {
            endGameWithWinner(playerName); 
        }
    }

    public  void endGameWithWinner(String winner) {
        gameRunning = false;
        scheduler.shutdownNow();
        String winnerMessage = "GAME_OVER: Winner is " + winner;
        broadcastToAllPlayers(winnerMessage); 
        System.out.println("Game ended. Winner: " + winner);
    }

    public void endGameWithoutWinner() {
        gameRunning = false;
        scheduler.shutdownNow();
        String noWinnerMessage = "GAME_OVER: No winner. Time is up!";
        broadcastToAllPlayers(noWinnerMessage); 
        System.out.println("Game ended. No winner.");
    }

    public  void broadcastScores() {
    StringBuilder scoreMessage = new StringBuilder("SCORES:");
    for (Map.Entry<String, Integer> entry : scores.entrySet()) {
        scoreMessage.append(entry.getKey()).append("=").append(entry.getValue()).append(","); 
    }
    broadcastToAllPlayers(scoreMessage.toString());
}

    private char currentLetter; 

    public void processPlayerResponse(PlayerHandler player, String response) {
        if (!gameRunning) return; 
          

        if (response.equalsIgnoreCase(String.valueOf(currentLetter))) {
            responseReceived = true; 
            updateScore(player.getPlayerName());
            broadcastScores(); 

            System.out.println("Correct input from: " + player.getPlayerName());

      
            scheduler.schedule(() -> {
                if (gameRunning) {
                    nextRound();
                }
            }, ROUND_DELAY_SECONDS, TimeUnit.SECONDS);
        } else {
            System.out.println("Incorrect input: " + response + " (Expected: " + currentLetter + ")");
        }
    }

    private void broadcastToAllPlayers(String message) {
        for (PlayerHandler player : players) {
            player.sendMessage(message);
        }
    }
   public synchronized void removePlayer(PlayerHandler player) {
   
    players.remove(player);
 if (gameRunning) {
        broadcastScores(); 
    }
     scores.remove(player.getPlayerName()); 
    
    broadcastToAllPlayers(player.getPlayerName() + " has left the game.");


    if (players.size() > 1) {
        broadcastScores(); 
    } else if (players.size() == 1) {
        PlayerHandler lastPlayer = players.get(0);
        broadcastToAllPlayers("GAME_OVER: Only one player remains. " + lastPlayer.getPlayerName() + " wins!");
        endGameWithWinner(lastPlayer.getPlayerName());
    } else {
        broadcastToAllPlayers("GAME_OVER: All players have left. Game is over.");
        gameRunning = false;
        scheduler.shutdownNow();
    }
}





   

}
