package com.josemarcellio.jdiscord.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;
import java.util.Optional;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class JDiscordPanelGUI extends JFrame
        implements ActionListener, ItemListener {

    private final JDA jda;
    private JComboBox<String> serverList;
    private JComboBox<String> channelList;
    private JTextArea messageField;
    private JCheckBox embedCheckBox;
    private JTextField embedTitleField;
    private JTextField embedDescriptionField;
    private JTextField embedColorField;

    public JDiscordPanelGUI(
            JDA jda) {

        this.jda = jda;
        createUI();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        updateServerList();
        updateChannelList(jda.getGuilds()
                .get(0).getIdLong());
    }

    private void createUI() {
        setTitle("JDiscord Panel");
        setSize(290, 330);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();

        inputPanel.setLayout(new BoxLayout(
                inputPanel, BoxLayout.Y_AXIS));

        JLabel serverLabel = new JLabel("Server:");
        serverList = new JComboBox<>();
        updateServerList();
        inputPanel.add(serverLabel);
        inputPanel.add(serverList);

        JLabel channelLabel = new JLabel(
                "Channel:");

        channelList = new JComboBox<>();
        inputPanel.add(channelLabel);
        inputPanel.add(channelList);

        JLabel messageLabel = new JLabel(
                "Message:");

        messageField = new JTextArea();
        inputPanel.add(messageLabel);
        inputPanel.add(new JScrollPane(messageField));

        embedCheckBox = new JCheckBox(
                "Click to use embed",
                false);

        inputPanel.add(embedCheckBox);

        embedTitleField = new JTextField();
        embedTitleField.setEnabled(false);
        inputPanel.add(new JLabel(
                "Title"));

        inputPanel.add(
                embedTitleField);

        embedDescriptionField = new JTextField();
        embedDescriptionField.setEnabled(false);
        inputPanel.add(new JLabel(
                "Description"));

        inputPanel.add(
                embedDescriptionField);

        embedColorField = new JTextField();
        embedColorField.setEnabled(false);

        inputPanel.add(new JLabel(
                "Color ( #Hex / Int )"));

        inputPanel.add(embedColorField);

        add(inputPanel, BorderLayout.CENTER);

        JButton sendButton = new JButton(
                "Send a message");

        add(sendButton, BorderLayout.SOUTH);
        sendButton.addActionListener(this);
        embedCheckBox.addItemListener(this);
        serverList.addActionListener(this);
    }

    private void updateServerList() {
        serverList.removeAllItems();
        List<Guild> guilds = jda.getGuilds();
        for (Guild guild : guilds) {
            serverList.addItem(guild.getName());
        }
    }

    private void updateChannelList(
            long serverId) {

        channelList.removeAllItems();
        Optional<Guild> guild = Optional.ofNullable(
                jda.getGuildById(serverId));

        guild.ifPresent(g -> {
            List<TextChannel> channels = g.getTextChannels();
            for (TextChannel channel : channels) {
                channelList.addItem(channel.getName());
            }
        });
    }

    @Override
    public void actionPerformed(
            ActionEvent e) {

        if (e.getSource() == serverList) {
            Object selectedServer = serverList.getSelectedItem();
            if (selectedServer != null) {
                updateChannelList(jda.getGuildsByName(
                        (String) selectedServer,
                        true).get(0).getIdLong());
            }

        } else if (e.getSource() instanceof JButton) {
            String serverName = (String) serverList.getSelectedItem();
            String channelName = (String) channelList.getSelectedItem();
            String message = messageField.getText();

            if (serverName == null ||
                    channelName == null) {
                return;
            }

            Guild guild = jda.getGuildsByName(
                            serverName,
                            true)
                    .get(0);

            TextChannel channel = guild.getTextChannelsByName(
                            channelName,
                            true)
                    .get(0);

            if (embedCheckBox.isSelected()) {

                EmbedBuilder embedBuilder =
                        new EmbedBuilder();

                String title = embedTitleField
                        .getText().trim();

                String description = embedDescriptionField
                        .getText().trim();

                String color = embedColorField
                        .getText().trim();

                if (!title.isEmpty()) {
                    embedBuilder.setTitle(title);
                }
                if (!description.isEmpty()) {
                    embedBuilder.setDescription(
                            description);
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
                        embedBuilder.build())
                        .queue();

                System.out.printf(
                        "[JDiscord] %s -> Title: %s, Description: %s, Color: %s%n",
                        jda.getSelfUser().getName(), title, description, color);

            } else {
                channel.sendMessage(message)
                        .queue();
                System.out.printf(
                        "[JDiscord] %s -> %s%n",
                        jda.getSelfUser().getName(), message);
            }
        }
    }

    @Override
    public void itemStateChanged(
            ItemEvent e) {

        if (e.getSource() == embedCheckBox) {

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
}


