package com.objectvolatile.megacorev2.gui;

import com.objectvolatile.megacorev2.ItemRegistry;
import com.objectvolatile.megacorev2.gui.info.ButtonInformation;
import com.objectvolatile.megacorev2.gui.info.ClickInformation;
import com.objectvolatile.megacorev2.gui.info.OptionButtonInformation;
import com.objectvolatile.megacorev2.util.MUtils;
import com.objectvolatile.megacorev2.util.item.ItemEditor;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GuiPage implements InventoryHolder {

    private final Map<Integer, SlotItemFieldPair> savedSlots = new HashMap<>();
    private final Map<Integer, SlotItemFieldPair> currentSlots = new HashMap<>();
    private final Map<String, List<Integer>> slots = new HashMap<>();

    private boolean shouldUpdateTitle = false;

    private final String title;
    private final InventoryType type;
    private final int rows;

    private final GuiInstance guiInstance;
    private Inventory inventory;
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public GuiResponse handleClick(Player p, String field, ClickType clickType, ItemStack item, int slot) {
        if (field.equals("prev page")) {
            guiInstance.handlePrevPage(p, this);
            return GuiResponse.CANCEL;
        }

        if (field.equals("next page")) {
            guiInstance.handleNextPage(p, this);
            return GuiResponse.CANCEL;
        }

        if (field.equals(GuiManager.EXTERNAL_FIELD)) {
            return guiInstance.handleExternalItem(new ButtonInformation(p, clickType, item, slot, this));
        } else if (field.equals(GuiManager.OTHERINV_FIELD)) {
            return guiInstance.handleOtherInventory(new ClickInformation(p, clickType, item, slot));
        } else {
            GuiResponse res = guiInstance.handleOptionButton(new OptionButtonInformation(p, field, clickType, item, slot, this));

            if (res == GuiResponse.NOT_HANDLED) {
                applyCommand(p, field);
                res = GuiResponse.CANCEL;
            }

            return res;
        }
    }

    public void handleClose(Player p) {
        guiInstance.handleClose(p, this);
    }

    public boolean canPutItems() {
        return guiInstance.canPutItems;
    }

    public void applyCommand(Player p, String field) {
        guiInstance.applyCommand(p, field);
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

        List<Integer> list = slots.get(slotField);
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(slot);

        slots.put(slotField, list);
    }

    public void removeOptionButton(int slot) {
        savedSlots.remove(slot);

        slots.getOrDefault(getSlotField(slot), new ArrayList<>()).remove((Integer)slot);
    }

    public void setItem(String slotField, String itemField) {
        for (int slot : getSlots(slotField)) {
            addOptionButton(slot, slotField, itemField);
        }
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

    List<Integer> getSlots(String slotField) {
        return new ArrayList<>(slots.getOrDefault(slotField, new ArrayList<>()));
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
        openFor(p, true, replacements);
    }

    void openFor(Player p, boolean update, String... replacements) {
        if (shouldUpdateTitle && update) {
            updateFully(replacements);
        }

        if (update) updateOptionItems(replacements);

        p.openInventory(inventory);
    }

    void openFor(List<HumanEntity> entities, String... replacements) {
        openFor(entities, true, replacements);
    }

    void openFor(List<HumanEntity> entities, boolean update, String... replacements) {
        if (shouldUpdateTitle && update) {
            updateFully(replacements);
        }

        if (update) updateOptionItems(replacements);

        for (HumanEntity entity : entities) {
            entity.openInventory(inventory);
        }
    }

    synchronized void closeForAll() {
        ArrayList<HumanEntity> list = new ArrayList<>(inventory.getViewers());

        list.forEach(HumanEntity::closeInventory);
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public void updateOptionItems(String... replacements) {
        for (Integer i : savedSlots.keySet()) {
            if (savedSlots.get(i).itemField() == null) continue;
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
                inventory = Bukkit.createInventory(this, rows*9, MUtils.fastReplace(title, replacements));
            } else {
                inventory = Bukkit.createInventory(this, rows*9);
            }
        }

        this.shouldUpdateTitle = false;
        openFor(viewers, replacements);
        this.shouldUpdateTitle = true;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    GuiPage(GuiInstance guiInstance, String title, int rows, InventoryType type) {
        String menuTitle = null;
        InventoryType menuType = null;
        int menuRows = -1;

        ///////////////////////////////////////////////////////////////////////////////////
        Inventory base;
        if (type != null) {
            menuType = type;
            if (title != null) {
                base = Bukkit.createInventory(this, type, title);
                menuTitle = title;
            } else {
                base = Bukkit.createInventory(this, type);
            }
        } else {
            menuRows = rows;
            if (title != null) {
                base = Bukkit.createInventory(this, rows*9, title);
                menuTitle = title;
            } else {
                base = Bukkit.createInventory(this, rows*9);
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////

        if (menuTitle != null && menuTitle.contains("%")) this.shouldUpdateTitle = true;

        this.inventory = base;
        this.guiInstance = guiInstance;
        this.rows = menuRows;
        this.title = menuTitle;
        this.type = menuType;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
}