package com.mycompany.memorygame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class LogIn extends JFrame {

    private Connection conn = null;
    private final JLabel titleLabel;
    private final JLabel lbl_username;
    private final JLabel lbl_password;
    private final JTextField txt_username;
    private final JPasswordField txt_password;
    private final JButton bt_LogIn;
    private final JPanel panel;
    private final JPanel buttonPanel;

    public LogIn() {
        super("Login");
        this.setSize(300, 220);
        this.setLayout(new BorderLayout(10, 15));

        titleLabel = new JLabel("Mind Game");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        lbl_username = new JLabel("Username");
        lbl_password = new JLabel("Password");

        txt_username = new JTextField(10);
        txt_password = new JPasswordField();

        bt_LogIn = new JButton("Log In");

        panel = new JPanel(new GridLayout(4, 2));
        panel.add(lbl_username);
        panel.add(txt_username);
        panel.add(lbl_password);
        panel.add(txt_password);

        buttonPanel = new JPanel();
        buttonPanel.add(bt_LogIn);

        JPanel emptyPanel1 = new JPanel();
        JPanel emptyPanel2 = new JPanel();
        emptyPanel1.setPreferredSize(new Dimension(25, 100));
        emptyPanel2.setPreferredSize(new Dimension(25, 100));
        add(titleLabel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
        add(emptyPanel1, BorderLayout.EAST);
        add(emptyPanel2, BorderLayout.WEST);
        add(buttonPanel, BorderLayout.SOUTH);

        bt_LogIn.addActionListener((ActionEvent e) -> login());

        getRootPane().setDefaultButton(bt_LogIn);
    }

    private void login() {
        String username = txt_username.getText();
        String password = new String(txt_password.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            UIUtil.showError("Please fill in both username and password.");
            return;
        }

        String query = "SELECT user_type FROM users WHERE username=? AND password=?";

        try {
            conn = DBConnection.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String userType = rs.getString("user_type");
                    if ("admin".equalsIgnoreCase(userType)) {
                        UIUtil.showFrame(new AdminFrame());
                    } else if ("player".equalsIgnoreCase(userType)) {
                        UIUtil.showFrame(new PlayerFrame(username));
                    }
                    setVisible(false);
                } else {
                    UIUtil.showError("Invalid username or password.");
                }
            }
        } catch (SQLException ex) {
            UIUtil.showError("SQL Error: " + ex.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                UIUtil.showError("Error closing connection: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        UIUtil.showFrame(new LogIn());
    }
}
