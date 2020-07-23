package com.objectvolatile.megacorev2.gui.anvil;

import com.objectvolatile.megacorev2.gui.GuiResponse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;

// Not working on 1.8

public class AnvilGuiManager implements Listener {

    @EventHandler
    public void invClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        if (event.getInventory().getHolder() instanceof AnvilPage) {
            AnvilPage page = (AnvilPage) event.getInventory().getHolder();

            if (event.getClickedInventory() != event.getInventory()) {
                if (page.handleOtherInventory((Player) event.getWhoClicked(), event.getClick(), event.getClickedInventory().getItem(event.getSlot())) == GuiResponse.CANCEL) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).updateInventory();
                }

            } else {
                AnvilSlot slot = AnvilSlot.values()[event.getSlot()];
                if (page.handleClick((Player) event.getWhoClicked(), slot, event.getClick(), event.getClickedInventory().getItem(event.getSlot())) == GuiResponse.CANCEL) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).updateInventory();
                }

            }
        }
    }

    /************************************
     ***      NOT WORKING ON 1.8      ***
     *************************************/
    @EventHandler
    public void anvilClick(PrepareAnvilEvent event) {
        if (event.getInventory().getHolder() instanceof AnvilPage) {
            event.getInventory().setRepairCost(0);
            event.getInventory().setMaximumRepairCost(0);

            AnvilPage page = (AnvilPage) event.getInventory().getHolder();
            ItemStack ret = page.handleResultChange(event.getResult(), event.getInventory().getRenameText());

            if (ret != null) event.setResult(ret);
        }
    }
    /************************************
     ************************************
     *************************************/

    @EventHandler
    public void invClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof AnvilPage) {
            AnvilPage page = (AnvilPage) event.getInventory().getHolder();
            page.handleClose((Player) event.getPlayer());
        }
    }

}