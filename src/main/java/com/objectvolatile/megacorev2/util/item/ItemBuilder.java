package com.objectvolatile.megacorev2.util.item;

import com.objectvolatile.megacorev2.util.MUtils;
import com.objectvolatile.megacorev2.util.oop.ColoredList;
import com.objectvolatile.megacorev2.util.oop.ColoredString;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ItemBuilder {

    private Material mat;
    private int amount;
    private short data;
    private String name;
    private boolean glowing = false;
    private List<String> lore = new ArrayList<>();
    private List<EnchantString> enchants = new ArrayList<>();

    public ItemBuilder() {
        amount = 1;
        data = 0;
    }

    public ItemBuilder copy(ItemStack is) {
        this.mat = is.getType();
        this.amount = is.getAmount();
        this.data = is.getData().getData();
        this.name = is.getItemMeta().getDisplayName();
        this.lore = is.getItemMeta().getLore();
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder material(Material mat) {
        this.mat = mat;
        return this;
    }

    public ItemBuilder material(String matName) {
        this.mat = Material.matchMaterial(matName);
        if (this.mat == null) {
            throw new IllegalArgumentException("Material name '"+matName+"' could not be resolved!");
        }
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder data(short data) {
        this.data = data;
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder name(String name) {
        if (name == null) return this;

        if (name.equals("")) this.name = "";
        else this.name = new ColoredString(name).applied();
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder lore(String... lore) {
        if (lore == null) return this;

        this.lore.clear();
        for (int size = lore.length, i = 0; i < size; i++) {
            this.lore.add(new ColoredString(lore[i]).applied());
        }
        return this;
    }
    public ItemBuilder lore(List<String> lore) {
        if (lore == null) return this;

        this.lore = new ColoredList(lore).applied();

        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder enchants(List<String> strs) {
        int size = strs.size();
        if (size == 0) return this;

        EnchantString[] esArray = new EnchantString[size];
        for (int i = 0; i < size; i++) {
            esArray[i] = new EnchantString(strs.get(i));
        }
        return enchants(esArray);
    }

    public ItemBuilder enchants(String... strs) {
        EnchantString[] esArray = new EnchantString[strs.length];
        for (int i = 0; i < strs.length; i++) {
            esArray[i] = new EnchantString(strs[i]);
        }
        return enchants(esArray);
    }

    private ItemBuilder enchants(EnchantString... enchants) {
        for (EnchantString estr : enchants) {
            if (estr.valid()) this.enchants.add(estr);
        }
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder replacedInName(String... replacements) {
        name = MUtils.fastReplace(name, replacements);
        return this;
    }

    public ItemBuilder replacedInLore(String... replacements) {
        for (int size = this.lore.size(), i = 0; i < size; i++) {
            this.lore.set(i, MUtils.fastReplace(this.lore.get(i), replacements));
        }
        return this;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    public ItemBuilder glow(boolean glow) {
        this.glowing = glow;
        return this;
    }

    public ItemBuilder glow() {
        return glow(true);
    }
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////

    public ItemStack build() {
        ItemStack item = new ItemStack(mat, amount, data);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) throw new RuntimeException("Impossible?"); // probably impossible but.. just to make sure
        meta.setDisplayName(name);
        meta.setLore(lore);

        for (EnchantString estr : enchants) {
            meta.addEnchant(estr.enchantment(), estr.level(), true);
        }

        if (enchants.isEmpty() && glowing) {
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);

        return item;
    }

}