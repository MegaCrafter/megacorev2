package com.objectvolatile.megacorev2;

import com.objectvolatile.megacorev2.util.IOUtils;
import com.objectvolatile.megacorev2.util.MUtils;
import com.objectvolatile.megacorev2.util.oop.ColoredString;
import com.objectvolatile.megacorev2.util.oop.ConfMapOf;
import com.objectvolatile.megacorev2.util.oop.PlaceholderString;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class LangUtil {

    private Plugin plugin;
    private final String filePrefix;
    private File readFile;
    private FileConfiguration read;

    private String langOption;
    private String placeholderFile;

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
        List<String> textList = read.getStringList(field);
        String text = read.getString(field);
        if (textList.isEmpty()) {
            if (text == null) {
                throw new IllegalArgumentException("Message field " + field + " could not be found for " + plugin.getName() + "!");
            }
        }

        // "&cBir başarım kazandınız!@subtitle:2:2:2" type : fadein : dur : fadeout

        int i = 0;
        int size = textList.size();
        do {
            if (i < size) text = textList.get(i); // only if we are dealing with a list

            String titleText = "";

            text = MUtils.fastReplace(text, replacements);

            String[] split = text.split("@");

            process:
            if (cs instanceof Player && split.length != 0 && split.length != 1) {
                titleText = text.substring(0, text.length() - split[split.length - 1].length()-1);

                Player p = (Player) cs;

                String info = split[split.length - 1];
                String[] infopart = info.split(":");
                if (infopart.length < 1) break process;

                switch (infopart[0]) {
                    case "title":
                        if (infopart.length != 4) break process;

                        int fadeIn, dur, fadeOut;

                        try {
                            fadeIn = Integer.parseInt(infopart[1]);
                            dur = Integer.parseInt(infopart[2]);
                            fadeOut = Integer.parseInt(infopart[3]);
                        } catch (NumberFormatException e) {
                            break process;
                        }

                        String[] titleTextSplit = titleText.split("\\|");
                        String t = titleTextSplit[0];
                        String st = "";
                        if (titleTextSplit.length == 2) st = titleTextSplit[1];

                        MegaCore.sendTitle(t, st, p, fadeIn, dur, fadeOut);
                        continue;
                    case "actionbar":
                        MegaCore.sendActionbar(titleText, p);
                        continue;
                    default:
                        break process;
                }
            }

            cs.sendMessage(new ColoredString(text).applied());
        } while (++i < size);
    }

    private void saveDefaultFiles(String... fileNames) {
        File messagesFolder = new File(plugin.getDataFolder()+"/messages");
        messagesFolder.mkdir();

        for (String name : fileNames) {
            if (!name.startsWith(filePrefix)) {
                MegaCore.logger.log(Level.WARNING, name + " does not start with " + filePrefix + " [" + plugin.getName() + "]");
                MegaCore.logger.log(Level.WARNING, "Skipping...");
                continue;
            }

            File file = new File(messagesFolder, name);
            if (file.exists()) continue;

            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                IOUtils.copyContents(plugin.getResource(name), new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadLangConfiguration(String langOption, String placeholderFile) {
        String lang = plugin.getConfig().getString(langOption);

        if (lang == null) {
            throw new IllegalStateException("LangUtil could not find 'lang' option in config.yml of " + plugin.getName());
        }

        readFile = new File(plugin.getDataFolder()+"/messages/"+filePrefix+lang+".yml");

        if (!readFile.exists()) throw new IllegalArgumentException("Lang file " + filePrefix+lang+".yml could not be found for " + plugin.getName() + "!");

        read = YamlConfiguration.loadConfiguration(readFile);

        Map<String, Object> placeholders = new ConfMapOf(new MegaYaml(plugin, placeholderFile, true).loaded().options()).map();

        for (String key : read.getKeys(true)) {
            List<String> listValue = read.getStringList(key);
            if (!listValue.isEmpty()) {
                for (int i = 0, size = listValue.size(); i < size; i++) {
                    listValue.set(i, new PlaceholderString(listValue.get(i), placeholders).applied());
                }

                read.set(key, listValue);
                continue;
            }

            String value = read.getString(key);
            if (value != null) { // So it is not a configuration section
                read.set(key, new PlaceholderString(value, placeholders).applied());
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