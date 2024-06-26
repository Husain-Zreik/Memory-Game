package com.mycompany.memorygame;

import java.sql.*;
import javax.swing.*;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

public class DeleteUser extends JFrame {

    JLabel lbl_selectUser;
    JComboBox<String> userComboBox;
    JButton bt_DeleteUser;
    JButton bt_Back;
    JPanel panel;
    JPanel buttonPanel;

    DeleteUser() {

        super("Delete User");
        this.setSize(300, 150);

        lbl_selectUser = new JLabel("Select User");
        userComboBox = new JComboBox<>();
        bt_DeleteUser = new JButton("Delete User");
        bt_Back = new JButton("Back");

        panel = new JPanel(new GridLayout(2, 2));
        buttonPanel = new JPanel(new GridLayout(1, 2));

        panel.add(lbl_selectUser);
        panel.add(userComboBox);

        buttonPanel.add(bt_Back);
        buttonPanel.add(bt_DeleteUser);

        add(panel, "North");
        add(buttonPanel, "South");

        populateUserComboBox();

        bt_DeleteUser.addActionListener((ActionEvent e) -> {
            String selectedItem = (String) userComboBox.getSelectedItem();
            if (selectedItem != null) {
                String selectedUser = selectedItem.split(" - ")[0];
                deleteUserData(Integer.parseInt(selectedUser));
            } else {
                UIUtil.showError("No user selected!");
            }
        });

        bt_Back.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new AdminFrame());
            setVisible(false);
        });
    }

    private void populateUserComboBox() {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT user_id, username FROM dbo.users WHERE user_type='player'";
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                userComboBox.addItem(userId + " - " + username);
            }
        } catch (SQLException ex) {
            UIUtil.showError("SQL Error: " + ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                UIUtil.showError("Error closing connection: " + ex.getMessage());
            }
        }
    }

    private void deleteUserData(int userId) {
        Connection conn = DBConnection.getConnection();
        String deleteResultsQuery = "DELETE FROM dbo.results WHERE user_id = ?";
        String deleteUserQuery = "DELETE FROM dbo.users WHERE user_id = ?";

        try (
                PreparedStatement deleteResultsStmt = conn.prepareStatement(deleteResultsQuery); 
                PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserQuery)) {

            deleteResultsStmt.setInt(1, userId);
            deleteResultsStmt.executeUpdate();

            deleteUserStmt.setInt(1, userId);
            deleteUserStmt.executeUpdate();

            UIUtil.showSuccess("User and associated results deleted successfully!");
            UIUtil.showFrame(new AdminFrame());
            setVisible(false);

        } catch (SQLException ex) {
            UIUtil.showError("SQL Error: " + ex.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException ex) {
                UIUtil.showError("Error closing connection: " + ex.getMessage());
            }
        }
    }

}
