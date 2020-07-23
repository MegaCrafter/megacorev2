package com.objectvolatile.megacorev2;

import com.objectvolatile.megacorev2.util.item.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

public class RegisteredItem {

    private ItemStack item;

    private FileConfiguration cfg;

    private String headField;
    public RegisteredItem(FileConfiguration cfg, String headField) {
        this.headField = headField;
        this.cfg = cfg;

        loadItem();
    }

    private void loadItem() {
        this.item = cfg.getItemStack(headField);

        if (this.item == null) {
            this.item = new ItemBuilder()
                    .material(cfg.getString(headField + ".material", "dirt"))
                    .data((short) cfg.getInt(headField + ".data", 0))
                    .amount((short) cfg.getInt(headField + ".amount", 1))
                    .name(cfg.getString(headField + ".name"))
                    .lore(cfg.getStringList(headField + ".lore"))
                    .glow(cfg.getBoolean(headField + ".glow", false))
                    .enchants(cfg.getStringList(headField + ".enchants"))
                    .build();
        }
    }

    public ItemStack item() {
        return item;
    }
}