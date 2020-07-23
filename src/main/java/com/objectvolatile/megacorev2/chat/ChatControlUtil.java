package com.objectvolatile.megacorev2.chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChatControlUtil {

    static final Map<Player, ChatController> controllingPlayers = new HashMap<>();

    public static void control(Plugin plugin, Player player, BiConsumer<Player, String> controller) {
        controllingPlayers.put(player, new ChatController(plugin, controller));
    }

    public static void stopControlling(Player player) {
        controllingPlayers.remove(player);
    }
}