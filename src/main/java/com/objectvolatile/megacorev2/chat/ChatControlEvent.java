package com.objectvolatile.megacorev2.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.BiConsumer;

public class ChatControlEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        BiConsumer<Player, String> controller = ChatControlUtil.controllingPlayers.get(event.getPlayer());;
        if (controller != null) {
            event.setCancelled(true);

            controller.accept(event.getPlayer(), event.getMessage());
        }
    }

}