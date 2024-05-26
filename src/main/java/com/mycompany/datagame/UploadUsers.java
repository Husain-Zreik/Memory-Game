package com.mycompany.datagame;

import java.io.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;

public class UploadUsers extends JFrame {

    private final JTable table;

    public UploadUsers(List<User> users) {
        super("Review Users");
        this.setSize(400, 300);
        this.setLayout(new BorderLayout());

        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Username", "Password", "Type"}, 0);
        for (User user : users) {
            tableModel.addRow(new Object[]{user.getUsername(), user.getPassword(), user.getUserType()});
        }

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton bt_Approve = new JButton("Approve");
        JButton bt_Cancel = new JButton("Cancel");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(bt_Approve);
        buttonPanel.add(bt_Cancel);

        bt_Approve.addActionListener((ActionEvent e) -> {
            try {
                addUsersToDatabase(users);
                UIUtil.showSuccess("Users added successfully!");
            } catch (SQLException ex) {
                UIUtil.showError("SQL Error: " + ex.getMessage());
            }
            setVisible(false);
        });

        bt_Cancel.addActionListener((ActionEvent e) -> setVisible(false));

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    private void addUsersToDatabase(List<User> users) throws SQLException {
        String userQuery = "INSERT INTO dbo.users (username, password, user_type) VALUES (?, ?, ?)";
        String resultQuery = "INSERT INTO dbo.results (user_id, score, level) VALUES (?, 0, 0)";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement resultStmt = conn.prepareStatement(resultQuery)) {
                for (User user : users) {
                    userStmt.setString(1, user.getUsername());
                    userStmt.setString(2, user.getPassword());
                    userStmt.setString(3, user.getUserType());
                    userStmt.executeUpdate();

                    try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int userId = generatedKeys.getInt(1);
                            resultStmt.setInt(1, userId);
                            resultStmt.addBatch();
                        } else {
                            throw new SQLException("Creating user failed, no ID obtained.");
                        }
                    }
                }
                resultStmt.executeBatch();
            }
        }
    }

    public static void upload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Upload Excel Sheet");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel Files", "xlsx", "xls");
        fileChooser.addChoosableFileFilter(filter);

        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                List<User> users = parseUsers(file);
                if (users != null && !users.isEmpty()) {
                    UIUtil.showFrame(new UploadUsers(users));
                }
            } catch (IOException ex) {
                UIUtil.showError("Error: " + ex.getMessage());
            }
        }
    }

    private static List<User> parseUsers(File file) throws IOException {
        List<User> users = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getLastRowNum() == 0) {
                UIUtil.showError("The Excel file must contain at least one row of data.");
                return null;
            }
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                UIUtil.showError("The Excel file must contain a header row.");
                return null;
            }
            int usernameCol = -1;
            int passwordCol = -1;
            int userTypeCol = -1;

            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                String cellValue = headerRow.getCell(i).getStringCellValue().toLowerCase();
                if (cellValue.equals("username")) {
                    usernameCol = i;
                } else if (cellValue.equals("password")) {
                    passwordCol = i;
                } else if (cellValue.equals("type")) {
                    userTypeCol = i;
                }
            }

            if (usernameCol == -1 || passwordCol == -1) {
                UIUtil.showError("The Excel file must contain 'username' and 'password' columns.");
                return null;
            }

            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    String username = row.getCell(usernameCol).getStringCellValue();
                    String password = row.getCell(passwordCol).getStringCellValue();
                    String userType = userTypeCol != -1 && row.getCell(userTypeCol) != null ? row.getCell(userTypeCol).getStringCellValue() : "player";

                    users.add(new User(username, password, userType));
                }
            }
        }
        return users;
    }
}
