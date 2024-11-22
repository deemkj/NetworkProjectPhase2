/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

/**
 *
 * @author deemkj
 */
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameGUI extends JFrame {
    private JTextArea playersAreaMain;   // to display players in the main room
    private JTextArea playersAreaWaiting; // to display players in the waiting room
    private JLabel gameLetterLabel;
    private JTextField playerInputField;
    private JTextArea scoresArea;
    private JLabel timerLabel; // Label to show the countdown timer
    private JButton leaveButton;
    private JTextArea playersAreaGame; // TextArea لعرض اللاعبين أثناء اللعبة



    private String playerName;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private static final String SERVER_IP = "192.168.100.213";
    private static final int SERVER_PORT = 1234;

    public GameGUI(String playerName) {
        this.playerName = playerName;

        setTitle("Main Room");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setupPlayersAreas();
        showMainRoom();  // display the main room on startup
        connectToServer();
    }

   private void setupPlayersAreas() {
    playersAreaMain = createPlayersArea();
    playersAreaWaiting = createPlayersArea();
    playersAreaGame = createPlayersArea(); // TextArea جديدة لقائمة اللاعبين في اللعبة
}

private JTextArea createPlayersArea() {
    JTextArea area = new JTextArea();
    area.setEditable(false);
    area.setFont(new Font("SansSerif", Font.PLAIN, 18));
    area.setOpaque(false);
    area.setForeground(new Color(255, 77, 126));
    area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    return area;
}

    
    

    // provide the path to the background image from your local device
    private void showMainRoom() {
        JPanel mainRoomPanel = createBackgroundPanel("/Users/deemkj/Documents/Java 2/phase2/testt22/src/main/java/background.png");
        mainRoomPanel.setLayout(null);  // disable layout to adjust elements manually

        // create a Play (start) button
        JButton playButton = new JButton("");
        playButton.setBounds(450, 500, 400, 150);  // set the button's position and size

       // set the button to be transparent with the background
        playButton.setOpaque(false);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);
        playButton.setFocusPainted(false);

        // link the button to enter the waiting room
        playButton.addActionListener(e -> sendEnterWaitingRoomRequest());

        // add the button to the main panel
        mainRoomPanel.add(playButton);

        JScrollPane mainScrollPane = new JScrollPane(playersAreaMain);
        mainScrollPane.setBounds(70, 270, 350, 170);
        mainScrollPane.setOpaque(false);
        mainScrollPane.getViewport().setOpaque(false);
        mainScrollPane.setBorder(null);

        mainRoomPanel.add(mainScrollPane);
        setContentPane(mainRoomPanel);

        revalidate(); 
        repaint();     
    }

    private void enterWaitingRoom() {
    setTitle("Waiting Room");

    // provide the path to the background image (WaitingRoom) from your local device
    JPanel waitingRoomPanel = createBackgroundPanel("/Users/deemkj/Documents/Java 2/phase2/testt22/src/main/java/WaitingRoom.png");
    waitingRoomPanel.setLayout(null);

    // Set up the area to display players in the waiting room
    JScrollPane waitingScrollPane = new JScrollPane(playersAreaWaiting);
    waitingScrollPane.setBounds(96, 300, 350, 400); // Adjust bounds to fit inside the grey rectangle
    waitingScrollPane.setOpaque(false);
    waitingScrollPane.getViewport().setOpaque(false);
    waitingScrollPane.setBorder(null);

    // Set up the timer label
    timerLabel = new JLabel("Game will start soon");
    timerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    timerLabel.setForeground(Color.BLACK); // Change to black for better visibility
    timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    timerLabel.setBounds(500, -3, 400, 50); // Adjust position and size as needed

    
    // إضافة زر Leave
   /* JButton leaveButton = new JButton("Leave");
    leaveButton.setBounds(500, 500, 150, 50); // تعديل الموضع حسب التصميم
    leaveButton.addActionListener(e -> {
        out.println("LEAVE_WAITING_ROOM"); // إرسال رسالة LEAVE إلى السيرفر
        dispose(); // إغلاق واجهة اللعبة
         });*/
        
    // Add components to the panel
    waitingRoomPanel.add(waitingScrollPane);
    waitingRoomPanel.add(timerLabel);
   // waitingRoomPanel.add(leaveButton); // إضافة زر "Leave"

    setContentPane(waitingRoomPanel);

    revalidate();
    repaint();
}

    private JPanel createBackgroundPanel(String imagePath) {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image background = Toolkit.getDefaultToolkit().getImage(imagePath);
                    g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(playerName);  // send the player's name to the server

            new Thread(this::listenToServer).start();
        } catch (IOException e) {
            playersAreaMain.append("Failed to connect to the server: " + e.getMessage() + "\n");
        }
    }

    private void showGameScreen() {
    setTitle("Game Screen");
    JPanel gamePanel = new JPanel();
    gamePanel.setLayout(new BorderLayout());

    gameLetterLabel = new JLabel("Get ready...");
    gameLetterLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
    gameLetterLabel.setHorizontalAlignment(SwingConstants.CENTER);

    timerLabel = new JLabel("Time left: 180"); // Initialize timer label
    timerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    timerLabel.setHorizontalAlignment(SwingConstants.CENTER);

   /* scoresArea = new JTextArea();
    scoresArea.setEditable(false);
    scoresArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
    scoresArea.setOpaque(false);
    scoresArea.setForeground(Color.BLACK);
    scoresArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));*/

    JScrollPane gamePlayersScrollPane = new JScrollPane(playersAreaGame); // إضافة TextArea جديدة
    gamePlayersScrollPane.setBounds(10, 10, 200, 200); // تحديد الموقع والحجم

    playerInputField = new JTextField();
    playerInputField.setFont(new Font("SansSerif", Font.PLAIN, 24));
    playerInputField.addActionListener(e -> {
        String input = playerInputField.getText().trim();
        if (!input.isEmpty()) {
            out.println("PLAYER_RESPONSE:" + input); // إرسال المدخلات إلى السيرفر
            playerInputField.setText(""); // مسح حقل الإدخال بعد الإرسال
        }
    });

    JButton leaveButton = new JButton("Leave");
    leaveButton.setBounds(600, 500, 100, 50);
    leaveButton.addActionListener(e -> {
        out.println("LEAVE_GAME");
        dispose();
    });

    gamePanel.add(leaveButton);
    gamePanel.add(timerLabel, BorderLayout.NORTH);
    gamePanel.add(gameLetterLabel, BorderLayout.CENTER);
    ///gamePanel.add(new JScrollPane(scoresArea), BorderLayout.EAST);
    gamePanel.add(playerInputField, BorderLayout.SOUTH);
    gamePanel.add(gamePlayersScrollPane, BorderLayout.WEST); // إضافة TextArea الجديدة إلى الواجهة

    setContentPane(gamePanel);
    revalidate();
    repaint();
}

   private void listenToServer() {
    try {
        String response;
        while ((response = in.readLine()) != null) {
            if (response.startsWith("MAIN_ROOM:")) {
                String formattedResponse = response.replace("MAIN_ROOM:", "").trim().replace(",", "\n");
                playersAreaMain.setText(formattedResponse);
            } 
             else if (response.startsWith("LEAVE_GAME:")) {
    String playerName = response.replace("LEAVE_GAME:", "").trim();
    SwingUtilities.invokeLater(() -> {
        String updatedPlayers = playersAreaWaiting.getText().replace(playerName + "\n", "");
        playersAreaWaiting.setText(updatedPlayers);
    });
}

            
            else if (response.startsWith("WAITING_ROOM:")) {
                enterWaitingRoom();
                String formattedResponse = response.replace("WAITING_ROOM:", "").trim().replace(",", "\n");
                playersAreaWaiting.setText(formattedResponse);
            } else if (response.equals("GAME_START")) {
                showGameScreen();
            } else if (response.startsWith("NEW_LETTER:")) {
                String letter = response.replace("NEW_LETTER:", "").trim();
                if (gameLetterLabel != null) {
                    gameLetterLabel.setText("Type this letter: " + letter);
                }}
           else if (response.startsWith("SCORES:")) {
    String scores = response.replace("SCORES:", "").trim().replace(",", "\n");
    SwingUtilities.invokeLater(() -> playersAreaGame.setText(scores)); // عرض النقاط



                if (scoresArea != null) {
                    scoresArea.setText(scores);
                }
                
                
            } else if (response.startsWith("TIMER:")) {
                int secondsLeft = Integer.parseInt(response.replace("TIMER:", "").trim());
                updateTimer(secondsLeft);
            } else if (response.startsWith("WTIMER:")) {
                int secondsLeft = Integer.parseInt(response.replace("WTIMER:", "").trim());
                updateWaitingRoomTimer(secondsLeft);
            } else if (response.startsWith("GAME_OVER:")) {
                String winnerMessage = response.replace("GAME_OVER:", "").trim();
                JOptionPane.showMessageDialog(this, winnerMessage, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else if (response.equals("ROOM_FULL")) {
                JOptionPane.showMessageDialog(this, 
                        "Waiting room is full. Please wait.", 
                        "Room Full", JOptionPane.WARNING_MESSAGE);
            } else if (response.startsWith("GAME_PLAYERS_UPDATE:")) {
    String formattedResponse = response.replace("GAME_PLAYERS_UPDATE:", "").trim().replace(",", "\n");
    SwingUtilities.invokeLater(() -> playersAreaGame.setText(formattedResponse));

            }
           else if (response.startsWith("LEAVE_GAME:")) {
    String playerName1 = response.replace("LEAVE_GAME:", "").trim();
    SwingUtilities.invokeLater(() -> {
        // حذف اللاعب من القائمة
        String updatedPlayers = playersAreaGame.getText().replaceAll(playerName1 + "=\\d+\\n?", "");
       
        playersAreaGame.setText(updatedPlayers);

        // طلب تحديث النقاط المتبقية
        out.println("REQUEST_SCORES_UPDATE");
    });
}




        }
    } catch (IOException e) {
        playersAreaMain.append("Connection to server lost: " + e.getMessage() + "\n");
    }
}

    private void updateTimer(int secondsLeft) {
        SwingUtilities.invokeLater(() -> timerLabel.setText("Time left: " + secondsLeft + " seconds"));
    }
    
    private void updateWaitingRoomTimer(int secondsLeft) {
    SwingUtilities.invokeLater(() -> timerLabel.setText("Game will start in " + secondsLeft + " seconds"));
}


    private void sendEnterWaitingRoomRequest() {
        out.println("ENTER_WAITING_ROOM");  
    }


  









    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
        
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String playerName = JOptionPane.showInputDialog(null, "Enter your name:", 
                    "Player Name", JOptionPane.PLAIN_MESSAGE);
            if (playerName != null && !playerName.trim().isEmpty()) {
                new GameGUI(playerName).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Player name cannot be empty!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
