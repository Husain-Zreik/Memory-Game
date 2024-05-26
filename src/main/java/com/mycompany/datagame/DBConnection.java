package com.mycompany.datagame;

import java.sql.*;

public class DBConnection {
    static String url = "jdbc:sqlserver://HUZK\\SQLEXPRESS:1433;databaseName=hr;encrypt=false";
    static String username = "sa";
    static String password = "root";
    
    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connection to database has been established.");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return connection;
    }
}
