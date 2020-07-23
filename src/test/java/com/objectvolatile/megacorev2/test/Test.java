package com.objectvolatile.megacorev2.test;

import com.objectvolatile.abstractionapi.nmsinterface.ColorNames;
import com.objectvolatile.megacorev2.util.lang.LangMessage;
import com.objectvolatile.megacorev2.util.lang.RawJsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        String text = "%prefix% &6&l[inviter] &boyuncusu seni arkadaş olmaya davet etti! $1{&a[&lKABUL ET&r&a]} $2{&c&l[REDDET]}";

        LangMessage msg = new LangMessage(null, text);
    }

    private static HashMap<Integer, List<Integer>> captureIndices = new HashMap<>();

    private static String parseToJson(String text) {
        RawJsonBuilder json = new RawJsonBuilder();

        StringBuilder strb = new StringBuilder();
        StringBuilder numberBuilder = new StringBuilder();

        String colorName = "white";
        boolean bold = false;
        boolean italic = false;
        boolean strike = false;
        boolean underline = false;
        boolean obf = false;

        int inGroup = -1;
        boolean gettingGroupNumber = false;
        boolean gettingInGroup = false;

        int objIndex = 1;
        char[] chArray = text.toCharArray();
        for (int i = 0; i < chArray.length; i++) {
            char ch = chArray[i];

            if (ch == '&') {
                if (i == chArray.length-1) {
                    strb.append(ch);

                    json.appendText(strb.toString());

                    json.color(colorName);
                    if (bold) json.bold();
                    if (obf) json.obfuscated();
                    if (italic) json.italic();
                    if (strike) json.strikethrough();
                    if (underline) json.underlined();

                    break;
                }

                ch = chArray[i+1];
                if (isApplicableChar(ch)) {
                    if (strb.length() != 0) {
                        json.appendText(strb.toString());

                        json.color(colorName);
                        if (bold) json.bold();
                        if (obf) json.obfuscated();
                        if (italic) json.italic();
                        if (strike) json.strikethrough();
                        if (underline) json.underlined();

                        strb = new StringBuilder();

                        if (gettingInGroup) {
                            captureIndices.get(inGroup).add(objIndex);
                        }

                        objIndex++;
                    }

                    if (isColorChar(ch)) {
                        colorName = ColorNames.getName(ch);

                    } else if (ch == 'l') {
                        bold = true;

                    } else if (ch == 'k') {
                        obf = true;

                    } else if (ch == 'o') {
                        italic = true;

                    } else if (ch == 'm') {
                        strike = true;

                    } else if (ch == 'n') {
                        underline = true;

                    } else if (ch == 'r') {
                        colorName = "white";
                        bold = false;
                        italic = false;
                        strike = false;
                        underline = false;
                        obf = false;
                    }
                }

                i += 1; // skip the next character
                continue;
            } else if (ch == '$') {
                gettingGroupNumber = true;
                continue;
            } else if (ch == '{' && gettingGroupNumber) {
                gettingGroupNumber = false;
                gettingInGroup = true;

                inGroup = Integer.parseInt(numberBuilder.toString());
                numberBuilder = new StringBuilder();

                captureIndices.put(inGroup, new ArrayList<>());

                if (strb.length() != 0) {
                    json.appendText(strb.toString());

                    json.color(colorName);
                    if (bold) json.bold();
                    if (obf) json.obfuscated();
                    if (italic) json.italic();
                    if (strike) json.strikethrough();
                    if (underline) json.underlined();

                    strb = new StringBuilder();

                    objIndex++;
                }

                continue;
            } else if (ch == '}' && gettingInGroup) {
                gettingInGroup = false;

                if (strb.length() != 0) {
                    json.appendText(strb.toString());

                    json.color(colorName);
                    if (bold) json.bold();
                    if (obf) json.obfuscated();
                    if (italic) json.italic();
                    if (strike) json.strikethrough();
                    if (underline) json.underlined();

                    strb = new StringBuilder();

                    captureIndices.get(inGroup).add(objIndex);

                    objIndex++;
                }

                continue;
            } else if (gettingGroupNumber) {
                numberBuilder.append(ch);
                continue;
            }

            strb.append(ch);
        }

        return json.getAsString();
    }

    private static boolean isApplicableChar(char ch) {
        return isColorChar(ch) || (ch == 'l') || (ch == 'r') || (ch == 'o') || (ch == 'k') || (ch == 'n') || (ch == 'm');
    }

    private static boolean isColorChar(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f');
    }

}