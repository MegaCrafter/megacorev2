package com.objectvolatile.megacorev2.gui;

import com.objectvolatile.megacorev2.ItemRegistry;
import com.objectvolatile.megacorev2.MegaYaml;
import com.objectvolatile.megacorev2.gui.info.ButtonInformation;
import com.objectvolatile.megacorev2.gui.info.ClickInformation;
import com.objectvolatile.megacorev2.gui.info.OptionButtonInformation;
import com.objectvolatile.megacorev2.util.MUtils;
import com.objectvolatile.megacorev2.util.oop.Clamped;
import com.objectvolatile.megacorev2.util.oop.ColoredString;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public abstract class GuiInstance {

    private ArrayList<GuiPage> pages;
    private List<ItemStack> externalItems;
    private Map<ItemStack, String[]> exItemReplacements;
    private String[] defaultReplacements;

    private Map<String, String> fieldCommands;

    private final Plugin plugin;

    private int minimumPageCount;
    private int externalFirst;
    private int externalLimit;

    private Map<Integer, SlotItemFieldPair> nopageSlots;
    private String title = null;
    private int rows = 0;
    private InventoryType type = null;

    protected boolean canPutItems = false;

    private Object dataHolder = null;

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public GuiInstance(Plugin plugin, String confName) {
        this(plugin, new MegaYaml(plugin, confName, true).loaded().options());
    }

    public GuiInstance(Plugin plugin, FileConfiguration guiConf) {
        this.plugin = plugin;

        this.fieldCommands = new HashMap<>();

        initPages(guiConf);
    }

    public GuiInstance(Plugin plugin, String confName, Object dataHolder) {
        this(plugin, new MegaYaml(plugin, confName, true).loaded().options(), dataHolder);
    }

    public GuiInstance(Plugin plugin, FileConfiguration guiConf, Object dataHolder) {
        this.plugin = plugin;

        this.fieldCommands = new HashMap<>();

        this.dataHolder = dataHolder;

        initPages(guiConf);
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public final Plugin plugin() {
        return plugin;
    }

    public final GuiPage page(int page) { return pages.get(page); }

    public Object dataHolder() {
        return dataHolder;
    }

    public final void addExternalItem(ItemStack item) {
        externalItems.add(item);
    }

    public final void clearExternalItems() { externalItems.clear(); }

    public final void removeExternalItem(ItemStack item) {
        externalItems.remove(item);
    }

    public final boolean nextExternalCreatesPage() {
        int maxItems = pages.size() * externalLimit;

        // Gui initially has some pages independent from external items
        // so we get the max items we can put in those independent pages.
        // If the gui holds the max number of items it will create a dependent page
        // to put the next item. So return true.

        return externalItems.size() == maxItems;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public final void openFor(Player p, String... replacements) {
        openFor(p, 0, replacements);
    }

    public void openFor(Player p, int pageIndex, String... replacements) {
        openFor(p, pageIndex, true, replacements);
    }

    public final void openFor(Player p, boolean update, String... replacements) {
        openFor(p, 0, update, replacements);
    }

    public void openFor(Player p, int pageIndex, boolean update, String... replacements) {
        this.defaultReplacements = replacements;

        updatePages();

        GuiPage page = pages.get(pageIndex);
        page.openFor(p, update, replacements);

        handleOpen(p, page);
    }

    public final synchronized void closeForAll() {
        for (GuiPage page : pages) {
            page.closeForAll();
        }
    }

    public final void updateOptionItem(String field, String... replacements) {
        for (GuiPage page : pages) {
            page.updateOptionItem(field, replacements);
        }
    }

    public final void updateFully(String... replacements) {
        for (GuiPage page : pages) {
            page.updateFully(replacements);
        }
    }

    public final void setItem(String slotField, String itemField) {
        for (GuiPage page : pages) {
            page.setItem(slotField, itemField);
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    final void handlePrevPage(Player p, GuiPage page) {
        int index = pages.indexOf(page) - 1;
        if (index < 0) return;

        openFor(p, index, defaultReplacements);
    }

    final void handleNextPage(Player p, GuiPage page) {
        int index = pages.indexOf(page) + 1;
        if (index >= pages.size()) return;

        openFor(p, index, defaultReplacements);
    }

    public abstract GuiResponse handleOptionButton(OptionButtonInformation info);
    public abstract GuiResponse handleExternalItem(ButtonInformation info);
    public abstract GuiResponse handleOtherInventory(ClickInformation info);
    public String[] getCommandReplacements() { return new String[0]; }
    public void handleOpen(Player p, GuiPage page) { }
    public void handleClose(Player p, GuiPage page) { }

    final void applyCommand(Player p, String field) {
        String command = fieldCommands.get(field);
        if (command == null) return;

        String[] otherRep = getCommandReplacements();
        String[] replacements = new String[otherRep.length + 2];
        replacements[0] = "%player%";
        replacements[1] = p.getName();

        System.arraycopy(otherRep, 0, replacements, 2, otherRep.length);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), MUtils.fastReplace(command, replacements));
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    private void updatePages() {
        int externalItemPageNeed = (int) Math.ceil((float) exItemReplacements.size() / externalLimit);

        // We need more pages to put items
        if (externalItemPageNeed > pages.size()) {
            createNewPages(externalItemPageNeed - pages.size());
        }

        // We have more pages than we need
        if (minimumPageCount < pages.size() && externalItemPageNeed < pages.size()) {
            int max = Math.max(minimumPageCount, externalItemPageNeed);

            removePages(pages.size() - max);
        }

        int itemIndex = 0;

        for (int pageIndex = 0, pageSize = pages.size(); pageIndex < pageSize; pageIndex++) {
            GuiPage page = pages.get(pageIndex);
            for (int i = 0, invsize = page.getInventory().getSize(); i < invsize; i++) {
                // pageIndex * externalLimit + i

                if (i >= externalFirst && i < externalFirst + externalLimit - 1 && itemIndex < externalItems.size()) {

                    ItemStack item = externalItems.get(itemIndex++);
                    page.setSlot(i, item, exItemReplacements.get(item));

                } else if (page.getSlotField(i).equals(GuiManager.EXTERNAL_FIELD)) {

                    page.getInventory().clear(i);

                }
            }
        }
    }

    private void createNewPages(int count) {
        pages.ensureCapacity(pages.size() + count);

        for (int i = 0; i < count; i++) {
            GuiPage page = new GuiPage(this, MUtils.fastReplace(title, "%page%", ""+(pages.size() + 1)), rows, type);

            for (Map.Entry<Integer, SlotItemFieldPair> npEntry : nopageSlots.entrySet()) {
                if (i == 0 && npEntry.getValue().slotField().equals("prev page")) continue; // no prev page on first page
                if (i == count-1 && npEntry.getValue().slotField().equals("next page")) continue; // no next page on last page

                page.addOptionButton(npEntry.getKey(), npEntry.getValue().slotField(), npEntry.getValue().itemField());
            }

            pages.add(page);
        }
    }

    private void removePages(int count) {
        int from = Math.max(minimumPageCount, pages.size() - count);
        pages.subList(from, pages.size()).clear();
        pages.trimToSize();
    }

    // Rows option is dominant over type option
    // slot option is dominant over x-y options
    private void initPages(FileConfiguration guiConf) {
        int pageCount = 1;

        ///////////////////////////////////////////////////////////////////////////////////
        rows = guiConf.getInt("rows", 0);
        if (rows < 0 || rows > 6) {
            throw new IllegalArgumentException("Rows cannot be less than 0 or greater than 6!");
        }

        if (rows == 0) {
            String typeStr = guiConf.getString("type");
            if (typeStr == null) throw new IllegalArgumentException("No row or type information found!");

            try {
                type = InventoryType.valueOf(typeStr.toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("No inventory type found as " + typeStr.toUpperCase(Locale.ENGLISH) + "!");
            }
        }

        title = guiConf.getString("title");
        if (title != null) title = new ColoredString(title).applied();

        int invSize = (rows == 0) ? type.getDefaultSize() : rows*9;

        externalFirst = guiConf.getInt("external-first", 0);
        int externalLast = guiConf.getInt("external-last", invSize - 1);
        // externalLast is the last slot by default

        if (externalFirst < 0) externalFirst = 0;
        if (externalLast > invSize - 1) externalLast = invSize - 1;
        if (externalFirst > externalLast) throw new IllegalArgumentException("external-first cannot be bigger than external-last!");

        externalLimit = externalLast - externalFirst + 1;
        ///////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////
        nopageSlots = new HashMap<>();
        Map<Integer, SlotItemFieldPair> pagedSlots = new HashMap<>();

        ConfigurationSection optionButtons = guiConf.getConfigurationSection("option-buttons");
        if (optionButtons != null) {
            for (String slotField : optionButtons.getKeys(false)) {
                String itemField = optionButtons.getString(slotField+".item");

                SlotItemFieldPair sifPair = new SlotItemFieldPair(slotField, itemField);

                Map<Integer, SlotItemFieldPair> mapToPut;

                Object pageObject = optionButtons.get(slotField+".page");
                int pageIndex = 0;

                if (pageObject instanceof Integer && !slotField.equals("prev page") && !slotField.equals("next page")) {
                    pageIndex = (int) pageObject;
                    if (pageIndex < 0) pageIndex = 0;

                    mapToPut = pagedSlots;
                } else {
                    mapToPut = nopageSlots;
                }

                if (pageIndex + 1 > pageCount) pageCount = pageIndex + 1;

                Object slotObject = optionButtons.get(slotField+".slot");
                Object xObject = optionButtons.get(slotField+".x");
                Object yObject = optionButtons.get(slotField+".y");

                if (slotObject instanceof Integer) {
                    int slot = (int) slotObject;
                    slot = new Clamped<>(slot, 0, invSize - 1).value();

                    mapToPut.put(pageIndex*invSize + slot, sifPair);

                } else if (slotObject instanceof String) {
                    String slots = (String) slotObject;
                    String[] slotSplit = slots.split(",");

                    for (String slotString : slotSplit) {
                        try {
                            int slot = Integer.parseInt(slotString);
                            slot = new Clamped<>(slot, 0, invSize - 1).value();

                            mapToPut.put(pageIndex*invSize + slot, sifPair);

                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Found invalid slot for " + slotField);
                        }
                    }

                } else if (rows == 0) {
                    throw new IllegalArgumentException("x-y definition is not supported for type inventories [" + slotField + "]");

                } else if (xObject instanceof Integer && yObject instanceof Integer) {
                    int x = (int) xObject;
                    int y = (int) yObject;

                    x = new Clamped<>(x, 0, 8).value();
                    y = new Clamped<>(y, 0, rows-1).value();

                    mapToPut.put(pageIndex*invSize + y*9 + x, sifPair);
                }

                String command = optionButtons.getString(slotField+".command");

                if (command != null) {
                    if (!command.startsWith("/")) {
                        command = "/" + command;
                    }

                    fieldCommands.put(slotField, command);
                }
            }
        }
        ///////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////
        List<String> externalItemList = guiConf.getStringList("external-items");
        externalItems = new ArrayList<>(externalItemList.size());
        exItemReplacements = new HashMap<>(externalItemList.size());
        for (String itemFieldName : externalItemList) {
            externalItems.add(ItemRegistry.get(plugin).getItem(itemFieldName));
            exItemReplacements.put(ItemRegistry.get(plugin).getItem(itemFieldName), new String[0]);
        }
        ///////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////
        minimumPageCount = pageCount;
        pages = new ArrayList<>(pageCount);
        createNewPages(pageCount);

        for (Map.Entry<Integer, SlotItemFieldPair> pEntry : pagedSlots.entrySet()) {
            int pageOfItem = (int) Math.floor(pEntry.getKey() / (float) invSize);
            int slot = pEntry.getKey() % invSize;

            pages.get(pageOfItem).addOptionButton(slot, pEntry.getValue().slotField(), pEntry.getValue().itemField());
        }
        ///////////////////////////////////////////////////////////////////////////////////
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

}