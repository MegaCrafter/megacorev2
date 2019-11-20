package com.objectvolatile.megacorev2.chat;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChatControlUtil {

    static final Map<Player, BiConsumer<Player, String>> controllingPlayers = new HashMap<>();

    public static void control(Player player, BiConsumer<Player, String> controller) {
        controllingPlayers.put(player, controller);
    }

    public static void stopControlling(Player player) {
        controllingPlayers.remove(player);
    }
}