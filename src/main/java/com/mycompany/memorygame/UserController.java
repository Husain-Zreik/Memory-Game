package com.mycompany.memorygame;

import java.sql.*;
import java.util.*;

public class UserController {

    public static boolean hasUserRecord(String username) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT * FROM results WHERE user_id = ?")) {
            statement.setInt(1, getUserId(username));
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // If there's a record, return true; otherwise, false
        } catch (SQLException e) {
            UIUtil.showError("Error checking user record: " + e.getMessage());
            return false;
        }
    }

    public static void createInitialRecord(String username) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO results (user_id, score, level) VALUES (?,  0, 0)")) {
            statement.setInt(1, getUserId(username));
            statement.executeUpdate();
        } catch (SQLException e) {
            UIUtil.showError("Error creating initial record: " + e.getMessage());
        }
    }

    public static int getUserId(String username) {
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            }
        } catch (SQLException e) {
            UIUtil.showError("Error getting user ID: " + e.getMessage());
        }
        return -1;
    }

    public static Player getUserInfo(int userId) {
        Player player = null;
        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(
                "SELECT users.username, results.score, results.level FROM users "
                + "JOIN results ON users.user_id = results.user_id WHERE users.user_id = ?")) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                int score = resultSet.getInt("score");
                int level = resultSet.getInt("level");
                player = new Player(username, score, level);
            }
        } catch (SQLException e) {
            UIUtil.showError("Error fetching user info: " + e.getMessage());
        }
        return player;
    }

    public static List<Player> getTopPlayers(int limit) {
        List<Player> topPlayers = new ArrayList<>();
        String query = "SELECT TOP " + limit + " users.username, results.score, results.level "
                + "FROM results "
                + "JOIN users ON results.user_id = users.user_id "
                + "ORDER BY results.score DESC, results.level DESC";

        try (Connection connection = DBConnection.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int score = resultSet.getInt("score");
                int level = resultSet.getInt("level");
                topPlayers.add(new Player(username, score, level));
            }
        } catch (SQLException e) {
            UIUtil.showError("Error fetching top players: " + e.getMessage());
        }
        return topPlayers;
    }

}
