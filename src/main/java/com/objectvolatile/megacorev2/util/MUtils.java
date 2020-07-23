package com.objectvolatile.megacorev2.util;

import com.objectvolatile.megacorev2.util.item.ItemEditor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MUtils {

    public static String fastReplace(String string, String... replacements) {
        if (replacements.length == 0) return string;

        StringBuilder sb = new StringBuilder(string);

        if (replacements.length % 2 != 0) throw new IllegalArgumentException("from-to pairing is wrong!");

        for (int i = 0; i < replacements.length-1; i+=2) {
            String key = replacements[i];
            String value = replacements[i+1];

            int start = sb.indexOf(key, 0);
            while (start > -1) {
                int end = start + key.length();
                int nextSearchStart = start + value.length();
                sb.replace(start, end, value);
                start = sb.indexOf(key, nextSearchStart);
            }
        }
        return sb.toString();
    }

    public static List<String> fastReplace(List<String> list, String... replacements) {
        if (replacements.length == 0) return list;

        List<String> returnList = new ArrayList<>(list.size());
        for (String string : list) {
            StringBuilder sb = new StringBuilder(string);

            if (replacements.length % 2 != 0) throw new IllegalArgumentException("from-to pairing is wrong!");

            for (int i = 0; i < replacements.length - 1; i += 2) {
                String key = replacements[i];
                String value = replacements[i + 1];

                int start = sb.indexOf(key, 0);
                while (start > -1) {
                    int end = start + key.length();
                    int nextSearchStart = start + value.length();
                    sb.replace(start, end, value);
                    start = sb.indexOf(key, nextSearchStart);
                }
            }
            returnList.add(sb.toString());
        }

        return returnList;
    }

    public static <T> void sortAndApply(Map<T, ? extends Number> map, SortedMapApplier<T> applier) {
        List<T> search = new ArrayList<>(map.keySet());
        List<Number> valuelist = new ArrayList<>(new HashSet<>(map.values()));
        Map<Number, List<T>> reverse = new HashMap<>(valuelist.size());

        for (int i = valuelist.size() - 1; i >= 0; i--) {
            Number hitvalue = valuelist.get(i);

            List<T> owners = new ArrayList<>(4);
            for (int j = search.size() - 1; j >= 0; j--) {
                T owner = search.get(j);

                if (map.get(owner) == hitvalue) {
                    search.remove(owner);
                    owners.add(owner);
                }
            }

            reverse.put(hitvalue, owners);
        }

        List<Number> sortedValuesList = new ArrayList<>(reverse.keySet());

        Number[] sortedValues = new Number[sortedValuesList.size()];
        for (int i = sortedValuesList.size() - 1; i >= 0; i--) {
            sortedValues[i] = sortedValuesList.get(i);
        }

        mergesort(sortedValues);

        for (int i = sortedValues.length - 1; i >= 0; i--) {
            List<T> owners = reverse.get(sortedValues[i]);
            for (int j = owners.size() - 1; j >= 0; j--) {
                applier.apply(owners.get(j), sortedValues[i]);
            }
        }
    }

    public static <T> Map<T, ? extends Number> sortReturnMap(Map<T, ? extends Number> map) {
        Map<T, Number> toret = new HashMap<>();

        sortAndApply(map, toret::put);

        return toret;
    }

    private static void mergesort(Number[] array) {
        if (array.length == 1) return;
        if (array.length == 2) {
            if (array[0].floatValue() > array[1].floatValue()) {
                Number temp = array[0];
                array[0] = array[1];
                array[1] = temp;
            }
            return;
        }

        int midpoint = array.length / 2;
        Number[] part1 = new Number[midpoint];
        Number[] part2 = new Number[array.length - midpoint];

        System.arraycopy(array, 0, part1, 0, part1.length);
        System.arraycopy(array, midpoint, part2, 0, part2.length);

        mergesort(part1);
        mergesort(part2);

        int arrayIndex = 0;
        int index1 = 0;
        int index2 = 0;

        while (index1 < part1.length && index2 < part2.length) {
            if (part1[index1].floatValue() <= part2[index2].floatValue()) {
                array[arrayIndex] = part1[index1];
                index1++;
            } else {
                array[arrayIndex] = part2[index2];
                index2++;
            }
            arrayIndex++;
        }

        while (index1 < part1.length) {
            array[arrayIndex] = part1[index1];
            index1++;
            arrayIndex++;
        }

        while (index2 < part2.length) {
            array[arrayIndex] = part2[index2];
            index2++;
            arrayIndex++;
        }
    }

    interface SortedMapApplier<T> {

        void apply(T key, Number value);

    }

    public static String buildTextFrom(String[] textArray) {
        return buildTextFrom(textArray, 0);
    }

    public static String buildTextFrom(String[] textArray, int textStartIndex) {
        return buildTextFrom(textArray, textStartIndex, 0);
    }

    public static String buildTextFrom(String[] textArray, int textStartIndex, int ignoreLast) {
        if (textStartIndex < 0 || textStartIndex >= textArray.length) return "";

        StringBuilder builder = new StringBuilder();
        for (int i = textStartIndex; i < textArray.length - ignoreLast; i++) {
            builder.append(i == textStartIndex ? "" : " ").append(textArray[i]);
        }

        return builder.toString();
    }

    public static String enumAsName(Enum en) {
        String[] enName = fastReplace(en.name().toLowerCase(Locale.ENGLISH), "_", " ").split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < enName.length; i++) {
            enName[i] = (enName[i].charAt(0) + "").toUpperCase(Locale.ENGLISH) + enName[i].substring(1);
            builder.append(i == 0 ? "" : " ").append(enName[i]);
        }
        return builder.toString();
    }

    public static String materialAsName(Material mat) {
        String[] enName = fastReplace(mat.name().toLowerCase(Locale.ENGLISH), "_", " ").split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < enName.length; i++) {
            enName[i] = (enName[i].charAt(0) + "").toUpperCase(Locale.ENGLISH) + enName[i].substring(1);
            builder.append(i == 0 ? "" : " ").append(enName[i]);
        }
        return builder.toString();
    }

    public static void saveResourceSilent(Plugin plugin, String filePath) {
        InputStream inputStream = plugin.getResource(filePath);
        File file = new File(plugin.getDataFolder(), filePath);
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdir();
                file.getParentFile().mkdir();
                file.createNewFile();

                IOUtils.copyContents(inputStream, new FileOutputStream(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void giveOrDrop(Player p, ItemStack... items) {
        List<ItemStack> itemList = new ArrayList<>(items.length);
        for (ItemStack item : items) {
            if (item != null) itemList.add(item);
        }

        HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(itemList.toArray(new ItemStack[0]));
        for (ItemStack item : leftover.values()) {
            p.getWorld().dropItem(p.getLocation(), item);
        }
    }

    public static void giveOrDrop(Player p, ItemStack base, int amount) {
        int overstack = amount % 64;
        int stacks = (amount - overstack) / 64;
        ItemStack[] items = new ItemStack[stacks+1];
        for (int i = 0; i < stacks+1; i++) {
            ItemStack safebase = base.clone();
            safebase.setAmount((i == stacks) ? overstack : 64);
            items[i] = safebase;
        }

        HashMap<Integer, ItemStack> leftover = p.getInventory().addItem(items);
        for (ItemStack item : leftover.values()) {
            p.getWorld().dropItem(p.getLocation(), item);
        }
    }

    public static void removeAddedLore(ItemStack loreditem, List<String> added) {
        if (loreditem != null && loreditem.hasItemMeta() && loreditem.getItemMeta().hasLore()) {
            List<String> lore = loreditem.getItemMeta().getLore();

            if (added.size() > lore.size()) return;

            for (int i = 1; i <= added.size(); i++) {
                if (!lore.get(lore.size() - i).equals(added.get(added.size() - i))) return;
            }

            ItemEditor.editUnsafe(loreditem)
                    .removeLoreLines(added.size())
                    .finish();
        }
    }

    public static BlockFace getDirection8(Player player) {
        double rotation = (player.getLocation().getYaw()) % 360;
        if (rotation < 0) {
            rotation += 360;
        }

        if (0 <= rotation && rotation < 22.5) {
            return BlockFace.NORTH;
        } else if (22.5 <= rotation && rotation < 67.5) {
            return BlockFace.NORTH_EAST;
        } else if (67.5 <= rotation && rotation < 112.5) {
            return BlockFace.EAST;
        } else if (112.5 <= rotation && rotation < 157.5) {
            return BlockFace.SOUTH_EAST;
        } else if (157.5 <= rotation && rotation < 202.5) {
            return BlockFace.SOUTH;
        } else if (202.5 <= rotation && rotation < 247.5) {
            return BlockFace.SOUTH_WEST;
        } else if (247.5 <= rotation && rotation < 292.5) {
            return BlockFace.WEST;
        } else if (292.5 <= rotation && rotation < 337.5) {
            return BlockFace.NORTH_WEST;
        } else if (337.5 <= rotation && rotation < 360.0) {
            return BlockFace.NORTH;
        } else {
            return null;
        }
    }

    public static BlockFace getDirection4(Player player) {
        double rotation = (player.getLocation().getYaw()) % 360;
        if (rotation < 0) {
            rotation += 360;
        }

        if (0 <= rotation && rotation < 45) {
            return BlockFace.NORTH;
        } else if (45 <= rotation && rotation < 135) {
            return BlockFace.EAST;
        } else if (135 <= rotation && rotation < 225) {
            return BlockFace.SOUTH;
        }  else if (225 <= rotation && rotation < 315) {
            return BlockFace.WEST;
        }  else if (315 <= rotation && rotation < 360.0) {
            return BlockFace.NORTH;
        } else {
            return null;
        }
    }

    public static String replaceWrapper(String text, String pattern, int group, Function<String, String> replacer, BiConsumer<Integer, String> consumer) {
        Matcher matcher = Pattern.compile(pattern).matcher(text);

        StringBuilder builder = new StringBuilder(text);

        int sub = 0;
        while (matcher.find()) {
            String rep = replacer.apply(matcher.group(group));
            if (rep != null) {
                builder.replace(matcher.start() - sub, matcher.end() - sub, rep);
                int len = matcher.end() - matcher.start();
                int len2 = rep.length();

                sub += (len - len2);
            }

            if (consumer != null) {
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    consumer.accept(i, matcher.group(i));
                }
            }
        }

        return builder.toString();
    }
}