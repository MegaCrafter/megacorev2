package com.objectvolatile.megacorev2.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OptionButtonInformation {

    private Player player;
    private String field;
    private ItemStack item;
    private int slot;
    private GuiPage page;

    public OptionButtonInformation(Player player, String field, ItemStack item, int slot, GuiPage page) {
        this.player = player;
        this.field = field;
        this.item = item;
        this.slot = slot;
        this.page = page;
    }

    public Player player() {
        return player;
    }

    public String field() {
        return field;
    }

    public ItemStack item() {
        return item;
    }

    public int slot() {
        return slot;
    }

    public GuiPage page() {
        return page;
    }
}