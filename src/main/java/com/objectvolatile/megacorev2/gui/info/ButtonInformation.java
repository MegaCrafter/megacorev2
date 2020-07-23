package com.objectvolatile.megacorev2.gui.info;

import com.objectvolatile.megacorev2.gui.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ButtonInformation extends ClickInformation {

    private GuiPage page;
    public ButtonInformation(Player player, ClickType clickType, ItemStack item, int slot, GuiPage page) {
        super(player, clickType, item, slot);
        this.page = page;
    }

    public GuiPage page() {
        return page;
    }
}