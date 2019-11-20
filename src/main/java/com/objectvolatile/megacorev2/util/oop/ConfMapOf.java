package com.objectvolatile.megacorev2.util.oop;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfMapOf {

    private FileConfiguration conf;
    public ConfMapOf(FileConfiguration conf) {
        this.conf = conf;
    }

    public Map<String, Object> map() {
        Set<String> keys = conf.getKeys(true);

        Map<String, Object> map = new HashMap<>(keys.size());

        for (String key : keys) {
            map.put(key, conf.get(key));
        }

        return map;
    }
}