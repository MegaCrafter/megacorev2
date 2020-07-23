package com.objectvolatile.megacorev2.gui.anvil;

import com.objectvolatile.megacorev2.gui.GuiResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public final class AnvilPage implements InventoryHolder {

    private AnvilGuiInstance guiInstance;
    private Inventory inv;

    private ItemStack output = null;

    AnvilPage(AnvilGuiInstance guiInstance, String title) {
        this.guiInstance = guiInstance;
        this.inv = Bukkit.createInventory(this, InventoryType.ANVIL, title);
    }

    GuiResponse handleClick(Player p, AnvilSlot slot, ClickType clickType, ItemStack item) {
        return guiInstance.handleClick(p, slot, clickType, item);
    }
    GuiResponse handleOtherInventory(Player p, ClickType clickType, ItemStack item) {
        return guiInstance.handleOtherInventory(p, clickType, item);
    }

    ItemStack handleResultChange(ItemStack result, String text) {
        return guiInstance.handleResultChange(result, text);
    }

    void handleClose(Player player) {
        guiInstance.handleClose(player);
    }

    void openFor(Player player) {
        player.openInventory(inv);
    }

    public void setItem(AnvilSlot slot, ItemStack item) {
        if (slot == AnvilSlot.OUTPUT) output = item;

        inv.setItem(slot.ordinal(), item);
    }

    public Inventory inventory() {
        return inv;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }
}