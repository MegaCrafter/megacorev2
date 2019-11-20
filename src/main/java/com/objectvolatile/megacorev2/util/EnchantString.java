package com.objectvolatile.megacorev2.util;

import com.objectvolatile.megacorev2.MegaCore;
import org.bukkit.enchantments.Enchantment;

import java.util.Locale;
import java.util.logging.Level;

public class EnchantString {

    private Enchantment enchant;
    private int level;

    public EnchantString(Enchantment enchant, int level) {
        this.enchant = enchant;
        this.level = Math.max(0, level);
    }

    public EnchantString(String enchantName, int level) {
        this(Enchantment.getByName(MUtils.fastReplace(enchantName, " ", "_").toUpperCase(Locale.ENGLISH)), level);
    }

    public EnchantString(String token) {
        String[] split = token.split(":");
        if (split.length != 2) {
            MegaCore.logger.log(Level.WARNING, token + " could not be resolved to an enchant!");

            this.level = -1;

            return;
        }

        this.enchant = Enchantment.getByName(MUtils.fastReplace(split[0], " ", "_").toUpperCase(Locale.ENGLISH));

        try {
            this.level = Integer.parseInt(split[1]);
        } catch (NumberFormatException ignored) {
            this.level = -1;
        }


    }

    public Enchantment enchantment() {
        return enchant;
    }

    public int level() {
        return level;
    }

    public boolean valid() { return level != -1; }

}