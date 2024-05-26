package com.mycompany.datagame;

import java.sql.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

public class AddUser extends JFrame {

    JLabel lbl_username;
    JLabel lbl_password;
    JLabel lbl_usertype;
    JTextField txt_username;
    JPasswordField txt_password;
    JRadioButton adminRadioButton;
    JRadioButton playerRadioButton;
    JButton bt_AddUser;
    JButton bt_Back;
    JPanel panel;
    JPanel ButtonPanel;

    AddUser() {

        super("Add User");
        this.setSize(300, 200);

        lbl_username = new JLabel("Username");
        txt_username = new JTextField();
        lbl_password = new JLabel("Password");
        txt_password = new JPasswordField();
        lbl_usertype = new JLabel("User Type");
        bt_AddUser = new JButton("Add User");
        bt_Back = new JButton("Back");
        adminRadioButton = new JRadioButton("Admin");
        playerRadioButton = new JRadioButton("Player", true);
        ButtonGroup userTypeGroup = new ButtonGroup();
        userTypeGroup.add(adminRadioButton);
        userTypeGroup.add(playerRadioButton);

        ButtonPanel = new JPanel(new GridLayout(1, 2));
        ButtonPanel.add(bt_Back);
        ButtonPanel.add(bt_AddUser);

        panel = new JPanel(new GridLayout(5, 2));
        panel.add(lbl_username);
        panel.add(txt_username);
        panel.add(lbl_password);
        panel.add(txt_password);
        panel.add(lbl_usertype);
        panel.add(adminRadioButton);
        panel.add(new JLabel());
        panel.add(playerRadioButton);
        panel.add(new JLabel());
        add(panel, "North");
        add(ButtonPanel, "South");

        bt_AddUser.addActionListener((ActionEvent e) -> {
            String username = txt_username.getText();
            String password = new String(txt_password.getPassword());
            String userType = adminRadioButton.isSelected() ? "admin" : "player";

            if (username.isEmpty() || password.isEmpty()) {
                UIUtil.showError("Please fill in both username and password.");
                return;
            }

            Connection conn = null;
            try {
                conn = DBConnection.getConnection();
                conn.setAutoCommit(false); // Start transaction

                String userQuery = "INSERT INTO dbo.users (username, password, user_type) VALUES (?, ?, ?)";
                try (PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS)) {
                    userStmt.setString(1, username);
                    userStmt.setString(2, password);
                    userStmt.setString(3, userType);
                    userStmt.executeUpdate();

                    try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            String resultQuery = "INSERT INTO dbo.results (user_id, score, level) VALUES (?, 0, 0)";
                            try (PreparedStatement resultStmt = conn.prepareStatement(resultQuery)) {
                                resultStmt.setInt(1, userId);
                                resultStmt.executeUpdate();
                            }
                        } else {
                            throw new SQLException("Creating user failed, no ID obtained.");
                        }
                    }
                }

                conn.commit(); // Commit transaction
                UIUtil.showSuccess("User added successfully!");
            } catch (SQLException ex) {
                if (conn != null) {
                    try {
                        conn.rollback(); // Rollback transaction in case of error
                    } catch (SQLException rollbackEx) {
                        UIUtil.showError("Error during rollback: " + rollbackEx.getMessage());
                    }
                }
                UIUtil.showError("SQL Error: " + ex.getMessage());
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true); // Restore default auto-commit behavior
                        conn.close();
                    } catch (SQLException ex) {
                        UIUtil.showError("Error closing connection: " + ex.getMessage());
                    }
                }
            }
            UIUtil.showFrame(new AdminFrame());
            setVisible(false);
        });

        bt_Back.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new AdminFrame());
            setVisible(false);
        });
    }
}
