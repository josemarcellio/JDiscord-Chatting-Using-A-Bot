package com.josemarcellio.jdiscord.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(
            ReadyEvent event) {

        JDA jda = event.getJDA();
        SelfUser selfUser = jda.getSelfUser();
        String botName = selfUser.getName();

        System.out.printf(
                "[JDiscord] Logged in as %s%n",
                botName);
    }
}
