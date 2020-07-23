package com.objectvolatile.megacorev2.chat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.BiConsumer;

public class ChatControlEvent implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        ChatController cont = ChatControlUtil.controllingPlayers.get(event.getPlayer());
        if (cont == null) return;

        BiConsumer<Player, String> controller = cont.controller();;
        if (controller != null) {
            event.setCancelled(true);

            Bukkit.getScheduler().runTask(cont.plugin(), () -> {
                controller.accept(event.getPlayer(), event.getMessage());
            });
        }
    }

}