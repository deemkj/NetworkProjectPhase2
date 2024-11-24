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
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;

public class GameGUI extends JFrame {
    private JTextArea playersAreaMain;   // to display players in the main room
    private JTextArea playersAreaWaiting; // to display players in the waiting room
    private JLabel gameLetterLabel;
    private JTextField playerInputField;
    private JTextArea scoresArea;
    private JLabel timerLabel; // Label to show the countdown timer
    private JButton leaveButton;
    private JTextArea playersAreaGame; 



    private String playerName;
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket;

    private static final String SERVER_IP = "192.168.100.227";
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
    playersAreaGame = createPlayersArea(); 
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
 
    Font pixelFontLarge = loadPixelFont(48f); 
    Font pixelFontMedium = loadPixelFont(30f); 


    JPanel mainRoomPanel = createBackgroundPanel("/Users/deemkj/Documents/GitHub/NetworkProjectPhase2/src/main/java/StartPage.png");
    mainRoomPanel.setLayout(null);  


    JButton playButton = new JButton("");
    playButton.setBounds(290, 300, 1000, 500);  

 
    playButton.setOpaque(false);
    playButton.setContentAreaFilled(false);
    playButton.setBorderPainted(false);
    playButton.setFocusPainted(false);

   
    playButton.addActionListener(e -> sendEnterWaitingRoomRequest());

    mainRoomPanel.add(playButton);


    playersAreaMain.setFont(pixelFontMedium); 
    playersAreaMain.setForeground(new Color(113, 60, 130)); 
    playersAreaMain.setOpaque(false); 
    playersAreaMain.setBorder(null);

 
    JScrollPane mainScrollPane = new JScrollPane(playersAreaMain);
    mainScrollPane.setBounds(100, 290, 350, 170); 
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

  
    Font pixelFontLarge = loadPixelFont(48f);
    Font pixelFontMedium = loadPixelFont(30f); 


    JPanel waitingRoomPanel = createBackgroundPanel("/Users/deemkj/Documents/GitHub/NetworkProjectPhase2/src/main/java/WaitingRoom.png");
    waitingRoomPanel.setLayout(null);


    playersAreaWaiting.setFont(pixelFontMedium); 
    playersAreaWaiting.setOpaque(false); 
    playersAreaWaiting.setForeground(new Color(113, 60, 130)); 
    playersAreaWaiting.setBorder(null); 

  
    JScrollPane waitingScrollPane = new JScrollPane(playersAreaWaiting);
    waitingScrollPane.setBounds(96, 300, 350, 400); 
    waitingScrollPane.setOpaque(false);
    waitingScrollPane.getViewport().setOpaque(false);
    waitingScrollPane.setBorder(null);

 
    timerLabel = new JLabel("Game will start soon");
    timerLabel.setFont(pixelFontMedium); 
    timerLabel.setForeground(Color.BLACK); 
    timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    timerLabel.setBounds(500, -3, 400, 50); 


    waitingRoomPanel.add(waitingScrollPane);
    waitingRoomPanel.add(timerLabel);


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

   
    Font pixelFontVeryLarge = loadPixelFont(60f);
    Font pixelFontLarge = loadPixelFont(48f); 
    Font pixelFontSmall = loadPixelFont(27f); 
    Font pixelFontVerySmall = loadPixelFont(18f);


    JPanel backgroundPanel = createBackgroundPanel("/Users/deemkj/Documents/GitHub/NetworkProjectPhase2/src/main/java/GameArea.png"); 
    backgroundPanel.setLayout(null); 


    gameLetterLabel = new JLabel("Type this letter: W");
    gameLetterLabel.setFont(pixelFontVeryLarge);
    gameLetterLabel.setHorizontalAlignment(SwingConstants.CENTER);
    gameLetterLabel.setBounds(390, 400, 600, 50); 
    gameLetterLabel.setForeground(new Color(113, 60, 130));
   

    timerLabel = new JLabel("Time left: 106 seconds");
    timerLabel.setFont(pixelFontSmall);
    timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
    timerLabel.setBounds(490, 20, 400, 30);

    playersAreaGame = new JTextArea();
    playersAreaGame.setEditable(false);
    playersAreaGame.setFont(pixelFontSmall); 
    playersAreaGame.setOpaque(false); 
    playersAreaGame.setForeground(new Color(113, 60, 130)); 
    playersAreaGame.setBorder(null);

    JScrollPane gamePlayersScrollPane = new JScrollPane(playersAreaGame);
    gamePlayersScrollPane.setOpaque(false);
    gamePlayersScrollPane.getViewport().setOpaque(false); 
    gamePlayersScrollPane.setBorder(null); 
    gamePlayersScrollPane.setBounds(90, 130, 200, 400);


    playerInputField = new JTextField();
    playerInputField.setFont(pixelFontSmall);
    playerInputField.setBounds(490, 710, 400, 60); 
    playerInputField.addActionListener(e -> {
        String input = playerInputField.getText().trim();
        if (!input.isEmpty()) {
            out.println("PLAYER_RESPONSE:" + input); 
            playerInputField.setText(""); 
        }
    });


    JButton leaveButton = new JButton("Leave");
    leaveButton.setFont(pixelFontVerySmall); 
    leaveButton.setBounds(1200, 700, 100, 40); 
    leaveButton.addActionListener(e -> {
        out.println("LEAVE_GAME");
        dispose();
    });

 
    backgroundPanel.add(timerLabel);
    backgroundPanel.add(gameLetterLabel);
    backgroundPanel.add(gamePlayersScrollPane);
    backgroundPanel.add(playerInputField);
    backgroundPanel.add(leaveButton);

  
    setContentPane(backgroundPanel);
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
    SwingUtilities.invokeLater(() -> playersAreaGame.setText(scores)); 

 if (scoresArea != null) {
                    scoresArea.setText(scores);
                }
                
                
            } else if (response.startsWith("TIMER:")) {
                int secondsLeft = Integer.parseInt(response.replace("TIMER:", "").trim());
                updateTimer(secondsLeft);
            } else if (response.startsWith("WTIMER:")) {
                int secondsLeft = Integer.parseInt(response.replace("WTIMER:", "").trim());
                updateWaitingRoomTimer(secondsLeft);
            
                
                
           
            }else if (response.startsWith("GAME_OVER:")) {
    String winnerName = response.replace("GAME_OVER:", "").trim();
    SwingUtilities.invokeLater(() -> showWinnerScreen(winnerName));
}

            
            
            else if (response.equals("ROOM_FULL")) {
                JOptionPane.showMessageDialog(this, 
                        "Waiting room is full. Please wait.", 
                        "Room Full", JOptionPane.WARNING_MESSAGE);
            } else if (response.startsWith("GAME_PLAYERS_UPDATE:")) {
    String formattedResponse = response.replace("GAME_PLAYERS_UPDATE:", "").trim().replace(",", "\n");
    SwingUtilities.invokeLater(() -> playersAreaGame.setText(formattedResponse));

            }
           else if (response.startsWith("LEAVE_GAME:")) {
    String playerName = response.replace("LEAVE_GAME:", "").trim();
    SwingUtilities.invokeLater(() -> {
     
        String updatedPlayers = playersAreaGame.getText().replaceAll(playerName + "=\\d+\\n?", "");
  
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


  

/////////////////////////////////////////WINNER CODE //////////////////////////
    private void showWinnerScreen(String winnerName) {
  
    JPanel winnerPanel = createBackgroundPanel("/Users/deemkj/Documents/GitHub/NetworkProjectPhase2/src/main/java/Winner.png"); // ضع المسار الصحيح لصورة الفائز

    winnerPanel.setLayout(null); 


  
    JLabel winnerNameLabel = new JLabel(winnerName);
    winnerNameLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
    winnerNameLabel.setForeground(Color.YELLOW); 
    winnerNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
    winnerNameLabel.setBounds(450, 590, 500, 50); 


    winnerPanel.add(winnerNameLabel);

 
    setContentPane(winnerPanel);
    revalidate();
    repaint();
}

///////////////////////////////////// Font code//////////////////////////
    private Font loadPixelFont(float size) {
    try {
     
        File fontFile = new File("/Users/deemkj/Documents/GitHub/NetworkProjectPhase2/src/main/java/Jersey10-Regular.ttf");
        Font pixelFont = Font.createFont(Font.TRUETYPE_FONT, fontFile);
        return pixelFont.deriveFont(size); 
    } catch (FontFormatException | IOException e) {
        e.printStackTrace();
        return new Font("SansSerif", Font.PLAIN, (int) size); 
    }
}

private void applyPixelFont() {
    Font pixelFont = loadPixelFont(24f); 
    gameLetterLabel.setFont(pixelFont); 
    timerLabel.setFont(pixelFont); 
    playersAreaGame.setFont(pixelFont); 
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
