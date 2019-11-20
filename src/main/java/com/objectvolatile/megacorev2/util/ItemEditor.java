package com.objectvolatile.megacorev2.util;

import com.objectvolatile.megacorev2.util.oop.ColoredList;
import com.objectvolatile.megacorev2.util.oop.ColoredString;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class ItemEditor {

    private ItemStack baseItem;

    private String toName = null;
    private List<String> toLore = null;
    private boolean glowing = false;

    private ItemEditor(ItemStack baseItem) {
        if (baseItem == null) throw new NullPointerException("Cannot edit null item!");

        this.baseItem = baseItem;

        if (baseItem.hasItemMeta()) {
            if (baseItem.getItemMeta().hasDisplayName()) {
                toName = baseItem.getItemMeta().getDisplayName();
            }

            if (baseItem.getItemMeta().hasLore()) {
                toLore = baseItem.getItemMeta().getLore();
            }
        }
    }

    public static ItemEditor edit(ItemStack item) {
        return editUnsafe(item.clone());
    }

    public static ItemEditor editUnsafe(ItemStack item) {
        return new ItemEditor(item);
    }

    public ItemStack finish() {
        if (baseItem.hasItemMeta()) {
            ItemMeta meta = baseItem.getItemMeta();

            if (toName != null) {
                meta.setDisplayName(toName);
            }

            if (toLore != null) {
                meta.setLore(toLore);
            }

            if (glowing) {
                meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            baseItem.setItemMeta(meta);
        }

        return baseItem;
    }

    public ItemEditor changeNameTo(String name) {
        if (name == null) return this;

        this.toName = new ColoredString(name).applied();
        return this;
    }

    public ItemEditor changeLoreTo(List<String> lore) {
        if (lore == null) return this;

        this.toLore = new ColoredList(lore).applied();

        return this;
    }

    public ItemEditor addToLore(List<String> lore) {
        if (lore == null) return this;
        if (this.toLore == null) return changeLoreTo(lore);

        this.toLore.addAll(new ColoredList(lore).applied());

        return this;
    }

    public ItemEditor addToLore(String... lore) {
        return addToLore(Arrays.asList(lore));
    }

    public ItemEditor changeLoreTo(String... lore) {
        return changeLoreTo(Arrays.asList(lore));
    }

    public ItemEditor replaceInName(String... replacements) {
        if (replacements == null) return this;

        if (this.toName == null) {
            if (baseItem.getItemMeta() == null) return this;

            String itemname = baseItem.getItemMeta().getDisplayName();

            this.toName = itemname;
        }

        this.toName = MUtils.fastReplace(this.toName, replacements);
        return this;
    }

    public ItemEditor replaceInLore(String... replacements) {
        if (replacements == null) return this;

        if (this.toLore == null) {
            if (baseItem.getItemMeta() == null) return this;

            List<String> itemlore = baseItem.getItemMeta().getLore();

            if (itemlore == null) return this;

            this.toLore = itemlore;
        }

        for (int size = this.toLore.size(), i = 0; i < size; i++) {
            this.toLore.set(i, MUtils.fastReplace(this.toLore.get(i), replacements));
        }

        return this;
    }

    public ItemEditor replaceAll(String... replacements) {
        return replaceInName(replacements).replaceInLore(replacements);
    }

    public ItemEditor setGlow(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public boolean isGlowing() {
        return (baseItem.hasItemMeta() && baseItem.getItemMeta().hasEnchant(Enchantment.DAMAGE_ALL) && baseItem.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
    }

}