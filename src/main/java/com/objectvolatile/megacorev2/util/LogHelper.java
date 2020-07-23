package com.objectvolatile.megacorev2.util;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

public final class LogHelper {

    private Plugin plugin;

    private boolean silent = false;
    private boolean disabled = false;

    private BufferedWriter writer = null;

    public LogHelper(Plugin plugin, String logFilePath) {
        this.plugin = plugin;

        if (plugin == null) return;
        if (logFilePath == null) return;

        File logFile = new File(plugin.getDataFolder(), logFilePath);
        if (!logFile.exists()) {
            try {
                plugin.getDataFolder().mkdir();
                logFile.getParentFile().mkdir();
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile)));

            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            writer.write(builder.toString());
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void log(String message) {
        log(Level.INFO, message);
    }

    public void log(Level level, String message) {
        if (disabled) return;

        if (!silent) {
            ChatColor color = ChatColor.GREEN;
            if (Level.INFO.equals(level)) {
                color = ChatColor.AQUA;
            } else if (Level.WARNING.equals(level) || Level.SEVERE.equals(level)) {
                color = ChatColor.RED;
            }

            String tag;
            if (plugin != null) tag = plugin.getName();
            else tag = "MegaCore";

            Bukkit.getServer().getConsoleSender().sendMessage(color + "[" + tag + "] " + message);
        }

        appendLog(level, message);
    }

    public void severeError(String message) {
        log(Level.SEVERE, message);

        if (plugin != null) Bukkit.getPluginManager().disablePlugin(plugin);
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
        if (writer == null) return;

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}