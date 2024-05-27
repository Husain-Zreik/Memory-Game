package com.mycompany.memorygame;

import java.sql.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import javax.swing.table.DefaultTableModel;

public class DisplayUsers {

    public static void display() {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT * FROM dbo.users";
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Id", "Username", "Type"}, 0);

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("username");
                String userType = rs.getString("user_type");
                tableModel.addRow(new Object[]{id, name, userType});
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

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JFrame tableFrame = new JFrame("Users");
        JButton closeButton = new JButton("Close");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        tableFrame.add(buttonPanel, "South");
        tableFrame.add(scrollPane);
        tableFrame.pack();
        tableFrame.setSize(350, 250);
        tableFrame.setLocationRelativeTo(null);
        tableFrame.setVisible(true);

        closeButton.addActionListener((ActionEvent evt) -> {
            tableFrame.dispose();
        });
    }
}
