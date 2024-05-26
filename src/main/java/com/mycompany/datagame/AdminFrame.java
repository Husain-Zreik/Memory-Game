package com.mycompany.datagame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AdminFrame extends JFrame {

    JButton bt_AddUser;
    JButton bt_DisplayUsers;
    JButton bt_DeleteUser;
    JButton bt_Logout;
    JButton bt_UploadUsers;
    JButton bt_ExportUsers;
    JPanel panel;

    AdminFrame() {

        super("Admin Menu");
        this.setSize(350, 250);

        bt_AddUser = new JButton("Add User");
        bt_DeleteUser = new JButton("Delete User");
        bt_DisplayUsers = new JButton("Display Users");
        bt_Logout = new JButton("Log Out");
        bt_UploadUsers = new JButton("Upload Users");
        bt_ExportUsers = new JButton("Backup Data");
        panel = new JPanel(new GridLayout(5, 1));
        panel.add(bt_AddUser);
        panel.add(bt_DisplayUsers);
        panel.add(bt_DeleteUser);
        panel.add(bt_UploadUsers);
        panel.add(bt_ExportUsers);
        add(panel, "North");
        add(bt_Logout, "South");

        bt_Logout.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new LogIn());
            setVisible(false);
        });

        bt_DisplayUsers.addActionListener((ActionEvent e) -> {
            DisplayUsers.display();
        });

        bt_DeleteUser.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new DeleteUser());
            setVisible(false);
        });

        bt_AddUser.addActionListener((ActionEvent e) -> {
            UIUtil.showFrame(new AddUser());
            setVisible(false);
        });

        bt_UploadUsers.addActionListener((ActionEvent e) -> {
            UploadUsers.upload();
        });

        bt_ExportUsers.addActionListener((ActionEvent e) -> {
            ExportData.backup();
        });

    }

}
