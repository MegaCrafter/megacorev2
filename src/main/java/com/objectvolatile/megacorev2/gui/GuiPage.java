package com.objectvolatile.megacorev2.gui;

import com.objectvolatile.megacorev2.ItemRegistry;
import com.objectvolatile.megacorev2.util.ItemEditor;
import com.objectvolatile.megacorev2.util.MUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GuiPage implements InventoryHolder {

    private Map<Integer, SlotItemFieldPair> savedSlots = new HashMap<>();
    private Map<Integer, SlotItemFieldPair> currentSlots = new HashMap<>();

    private boolean shouldUpdateTitle = false;

    private String title = null;
    private InventoryType type = null;
    private int rows = -1;

    private GuiInstance guiInstance;
    private Inventory inventory;
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public GuiResponse handleClick(Player p, String field, ItemStack item, int slot) {
        if (field.equals("prev page")) {
            guiInstance.handlePrevPage(p, this);
            return GuiResponse.CANCEL;
        }

        if (field.equals("next page")) {
            guiInstance.handleNextPage(p, this);
            return GuiResponse.CANCEL;
        }

        if (field.equals(GuiManager.EXTERNAL_FIELD)) {
            return guiInstance.handleExternalItem(p, item);
        } else {
            return guiInstance.handleOptionButton(new OptionButtonInformation(p, field, item, slot, this));
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public void addItem(String itemField, String... replacements) {
        addItem(ItemRegistry.get(guiInstance.plugin()).getItem(itemField, replacements));
    }
    public void addItem(ItemStack item, String... replacements) {
        inventory.addItem(ItemEditor.edit(item).replaceAll(replacements).finish());
    }

    public void setSlot(int slot, String itemField, String... replacements) {
        inventory.setItem(slot, ItemRegistry.get(guiInstance.plugin()).getItem(itemField, replacements));
        currentSlots.put(slot, new SlotItemFieldPair(GuiManager.EXTERNAL_FIELD, ""));
    }
    public void setSlot(int slot, ItemStack item, String... replacements) {
        inventory.setItem(slot, ItemEditor.edit(item).replaceAll(replacements).finish());
        currentSlots.put(slot, new SlotItemFieldPair(GuiManager.EXTERNAL_FIELD, ""));
    }

    public void removeItems(ItemStack... items) {
        inventory.removeItem(items);
    }

    public void addOptionButton(int slot, String slotField, String itemField) {
        savedSlots.put(slot, new SlotItemFieldPair(slotField, itemField));
        currentSlots.put(slot, new SlotItemFieldPair(slotField, itemField));
    }

    public void removeOptionButton(int slot) {
        savedSlots.remove(slot);
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    String getSlotField(int slot) {
        // If there are no option buttons in this slot, then it is either null or an external item
        // For optimization purposes we don't check the inventory to find if the item is null
        return currentSlots.getOrDefault(slot, new SlotItemFieldPair(GuiManager.EXTERNAL_FIELD, "")).slotField();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    void openFor(Player p, String... replacements) {
        if (shouldUpdateTitle) {
            updateFully(replacements);
        }

        updateOptionItems(replacements);

        p.openInventory(inventory);
    }

    void openFor(List<HumanEntity> entities, String... replacements) {
        if (shouldUpdateTitle) {
            updateFully(replacements);
        }

        updateOptionItems(replacements);

        for (HumanEntity entity : entities) {
            entity.openInventory(inventory);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public void updateOptionItems(String... replacements) {
        for (Integer i : savedSlots.keySet()) {
            updateOptionItem(i, replacements);
        }
    }

    public void updateOptionItem(String field, String... replacements) {
        for (Map.Entry<Integer, SlotItemFieldPair> entry : savedSlots.entrySet()) {
            if (entry.getValue().slotField().equals(field)) {
                updateOptionItem(entry.getKey(), replacements);
            }
        }
    }

    public void updateOptionItem(int slot, String... replacements) {
        ItemStack item = ItemRegistry.get(guiInstance.plugin()).getItem(savedSlots.get(slot).itemField());
        currentSlots.put(slot, savedSlots.get(slot));

        ItemStack edited = ItemEditor.edit(item).replaceAll(replacements).finish();
        inventory.setItem(slot, edited);
    }

    public void updateFully(String... replacements) {
        List<HumanEntity> viewers = inventory.getViewers();

        if (type != null) {
            if (title != null) {
                inventory = Bukkit.createInventory(this, type, MUtils.fastReplace(title, replacements));
            } else {
                inventory = Bukkit.createInventory(this, type);
            }
        } else {
            if (title != null) {
                inventory = Bukkit.createInventory(this, rows, MUtils.fastReplace(title, replacements));
            } else {
                inventory = Bukkit.createInventory(this, rows);
            }
        }

        openFor(viewers, replacements);
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    static GuiPage create(GuiInstance guiInstance, String title, int rows, InventoryType type) {
        String menuTitle = null;
        InventoryType menuType = null;
        int menuRows = -1;

        GuiPage menu = new GuiPage();

        ///////////////////////////////////////////////////////////////////////////////////
        Inventory base;
        if (type != null) {
            menuType = type;
            if (title != null) {
                base = Bukkit.createInventory(menu, type, title);
                menuTitle = title;
            } else {
                base = Bukkit.createInventory(menu, type);
            }
        } else {
            menuRows = rows;
            if (title != null) {
                base = Bukkit.createInventory(menu, rows*9, title);
                menuTitle = title;
            } else {
                base = Bukkit.createInventory(menu, rows*9);
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////

        if (menuTitle != null && menuTitle.contains("%")) menu.shouldUpdateTitle = true;

        menu.inventory = base;
        menu.guiInstance = guiInstance;
        menu.rows = menuRows;
        menu.title = menuTitle;
        menu.type = menuType;

        return menu;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
}