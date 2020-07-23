package com.objectvolatile.megacorev2;

import com.objectvolatile.megacorev2.chat.ChatControlEvent;
import com.objectvolatile.megacorev2.gui.GuiManager;
import com.objectvolatile.megacorev2.gui.anvil.AnvilGuiManager;
import com.objectvolatile.megacorev2.util.LogHelper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class MegaCore {

    public static final LogHelper logger = new LogHelper(null, null);

    public static void initFor(Plugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(new GuiManager(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new AnvilGuiManager(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatControlEvent(), plugin);
    }

}