package com.mycompany.memorygame;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.awt.event.ActionEvent;

public class PlayerFrame extends JFrame {

    JPanel panel;
    JLabel welcomeLabel;
    JButton bt_Start;
    JButton bt_HighScoreList;
    JButton bt_ViewResults;
    JButton bt_Logout;
    Container mainContainer;

    PlayerFrame(String username) {
        super("Player Menu");
        this.setSize(350, 250);

        welcomeLabel = new JLabel("Welcome, " + username + " !");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);

        welcomeLabel.setFont(new Font("Roboto", Font.PLAIN, 20));

        bt_Logout = new JButton("Log Out");
        bt_Start = new JButton("Start Game !");
        bt_ViewResults = new JButton("My Info");
        bt_HighScoreList = new JButton("HighScore List");

        mainContainer = this.getContentPane();
        mainContainer.setLayout(new BorderLayout(10, 10));

        panel = new JPanel(new GridLayout(4, 1, 0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        Font buttonFont = new Font("Roboto", Font.PLAIN, 14);
        bt_Start.setFont(buttonFont);
        bt_ViewResults.setFont(buttonFont);
        bt_HighScoreList.setFont(buttonFont);
        bt_Logout.setFont(buttonFont);

        panel.add(bt_Start);
        panel.add(bt_ViewResults);
        panel.add(bt_HighScoreList);
        panel.add(bt_Logout);

        mainContainer.add(welcomeLabel, BorderLayout.NORTH);
        mainContainer.add(panel, BorderLayout.CENTER);

//        if (!UserController.hasUserRecord(username)) {
//          UserController.createInitialRecord(username);
//    }
        bt_Logout.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new LogIn());
            setVisible(false);
        });

        bt_Start.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new MemoryGame(1, 0, username));
        });

        bt_ViewResults.addActionListener((ActionEvent e) -> {
            int userId = UserController.getUserId(username);
            Player player = UserController.getUserInfo(userId);

            if (player != null) {
                StringBuilder userInfo = new StringBuilder();
                userInfo.append("Username: ").append(player.getUsername()).append("\n");
                userInfo.append("Score: ").append(player.getScore()).append("\n");
                userInfo.append("Level: ").append(player.getLevel()).append("\n");

                JOptionPane.showMessageDialog(this, userInfo.toString(), "User Information", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "User information not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        bt_HighScoreList.addActionListener((ActionEvent e) -> {
            List<Player> topPlayers = UserController.getTopPlayers(5);

            String[] columnNames = {"Username", "Score", "Level"};
            Object[][] data = new Object[topPlayers.size()][3];

            for (int i = 0; i < topPlayers.size(); i++) {
                Player player = topPlayers.get(i);
                data[i][0] = player.getUsername();
                data[i][1] = player.getScore();
                data[i][2] = player.getLevel();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JOptionPane.showMessageDialog(this, scrollPane, "Top Players", JOptionPane.INFORMATION_MESSAGE);
        });

    }
}
