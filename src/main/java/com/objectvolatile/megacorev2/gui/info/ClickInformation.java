package com.objectvolatile.megacorev2.gui.info;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ClickInformation {

    private Player player;
    private ClickType clickType;
    private ItemStack item;
    private int slot;

    public ClickInformation(Player player, ClickType clickType, ItemStack item, int slot) {
        this.player = player;
        this.clickType = clickType;
        this.item = item;
        this.slot = slot;
    }

    public Player player() {
        return player;
    }

    public ClickType clickType() {
        return clickType;
    }

    public ItemStack item() {
        return item;
    }

    public int slot() {
        return slot;
    }

}