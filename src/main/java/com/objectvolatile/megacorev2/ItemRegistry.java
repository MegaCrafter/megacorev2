package com.objectvolatile.megacorev2;

import com.objectvolatile.megacorev2.util.item.ItemEditor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public final class ItemRegistry {

    private final Map<String, RegisteredItem> REGISTRY = new HashMap<>();
    private final Map<String, ItemStack> MANUAL_REG = new HashMap<>();

    private MegaYaml yaml;
    private FileConfiguration cfg;

    public ItemRegistry(Plugin plugin) {
        this(plugin, "items.yml");
    }

    public ItemRegistry(Plugin plugin, String itemsFilePath) {
        this.yaml = new MegaYaml(plugin, itemsFilePath, true);

        cacheItems();

        map.put(plugin, this);
    }

    public void cacheItems() {
        this.cfg = this.yaml.loaded().options();

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

    public void registerItem(String key, ItemStack item) {
        MANUAL_REG.put(key, item);
    }

    public ItemStack getItem(String key, String... replacements) {
        if (replacements.length == 0) {
            ItemStack item = MANUAL_REG.get(key);
            if (item == null) {
                RegisteredItem regItem = REGISTRY.get(key);
                if (regItem == null) {
                    throw new IllegalArgumentException(key + " is invalid!");
                }

                return regItem.item().clone();
            }

            return item.clone();
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