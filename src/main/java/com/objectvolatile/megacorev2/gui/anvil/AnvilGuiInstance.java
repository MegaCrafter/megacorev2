package com.objectvolatile.megacorev2.gui.anvil;

import com.objectvolatile.megacorev2.gui.GuiResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract class AnvilGuiInstance {

    private AnvilPage page;

    public AnvilGuiInstance(String title) {
        page = new AnvilPage(this, title);
    }

    public final AnvilPage page() {
        return page;
    }

    public abstract GuiResponse handleClick(Player p, AnvilSlot slot, ClickType clickType, ItemStack item);
    public abstract GuiResponse handleOtherInventory(Player p, ClickType clickType, ItemStack item);

    public abstract ItemStack handleResultChange(ItemStack result, String text);

    public void handleClose(Player player) { }

    public final void openFor(Player player) {
        page.openFor(player);
    }
}