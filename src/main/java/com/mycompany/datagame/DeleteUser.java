package com.mycompany.datagame;

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
                Connection conn = DBConnection.getConnection();
                String query = "DELETE FROM dbo.users WHERE user_id='" + selectedUser + "'";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(query);
                    UIUtil.showSuccess("User deleted successfully!");
                } catch (SQLException ex) {
                    UIUtil.showError("SQL Error: " + ex.getMessage());
                } finally {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        UIUtil.showError("Error closing connection: " + ex.getMessage());
                    }
                }
                UIUtil.showFrame(new AdminFrame());
                setVisible(false);
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
}
