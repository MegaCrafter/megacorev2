package com.objectvolatile.megacorev2;

import com.objectvolatile.megacorev2.util.MUtils;
import com.objectvolatile.megacorev2.util.lang.ActionInfo;
import com.objectvolatile.megacorev2.util.lang.LangMessage;
import com.objectvolatile.megacorev2.util.oop.ConfMapOf;
import com.objectvolatile.megacorev2.util.oop.PlaceholderString;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.logging.Level;

public final class LangUtil {

    private final Plugin plugin;
    private final String filePrefix;

    private File readFile;
    private FileConfiguration read;

    private final String langOption;
    private final String placeholderFile;

    private AbstractionUtil abstraction = new AbstractionUtil();

    private Map<String, LangMessage> messageMap = new HashMap<>();

    public LangUtil(Plugin plugin, String... defaultMessageFiles) {
        this(plugin, "placeholders.yml", "lang_", defaultMessageFiles);
    }

    public LangUtil(Plugin plugin, String placeholderFile, String filePrefix, String... defaultMessageFiles) {
        this(plugin, "lang", placeholderFile, filePrefix, defaultMessageFiles);
    }

    public LangUtil(Plugin plugin, String langOption, String placeholderFile, String filePrefix, String... defaultMessageFiles) {
        this.plugin = plugin;
        this.filePrefix = filePrefix;
        this.langOption = langOption;
        this.placeholderFile = placeholderFile;

        saveDefaultFiles(defaultMessageFiles);
        loadLangConfiguration(langOption, placeholderFile);

        map.put(plugin, this);
    }

    public void sendText(CommandSender cs, String field, String... replacements) {
        messageMap.get(field).sendTo(cs, replacements);
    }
    public void sendTextWithAction(CommandSender cs, String field, ActionInfo[] infos, String... replacements) {
        messageMap.get(field).sendWithAction(cs, infos, replacements);
    }

    public void sendText(Iterable<? extends OfflinePlayer> cs, String field, String... replacements) {
        messageMap.get(field).sendTo(cs, replacements);
    }
    public void sendTextWithAction(Iterable<? extends OfflinePlayer> cs, String field, ActionInfo[] infos, String... replacements) {
        messageMap.get(field).sendWithAction(cs, infos, replacements);
    }

    public void sendText(UUID uuid, String field, String... replacements) {
        messageMap.get(field).sendTo(Bukkit.getPlayer(uuid), replacements);
    }
    public void sendTextWithAction(UUID uuid, String field, ActionInfo[] infos, String... replacements) {
        messageMap.get(field).sendWithAction(Bukkit.getPlayer(uuid), infos, replacements);
    }

    public void broadcastText(String field, String... replacements) {
        messageMap.get(field).sendTo(Bukkit.getOnlinePlayers(), replacements);
    }
    public void broadcastTextWithAction(String field, ActionInfo[] infos, String... replacements) {
        messageMap.get(field).sendWithAction(Bukkit.getOnlinePlayers(), infos, replacements);
    }

    public List<String> getRawString(String field, String... replacements) {
        return messageMap.get(field).getRawString(replacements);
    }

    public int maxPageFor(String fieldPrefix) {
        String maxPage = messageMap.keySet().stream()
                .filter(str -> str.startsWith(fieldPrefix))
                .max(Comparator.comparingInt(o -> Integer.parseInt(o.substring(fieldPrefix.length() + 1))))
                .orElse(fieldPrefix + "-0");

        return Integer.parseInt(maxPage.substring(fieldPrefix.length()+1));
    }

    private void saveDefaultFiles(String... fileNames) {
        plugin.getDataFolder().mkdir();

        for (String name : fileNames) {
            if (!name.startsWith(filePrefix)) {
                MegaCore.logger.log(Level.WARNING, name + " does not start with " + filePrefix + " [" + plugin.getName() + "]");
                MegaCore.logger.log(Level.WARNING, "Skipping...");
                continue;
            }

            MUtils.saveResourceSilent(plugin, name);
        }
    }

    private void loadLangConfiguration(String langOption, String placeholderFile) {
        String lang = plugin.getConfig().getString(langOption);

        if (lang == null) {
            throw new IllegalStateException("LangUtil could not find '" + langOption + "' option in config.yml of " + plugin.getName());
        }

        readFile = new File(plugin.getDataFolder(), filePrefix+lang+".yml");

        if (!readFile.exists()) throw new IllegalArgumentException("Lang file " + filePrefix+lang+".yml could not be found for " + plugin.getName() + "!");

        read = YamlConfiguration.loadConfiguration(readFile);

        Map<String, Object> placeholders = new ConfMapOf(new MegaYaml(plugin, placeholderFile, true).loaded().options()).map();

        for (String key : read.getKeys(true)) {
            List<String> listValue = read.getStringList(key);
            if (!listValue.isEmpty()) {
                for (int i = 0, size = listValue.size(); i < size; i++) {
                    listValue.set(i, new PlaceholderString(listValue.get(i), placeholders).applied());
                }

                messageMap.put(key, new LangMessage(abstraction, listValue));
                continue;
            }

            String value = read.getString(key);
            if (value != null) { // So it is not a configuration section
                messageMap.put(key, new LangMessage(abstraction, new PlaceholderString(value, placeholders).applied()));
            }
        }
    }

    public void reload() {
        loadLangConfiguration(langOption, placeholderFile);
    }

    public List<String> getMissingFields(String defFileName) {
        InputStream resStream = plugin.getResource(defFileName);
        if (resStream == null) throw new IllegalArgumentException(defFileName + " is not a default message file!");

        FileConfiguration def = YamlConfiguration.loadConfiguration(new InputStreamReader(resStream));
        Set<String> defKeys = def.getKeys(false);

        defKeys.removeAll(read.getKeys(false)); // get fields that are in default but not in read (they are 'missing')

        return new ArrayList<>(defKeys);
    }

    private static Map<Plugin, LangUtil> map = new HashMap<>();

    public static LangUtil get(Plugin plugin) {
        LangUtil util = map.get(plugin);
        if (util == null) throw new NullPointerException("LangUtil was not initialized for " + plugin.getName());
        return util;
    }
}