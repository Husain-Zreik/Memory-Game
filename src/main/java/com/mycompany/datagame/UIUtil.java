package com.mycompany.datagame;

import javax.swing.*;

public class UIUtil {

    public static void showError(String errorMessage) {
        JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public static void showSuccess(String successMessage) {
        JOptionPane.showMessageDialog(null, successMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showFrame(JFrame frame) {
        try {
            frame.setResizable(false);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (Exception e) {
            showError("Failed to display frame: " + e.getMessage());
        }
    }
}

