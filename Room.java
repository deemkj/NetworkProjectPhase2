/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author deemkj
 */
import java.util.ArrayList;
import java.util.List;
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
    private boolean timerStarted = false; // لتتبع ما إذا كان التايمر قد بدأ بالفعل

    public synchronized void addConnectedPlayer(PlayerHandler player) {
        mainRoomPlayers.add(player);
        updateAllConnectedPlayers();
    }

    public synchronized boolean tryAddToWaitingRoom(PlayerHandler player) {
        if (waitingRoom.size() < MAX_Players) {
            waitingRoom.add(player);
            updateWaitingRoomPlayers();

            if (waitingRoom.size() == MAX_Players && !gameStarted) {
                // ابدأ اللعبة فورًا عند اكتمال عدد اللاعبين إلى 4
                startGame();
            } else if (waitingRoom.size() >= 2 && !gameStarted && !timerStarted) {
                // ابدأ التايمر عند وجود لاعبين اثنين أو أكثر ولم تبدأ اللعبة بعد
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
            timerScheduler.shutdownNow(); // أوقف التايمر إذا كانت اللعبة ستبدأ
        }
        gameLogic = new GameLogic(new ArrayList<>(waitingRoom));
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
}
