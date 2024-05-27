package com.mycompany.memorygame;

import java.io.*;
import java.sql.*;
import javax.swing.JFileChooser;

public class ExportData {

    public static void backup() {
        Connection conn = DBConnection.getConnection();
        String query = "SELECT u.user_id, u.username, u.user_type, r.score, r.level "
                + "FROM dbo.users u "
                + "LEFT JOIN dbo.results r ON u.user_id = r.user_id";
        StringBuilder txtData = new StringBuilder("Id,Username,Type,Score,Level\n");

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("username");
                String userType = rs.getString("user_type");
                int score = rs.getInt("score");
                int level = rs.getInt("level");
                txtData.append(id).append(",").append(name).append(",").append(userType)
                        .append(",").append(score).append(",").append(level).append("\n");
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

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Users");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            File fileToSave = new File(selectedDirectory, "users.txt");
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(txtData.toString());
                UIUtil.showSuccess("Users exported successfully!");
            } catch (IOException ex) {
                UIUtil.showError("Error exporting users: " + ex.getMessage());
            }
        }
    }
}
