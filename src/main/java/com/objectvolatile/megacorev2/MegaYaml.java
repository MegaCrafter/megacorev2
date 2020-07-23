package com.objectvolatile.megacorev2;

import com.objectvolatile.megacorev2.util.MUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class MegaYaml {

    private Plugin plugin;
    private String filePath;
    private boolean isResource;
    private FileConfiguration options;
    private File file;

    public MegaYaml(Plugin plugin, String filePath, boolean isResource) {
        this.plugin = plugin;
        this.filePath = filePath;
        this.isResource = isResource;
    }

    public MegaYaml loaded() {
        if (isResource) {
            MUtils.saveResourceSilent(plugin, filePath);
        }

        file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdir();
                file.getParentFile().mkdir();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        options = YamlConfiguration.loadConfiguration(file);

        return this;
    }

    public FileConfiguration options() {
        return options;
    }

    public void save() {
        try {
            options.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}