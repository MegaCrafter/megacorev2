package com.objectvolatile.megacorev2;

import com.objectvolatile.megacorev2.util.ItemEditor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ItemRegistry {

    private Map<String, RegisteredItem> REGISTRY = new HashMap<>();

    private FileConfiguration cfg;

    private Plugin plugin;
    public ItemRegistry(Plugin plugin, String itemsFilePath) {
        this.plugin = plugin;

        this.cfg = new MegaYaml(plugin, itemsFilePath, true).loaded().options();

        cacheItems();

        map.put(plugin, this);
    }

    public void cacheItems() {
        if (cfg == null) throw new IllegalStateException("Items file was not registered!");

        REGISTRY.clear();

        for (String key : cfg.getKeys(false)) {
            registerItem(key, key);
        }
    }

    private void registerItem(String key, String headField) {
        REGISTRY.put(key, new RegisteredItem(cfg, headField));
    }

    private void unregisterItem(String key) {
        REGISTRY.remove(key);
    }

    public ItemStack getItem(String key, String... replacements) {
        if (replacements.length == 0) {
            RegisteredItem regItem = REGISTRY.get(key);
            if (regItem == null) throw new IllegalArgumentException(key + " is invalid!");

            return regItem.item().clone();
        } else {
            return ItemEditor.edit(getItem(key)).replaceAll(replacements).finish();
        }
    }


    private static Map<Plugin, ItemRegistry> map = new HashMap<>();

    public static ItemRegistry get(Plugin plugin) {
        ItemRegistry reg = map.get(plugin);
        if (reg == null) throw new NullPointerException("ItemRegistry was not initialized for " + plugin.getName());

        return reg;
    }
}