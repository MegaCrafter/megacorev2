package com.objectvolatile.megacorev2.gui.info;

import com.objectvolatile.megacorev2.gui.GuiPage;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class OptionButtonInformation extends ButtonInformation {

    private String field;
    public OptionButtonInformation(Player player, String field, ClickType clickType, ItemStack item, int slot, GuiPage page) {
        super(player, clickType, item, slot, page);
        this.field = field;
    }

    public String field() {
        return field;
    }
}