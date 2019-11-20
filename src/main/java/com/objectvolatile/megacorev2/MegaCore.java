package com.objectvolatile.megacorev2;

import com.objectvolatile.abstractionapi.AbstractionAPI;
import com.objectvolatile.megacorev2.chat.ChatControlEvent;
import com.objectvolatile.megacorev2.gui.GuiManager;
import com.objectvolatile.megacorev2.util.LogHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.logging.Level;

public class MegaCore {

    private static Plugin plugin;
    public static LogHelper logger;

    public static void init(Plugin toPlugin) {
        AbstractionAPI.init();

        plugin = toPlugin;

        logger = new LogHelper(plugin, plugin.getName().toLowerCase(Locale.ENGLISH) + ".log");

        Bukkit.getServer().getPluginManager().registerEvents(new GuiManager(), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new ChatControlEvent(), plugin);
    }

    public static void sendTitle(String titleText, String subtitleText, Player p, int fadeIn, int dur, int fadeOut) {
        boolean sent = AbstractionAPI.getTitleManager().sendTitle(titleText, subtitleText, p.getUniqueId(), fadeIn, dur, fadeOut);

        logger.log(Level.INFO, "Title: \"" + titleText + "\"");
        logger.log(Level.INFO, "Subtitle: \"" + subtitleText + "\"");

        if (!sent) {
            logger.log(Level.WARNING, "Titles are not supported in " + AbstractionAPI.getServerVersion());
        }
    }

    public static void sendActionbar(String text, Player p) {
        boolean sent = AbstractionAPI.getTitleManager().sendActionbar(text, p.getUniqueId());

        logger.log(Level.INFO, "Actionbar: \"" + text + "\"");

        if (!sent) {
            logger.log(Level.WARNING, "Actionbars is not supported in " + AbstractionAPI.getServerVersion());
        }
    }
}