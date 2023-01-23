package com.josemarcellio.jdiscord.gui;

import com.josemarcellio.jdiscord.listener.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class JDiscordLoginGUI extends JFrame
        implements ActionListener {

    private JPasswordField tokenField;

    public JDiscordLoginGUI() {
        createUI();
    }

    private void createUI() {
        setTitle("JDiscord Login");
        setSize(283, 100);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();

        inputPanel.setLayout(
                new BoxLayout(inputPanel,
                        BoxLayout.Y_AXIS));

        JLabel tokenLabel = new JLabel(
                "Input Token:");

        tokenLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT);

        tokenField = new JPasswordField();
        tokenField.setEchoChar('*');
        inputPanel.add(tokenLabel);
        inputPanel.add(tokenField);

        add(inputPanel, BorderLayout.CENTER);

        JButton loginButton = new JButton(
                "Login");

        add(loginButton, BorderLayout.SOUTH);
        loginButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(
            ActionEvent e) {

        String token = new String(
                tokenField.getPassword());

        try {
            JDA jda = JDABuilder.createDefault(
                    token)
                    .addEventListeners(new ReadyListener())
                    .build();

            JDiscordPanelGUI jDiscordPanelGUI =
                    new JDiscordPanelGUI(jda);

            jDiscordPanelGUI.setVisible(true);
            JDiscordLoginGUI.this.dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Your token is invalid, please input correctly!",
                    "JDiscord Error",
                    JOptionPane.ERROR_MESSAGE);

            System.out.println("[JDiscord] Your token is invalid, " +
                    "please input correctly!");
        }
    }
}