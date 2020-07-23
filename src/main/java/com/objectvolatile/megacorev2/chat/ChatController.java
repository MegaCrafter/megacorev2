package com.objectvolatile.megacorev2.chat;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.BiConsumer;

public class ChatController {

    private Plugin plugin;
    private BiConsumer<Player, String> controller;

    public ChatController(Plugin plugin, BiConsumer<Player, String> controller) {
        this.plugin = plugin;
        this.controller = controller;
    }

    public Plugin plugin() {
        return plugin;
    }

    public BiConsumer<Player, String> controller() {
        return controller;
    }
}