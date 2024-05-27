package com.mycompany.memorygame;

import java.sql.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

public class AddUser extends JFrame {

    private JLabel lbl_username;
    private JLabel lbl_password;
    private JLabel lbl_usertype;
    private JTextField txt_username;
    private JPasswordField txt_password;
    private JRadioButton adminRadioButton;
    private JRadioButton playerRadioButton;
    private JButton bt_AddUser;
    private JButton bt_Back;
    private JPanel panel;
    private JPanel buttonPanel;

    public AddUser() {
        super("Add User");
        this.setSize(300, 200);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        initializeComponents();
        setupLayout();
        addEventListeners();
    }

    private void initializeComponents() {
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
    }

    private void setupLayout() {
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

        buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(bt_Back);
        buttonPanel.add(bt_AddUser);

        add(panel, "North");
        add(buttonPanel, "South");
    }

    private void addEventListeners() {
        bt_AddUser.addActionListener((ActionEvent e) -> addUser());
        bt_Back.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new AdminFrame());
            setVisible(false);
        });
    }

    private void addUser() {
        String username = txt_username.getText();
        String password = new String(txt_password.getPassword());
        String userType = adminRadioButton.isSelected() ? "admin" : "player";

        if (username.isEmpty() || password.isEmpty()) {
            UIUtil.showError("Please fill in both username and password.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

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

            conn.commit();
            UIUtil.showSuccess("User added successfully!");
            UIUtil.showFrame(new AdminFrame());
            setVisible(false);

        } catch (SQLException ex) {
            UIUtil.showError("SQL Error: " + ex.getMessage());
        }
    }
}
