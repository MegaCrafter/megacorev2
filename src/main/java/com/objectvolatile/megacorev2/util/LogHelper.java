package com.objectvolatile.megacorev2.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogHelper {

    private Plugin plugin;
    private Logger logger;

    private BufferedWriter writer = null;

    public LogHelper(Plugin plugin, String logFilePath) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();

        if (logFilePath != null) {
            File logFile = new File(plugin.getDataFolder(), logFilePath);
            if (!logFile.exists()) {
                try {
                    plugin.getDataFolder().mkdir();
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile)));

                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
                String line;
                while ((line = reader.readLine()) != null) {
                    writer.append(line);
                    writer.newLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void log(String message) {
        log(Level.INFO, message);
    }

    public void log(Level level, String message) {
//        LOGGER.log(level, message);
        ChatColor color = ChatColor.GREEN;
        if (Level.INFO.equals(level)) {
            color = ChatColor.AQUA;
        } else if (Level.WARNING.equals(level) || Level.SEVERE.equals(level)) {
            color = ChatColor.RED;
        }

        Bukkit.getServer().getConsoleSender().sendMessage(color + "[" + plugin.getName() + "] " + message);

        appendLog(level, message);
    }

    public void severeError(String message) {
        log(Level.SEVERE, message);
        Bukkit.getPluginManager().disablePlugin(plugin);
    }

    private void appendLog(Level level, String message) {
        if (writer == null) return;

        try {
            String date = LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS"));

            writer.append(String.format("[%s] [%s] %s", date, level.getName(), message));
            writer.newLine();

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}