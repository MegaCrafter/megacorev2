package com.objectvolatile.megacorev2.util;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public final class CommandArgsHelper {

    private final Plugin plugin;
    private final String field;

    // alias to arg name
    private final HashMap<String, String> argsMap = new HashMap<>();

    // arg to alias list
    private final HashMap<String, Set<String>> aliases = new HashMap<>();

    public CommandArgsHelper(Plugin plugin, String field) {
        this.plugin = plugin;
        this.field = field;

        reload();
    }

    public void reload() {
        if (!argsMap.isEmpty()) argsMap.clear();
        if (!aliases.isEmpty()) aliases.clear();

        ConfigurationSection argsSection = this.plugin.getConfig().getConfigurationSection(field);

        if (argsSection == null) {
            throw new IllegalStateException("Could not find '" + field + "' in config.yml!");
        }

        for (String key : argsSection.getKeys(false)) {
            Set<String> set = new HashSet<>();

            aliases.put(key, set);

            for (String alias : argsSection.getStringList(key)) {
                argsMap.put(alias, key);
                set.add(alias);
            }
        }
    }

    @Nullable
    public String getGroupOf(String given) {
        return argsMap.get(given);
    }

    @Nullable
    public Set<String> getAliasesOf(String group) {
        return aliases.get(group);
    }

    @Nullable
    public String getAliasOf(String group) {
        Set<String> set = getAliasesOf(group);
        if (set == null) return null;

        return set.iterator().next();
    }
}