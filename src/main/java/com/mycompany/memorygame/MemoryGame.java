package com.mycompany.memorygame;

import javax.swing.*;
import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryGame extends JFrame {

    private final JPanel panel;
    private final JButton[][] buttons;
    private final List<Integer> sequence = new ArrayList<>();
    private int currentIndex = 0;
    private final int level;
    private final int gridSize;
    private final int minGridSize = 2;
    private final int maxGridSize = 5;
    private final JLabel levelLabel;
    private final JLabel scoreLabel;
    private final JLabel timerLabel;
    private final JButton exitButton;
    private int score = 0;
    private final String username;
    private int timeElapsed = 0;
    private Timer timer;
    private boolean buttonClickable = false;
    private final int totalLevels = 10;
    private final int timeLimit = 30;
    private Color[] colors;
    private final Random random = new Random();

    public MemoryGame(int level, int score, String username) {
        super("Memory Game");
        this.username = username;
        this.level = level;
        this.score = score;
        this.gridSize = Math.min(minGridSize + level - 1, maxGridSize);
        this.setSize(500, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel statPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        JPanel westPanel = new JPanel(new BorderLayout());

        levelLabel = new JLabel("Level: " + level, JLabel.CENTER);
        scoreLabel = new JLabel("Score: " + score, JLabel.CENTER);
        timerLabel = new JLabel("Time: 0s", JLabel.CENTER);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> dispose());

        statPanel.add(levelLabel);
        statPanel.add(scoreLabel);
        statPanel.add(timerLabel);

        westPanel.add(statPanel, BorderLayout.CENTER);
        westPanel.add(exitButton, BorderLayout.SOUTH);
        westPanel.setPreferredSize(new Dimension(120, 100));

        panel = new JPanel(new GridLayout(gridSize, gridSize));
        buttons = new JButton[gridSize][gridSize];

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                JButton button = new JButton();
                button.setBackground(Color.BLACK);
                button.setOpaque(true);
                button.addActionListener(this::handleButtonClick);
                buttons[i][j] = button;
                panel.add(button);
            }
        }

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(panel, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JLabel("Hi "+username+", let's play!", JLabel.CENTER), BorderLayout.NORTH);
        mainPanel.add(westPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);

        generateColors();
        generateSequence();
        startLevelWithDelay();
    }

    private void generateColors() {
        colors = new Color[level + 2];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
    }

    private void generateSequence() {
        sequence.clear();
        int sequenceLength = level + 4;
        for (int i = 0; i < sequenceLength; i++) {
            sequence.add(random.nextInt(gridSize * gridSize));
        }
    }

    private void startLevelWithDelay() {
        disableButtons();
        timer = new Timer(2000, e -> lightUpSequence());
        timer.setRepeats(false);
        timer.start();
    }

    private void lightUpSequence() {
        new Thread(() -> {
            try {
                int delay = 700 / gridSize;
                for (int index : sequence) {
                    int row = index / gridSize;
                    int col = index % gridSize;
                    buttons[row][col].setBackground(colors[random.nextInt(colors.length)]);
                    Thread.sleep(delay);
                    buttons[row][col].setBackground(Color.BLACK);
                    Thread.sleep(300);
                }
                enableButtons();
                startTimer();
            } catch (InterruptedException e) {
                UIUtil.showError("Light Error: " + e.getMessage());
            }
        }).start();
    }

    private void disableButtons() {
        for (JButton[] row : buttons) {
            for (JButton button : row) {
                button.setEnabled(false);
            }
        }
    }

    private void enableButtons() {
        buttonClickable = true;
        for (JButton[] row : buttons) {
            for (JButton button : row) {
                button.setEnabled(true);
            }
        }
    }

    private void handleButtonClick(ActionEvent e) {
        if (!buttonClickable) {
            return;
        }

        JButton button = (JButton) e.getSource();
        int index = getButtonIndex(button);

        if (index == sequence.get(currentIndex)) {
            button.setBackground(Color.WHITE);
            Timer highlightTimer = new Timer(500, evt -> {
                button.setBackground(Color.BLACK);
                currentIndex++;
                if (currentIndex == sequence.size()) {
                    currentIndex = 0;
                    score += (1000 - timeElapsed);
                    showLevelCompleteDialog();
                }
                enableButtons();
            });
            highlightTimer.setRepeats(false);
            highlightTimer.start();
        } else {
            score -= 50;
            scoreLabel.setText("Score: " + score);
            endGame("Game Over! You clicked the wrong button.");
        }

        score += 10;
        scoreLabel.setText("Score: " + score);
    }

    private void showLevelCompleteDialog() {
        disableButtons();
        if (timer != null) {
            timer.stop();
        }
        updateRecordInDatabase();

        int result = JOptionPane.showOptionDialog(this,
                "Congratulations! You completed level " + level + ".\nYour current score: " + score,
                "Level Complete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Continue to Next Level", "Return to Menu"},
                "Continue to Next Level");

        if (result == JOptionPane.YES_OPTION) {
            if (level < totalLevels) {
                UIUtil.showFrame(new MemoryGame(level + 1, score, username));
                dispose();
            } else {
                endGame("Congratulations! You completed all levels.");
            }
        } else {
            dispose();
        }
    }

    private void endGame(String message) {
        disableButtons();
        if (timer != null) {
            timer.stop();
        }
        JOptionPane.showMessageDialog(this, message + "\nYour final score: " + score);
        updateRecordInDatabase();
        dispose();
    }

    private int getButtonIndex(JButton button) {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                if (buttons[i][j] == button) {
                    return i * gridSize + j;
                }
            }
        }
        return -1;
    }

    private void startTimer() {
        timer = new Timer(1000, e -> {
            timeElapsed++;
            timerLabel.setText("Time: " + timeElapsed + "s");
            if (timeElapsed >= timeLimit) {
                endGame("Time's up! Game Over.");
            }
        });
        timer.start();
    }

    private void updateRecordInDatabase() {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement getStatement = connection.prepareStatement(
                "SELECT score, level FROM results WHERE user_id = ?"); PreparedStatement updateStatement = connection.prepareStatement(
                        "UPDATE results SET score = ?, level = ? WHERE user_id = ?")) {

            int userId = UserController.getUserId(username);
            getStatement.setInt(1, userId);
            ResultSet resultSet = getStatement.executeQuery();

            int currentScore = 0;
            int currentLevel = 0;

            if (resultSet.next()) {
                currentScore = resultSet.getInt("score");
                currentLevel = resultSet.getInt("level");
            }

            int updatedScore = currentScore + score;
            int updatedLevel = Math.max(currentLevel, level);

            updateStatement.setInt(1, updatedScore);
            updateStatement.setInt(2, updatedLevel);
            updateStatement.setInt(3, userId);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            UIUtil.showError("Error updating user record: " + e.getMessage());
        }
    }
}
