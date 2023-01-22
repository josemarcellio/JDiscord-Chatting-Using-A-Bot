package com.josemarcellio.jdiscord.gui;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class JDiscordPanelGUI extends JFrame
        implements ActionListener, ItemListener {

    private final JDA jda;
    private JTextField channelIdField;
    private JTextArea messageField;
    private JCheckBox embedCheckBox;
    private JTextField embedTitleField;
    private JTextField embedDescriptionField;
    private JTextField embedColorField;

    public JDiscordPanelGUI(
            JDA jda) {

        this.jda = jda;
        createUI();
    }

    private void createUI() {
        setTitle("JDiscord Panel");
        setSize(283, 270);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();

        inputPanel.setLayout(
                new BoxLayout(inputPanel,
                        BoxLayout.Y_AXIS));

        JLabel channelIdLabel = new JLabel(
                "Channel ID:");

        channelIdField = new JTextField();
        inputPanel.add(channelIdLabel);
        inputPanel.add(channelIdField);

        JLabel messageLabel = new JLabel(
                "Message:");

        messageField = new JTextArea();
        inputPanel.add(messageLabel);

        inputPanel.add(
                new JScrollPane(messageField));

        embedCheckBox = new JCheckBox(
                "Click to use embed", false);

        inputPanel.add(
                embedCheckBox);

        embedTitleField = new JTextField();
        embedTitleField.setEnabled(false);

        inputPanel.add(
                new JLabel("Title"));

        inputPanel.add(embedTitleField);

        embedDescriptionField = new JTextField();
        embedDescriptionField.setEnabled(false);

        inputPanel.add(
                new JLabel("Description"));

        inputPanel.add(embedDescriptionField);

        embedColorField = new JTextField();
        embedColorField.setEnabled(false);

        inputPanel.add(
                new JLabel("Color ( #Hex / Int )"));

        inputPanel.add(
                embedColorField);

        add(inputPanel, BorderLayout.CENTER);

        JButton sendButton = new JButton(
                "Send a message");

        add(sendButton, BorderLayout.SOUTH);
        sendButton.addActionListener(this);
        embedCheckBox.addItemListener(this);
    }

    private void sendMessage(
            String channelId, String message) {

        if (!channelId.matches("^\\d+$")) {

            JOptionPane.showMessageDialog(this,
                    "Invalid Channel ID!",
                    "JDiscord Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            JOptionPane.showMessageDialog(this,
                    "Channel not found!",
                    "JDiscord Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }

        if (embedCheckBox.isSelected()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            String title = embedTitleField.getText().trim();
            String description = embedDescriptionField.getText().trim();
            String color = embedColorField.getText().trim();

            if (!title.isEmpty()) {
                embedBuilder.setTitle(title);
            }
            if (!description.isEmpty()) {
                embedBuilder.setDescription(description);
            }
            if (!color.isEmpty()) {
                if (color.startsWith("#")) {
                    try {
                        embedBuilder.setColor(Color.decode(color));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Please input the code format correctly!",
                                "JDiscord Error",
                                JOptionPane.ERROR_MESSAGE);

                        return;
                    }
                } else {
                    try {
                        int intColor = Integer.parseInt(color);
                        if (intColor >= 0 && intColor <= 16777215) {
                            embedBuilder.setColor(intColor);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Please input the code format correctly!",
                                    "JDiscord Error",
                                    JOptionPane.ERROR_MESSAGE);

                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this,
                                "Please input the code format correctly!",
                                "JDiscord Error",
                                JOptionPane.ERROR_MESSAGE);

                        return;
                    }
                }
            }

            channel.sendMessageEmbeds(
                    embedBuilder.build()).queue();

            System.out.println("[JDiscord] "
                    + jda.getSelfUser().getName() + " -> Title: " + title
                    + ", Description: " + description + ", Color: " + color);

        } else {
            channel.sendMessage(message).queue();
            System.out.println("[JDiscord] "
                    + jda.getSelfUser().getName() + " -> " + message);
        }
    }

    @Override
    public void actionPerformed(
            ActionEvent e) {

        String channelId = channelIdField.getText().trim();
        String message = messageField.getText().trim();
        sendMessage(channelId, message);
    }

    @Override
    public void itemStateChanged(
            ItemEvent e) {

        if (e.getStateChange() == ItemEvent.SELECTED) {
            messageField.setEnabled(false);
            embedTitleField.setEnabled(true);
            embedDescriptionField.setEnabled(true);
            embedColorField.setEnabled(true);
        } else {
            messageField.setEnabled(true);
            embedTitleField.setEnabled(false);
            embedDescriptionField.setEnabled(false);
            embedColorField.setEnabled(false);
        }
    }
}