package com.objectvolatile.megacorev2.util;

import org.bukkit.Material;

import java.util.*;

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

    public static String buildTextFrom(String[] textArray, int textStartIndex) {
        if (textStartIndex < 0 || textStartIndex >= textArray.length) return "";

        StringBuilder builder = new StringBuilder();
        for (int i = textStartIndex; i < textArray.length; i++) {
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

}