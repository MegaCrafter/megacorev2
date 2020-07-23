package com.objectvolatile.megacorev2.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class GuiManager implements Listener {

    public static final String OTHERINV_FIELD = "%otherinv%"; // returned if click is on another menu (most likely player's inventory)
    public static final String EXTERNAL_FIELD = "%external%"; // returned if clicked item is not an option item

    @EventHandler
    public void invClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        if (event.getInventory().getHolder() instanceof GuiPage) {
            GuiPage page = (GuiPage) event.getInventory().getHolder();

            ItemStack item = event.getClickedInventory().getItem(event.getSlot());
            if (item == null || item.getType() == Material.AIR) return;

            if (event.getWhoClicked() instanceof Player) {
                String slotField;

                if (event.getInventory() == event.getClickedInventory()) {
                    slotField = page.getSlotField(event.getSlot());
                } else {
                    slotField = OTHERINV_FIELD;
                }

                GuiResponse res = page.handleClick((Player) event.getWhoClicked(), slotField, event.getClick(), item, event.getSlot());

                if (res == GuiResponse.CANCEL) {
                    event.setCancelled(true);
                    ((Player) event.getWhoClicked()).updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void invClick(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof GuiPage) {
            GuiPage page = (GuiPage) event.getInventory().getHolder();

            if (!page.canPutItems()) event.setCancelled(true);
        }
    }

    @EventHandler
    public void invClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof GuiPage) {
            ((GuiPage) event.getInventory().getHolder()).handleClose((Player) event.getPlayer());
        }
    }

}