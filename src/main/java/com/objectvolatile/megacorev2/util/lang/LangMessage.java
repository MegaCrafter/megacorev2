package com.objectvolatile.megacorev2.util.lang;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.objectvolatile.abstractionapi.nmsinterface.ChatBaseComponentUtil;
import com.objectvolatile.abstractionapi.nmsinterface.ColorNames;
import com.objectvolatile.megacorev2.AbstractionUtil;
import com.objectvolatile.megacorev2.util.MUtils;
import com.objectvolatile.megacorev2.util.oop.ColoredString;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LangMessage {

    private final List<String> jsonStrings;
    private final List<String> rawStrings;
    private LineCaptureIndices[] captureIndices;
    private LineInfo[] lineInfos;

    private final AbstractionUtil abstraction;

    public LangMessage(AbstractionUtil abstraction, List<String> list) {
        this.abstraction = abstraction;
        this.jsonStrings = new ArrayList<>(list.size());
        this.rawStrings = new ArrayList<>(list.size());

        this.captureIndices = new LineCaptureIndices[list.size()];
        for (int i = 0; i < this.captureIndices.length; i++) {
            this.captureIndices[i] = new LineCaptureIndices(new HashMap<>());
        }

        this.lineInfos = new LineInfo[list.size()];
        for (int i = 0; i < this.lineInfos.length; i++) {
            this.lineInfos[i] = new LineInfo();
        }

        int i = 0;
        for (String str : list) {
            this.jsonStrings.add(parseToJson(str, i++));
            this.rawStrings.add(new ColoredString(str).applied());
        }
    }

    public LangMessage(AbstractionUtil abstraction, String str) {
        this.abstraction = abstraction;
        this.jsonStrings = new ArrayList<>(1);
        this.rawStrings = new ArrayList<>(1);

        this.captureIndices = new LineCaptureIndices[] {new LineCaptureIndices(new HashMap<>())};
        this.lineInfos = new LineInfo[] {new LineInfo()};

        this.jsonStrings.add(parseToJson(str, 0));
        this.rawStrings.add(new ColoredString(str).applied());
    }

    public List<String> getRawString(String... replacements) {
        if (replacements.length == 0) {
            return getRawString();
        }

        List<String> list = new ArrayList<>(this.rawStrings.size());

        this.rawStrings.forEach(str -> {
            list.add(MUtils.fastReplace(str, replacements));
        });

        return list;
    }

    public List<String> getRawString() {
        return new ArrayList<>(this.rawStrings);
    }

    public void sendTo(CommandSender cs, String... replacements) {
        if (cs == null) return;

        if (!(cs instanceof Player)) {
            for (String str : this.rawStrings) {
                cs.sendMessage(MUtils.fastReplace(str, replacements));
            }
            return;
        }

        Player p = (Player) cs;
        if (!p.isOnline()) return;

        for (int line = 0; line < this.jsonStrings.size(); line++) {
            LineInfo info = lineInfos[line];

            if (info.showpos() == LineInfo.NORMAL) abstraction.sendRaw(p, MUtils.fastReplace(this.jsonStrings.get(line), replacements));
            else if (info.showpos() == LineInfo.TITLE) {
                abstraction.sendTitle(true,
                        MUtils.fastReplace(this.jsonStrings.get(line), replacements),
                        MUtils.fastReplace(this.lineInfos[line].subtitleJson(), replacements),
                        p, info.fadeIn(), info.dur(), info.fadeOut()
                );
            }
            else if (info.showpos() == LineInfo.ACTIONBAR) abstraction.sendActionbar(true, MUtils.fastReplace(this.jsonStrings.get(line), replacements), p);
        }
    }

    public void sendWithAction(CommandSender cs, ActionInfo[] infos, String... replacements) {
        if (cs == null) return;

        if (!(cs instanceof Player)) {
            for (String str : this.rawStrings) {
                cs.sendMessage(MUtils.fastReplace(str, replacements));
            }
            return;
        }

        Player p = (Player) cs;
        if (!p.isOnline()) return;

        for (int line = 0; line < this.jsonStrings.size(); line++) {
            LineInfo info = lineInfos[line];

            if (info.showpos() == LineInfo.NORMAL) abstraction.sendRaw(p, MUtils.fastReplace(withAction(line, infos), replacements));
            else if (info.showpos() == LineInfo.TITLE) abstraction.sendTitle(true, MUtils.fastReplace(this.jsonStrings.get(line), replacements), "", p, info.fadeIn(), info.dur(), info.fadeOut());
            else if (info.showpos() == LineInfo.ACTIONBAR) abstraction.sendActionbar(true, MUtils.fastReplace(this.jsonStrings.get(line), replacements), p);
        }
    }

    public void sendTo(Iterable<? extends OfflinePlayer> cs, String... replacements) {
        if (cs == null) return;

        for (OfflinePlayer p : cs) {
            if (!p.isOnline()) return;

            Player pl = (Player) p;

            for (int line = 0; line < this.jsonStrings.size(); line++) {
                LineInfo info = lineInfos[line];

                if (info.showpos() == LineInfo.NORMAL) abstraction.sendRaw(pl, MUtils.fastReplace(this.jsonStrings.get(line), replacements));
                else if (info.showpos() == LineInfo.TITLE) {
                    abstraction.sendTitle(true,
                            MUtils.fastReplace(this.jsonStrings.get(line), replacements),
                            MUtils.fastReplace(this.lineInfos[line].subtitleJson(), replacements),
                            pl, info.fadeIn(), info.dur(), info.fadeOut()
                    );
                } else if (info.showpos() == LineInfo.ACTIONBAR) abstraction.sendActionbar(true, MUtils.fastReplace(this.jsonStrings.get(line), replacements), pl);
            }
        }
    }

    public void sendWithAction(Iterable<? extends OfflinePlayer> cs, ActionInfo[] infos, String... replacements) {
        if (cs == null || infos == null) return;

        for (int line = 0; line < this.jsonStrings.size(); line++) {
            LineInfo info = lineInfos[line];

            String text = withAction(line, infos);
            for (OfflinePlayer p : cs) {
                if (!p.isOnline()) return;
                Player pl = (Player) p;

                if (info.showpos() == LineInfo.NORMAL) abstraction.sendRaw(pl, MUtils.fastReplace(text, replacements));
                else if (info.showpos() == LineInfo.TITLE) abstraction.sendTitle(true, MUtils.fastReplace(this.jsonStrings.get(line), replacements), "", pl, info.fadeIn(), info.dur(), info.fadeOut());
                else if (info.showpos() == LineInfo.ACTIONBAR) abstraction.sendActionbar(true, MUtils.fastReplace(this.jsonStrings.get(line), replacements), pl);
            }
        }
    }

    private String withAction(int line, ActionInfo[] infos) {
        JsonArray arr;
        arr = Json.parse(this.jsonStrings.get(line)).asArray();

        for (ActionInfo info : infos) {
            LineCapture captures = captureIndices[line].indices().get(info.capture);
            if (captures == null) continue;

            for (int i = captures.startIndex(); i <= captures.endIndex(); i++) {
                JsonObject node = arr.get(i).asObject();

                for (ActionInfoParam param : info.params) {
                    JsonObject actionNode = new JsonObject();
                    actionNode.add("action", param.action);

                    if (param.action.equals("hoverEvent") && param.value.equals("show_text")) {
                        actionNode.add("value", ChatBaseComponentUtil.constructJson(param.value));
                    } else {
                        actionNode.add("value", param.value);
                    }

                    node.add(param.event, actionNode);
                }
            }
        }

        return arr.toString();
    }

    private String parseToJson(String text, int line) {
        Map<Integer, LineCapture> captureIndices = this.captureIndices[line].indices();

        atsign: {
            int lastIndex = text.lastIndexOf('@');

            if (lastIndex == -1) {
                break atsign;
            }

            String info = text.substring(lastIndex+1);
            String[] split = info.split(":");
            if (split[0].equals("title")) {
                this.lineInfos[line].setShowpos(LineInfo.TITLE);

                this.lineInfos[line].setFadeIn(Integer.parseInt(split[1]));
                this.lineInfos[line].setDur(Integer.parseInt(split[2]));
                this.lineInfos[line].setFadeOut(Integer.parseInt(split[3]));

            } else if (split[0].equals("actionbar")) {
                this.lineInfos[line].setShowpos(LineInfo.ACTIONBAR);
            }

            text = text.substring(0, lastIndex);
        }

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

        int startIndex = -1;

        int objIndex = 1;

        String titleJson = "";

        char[] chArray = text.toCharArray();
        for (int i = 0; i < chArray.length; i++) {
            char ch = chArray[i];

            if (ch == '#') {
                if (i == chArray.length-1) {
                    strb.append(ch);
                    break;
                }

                strb.append(chArray[i+1]);
                i += 1;
                continue;
            } else if (ch == '&') {
                if (i == chArray.length-1) {
                    strb.append(ch);
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

                        if (gettingInGroup && startIndex == -1) {
                            startIndex = objIndex;
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
                if (startIndex == -1) {
                    startIndex = objIndex;
                }

                continue;
            } else if (ch == '}' && gettingInGroup) {
                gettingInGroup = false;

                json.appendText(strb.toString());

                json.color(colorName);
                if (bold) json.bold();
                if (obf) json.obfuscated();
                if (italic) json.italic();
                if (strike) json.strikethrough();
                if (underline) json.underlined();

                strb = new StringBuilder();

                captureIndices.put(inGroup, new LineCapture(startIndex, objIndex));
                startIndex = -1;

                objIndex++;

                continue;
            } else if (gettingGroupNumber) {
                numberBuilder.append(ch);
                continue;
            } else if (ch == '|' && this.lineInfos[line].showpos() == LineInfo.TITLE && titleJson.isEmpty()) {
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

                titleJson = json.getAsString();
                json = new RawJsonBuilder();
                continue;
            }

            strb.append(ch);
        }

        if (strb.length() != 0) {
            json.appendText(strb.toString());

            json.color(colorName);
            if (bold) json.bold();
            if (obf) json.obfuscated();
            if (italic) json.italic();
            if (strike) json.strikethrough();
            if (underline) json.underlined();
        }

        if (titleJson.isEmpty()) {
            return json.getAsString();
        } else {
            this.lineInfos[line].setSubtitleJson(json.getAsString());
            return titleJson;
        }
    }

    private boolean isApplicableChar(char ch) {
        return isColorChar(ch) || (ch == 'l') || (ch == 'r') || (ch == 'o') || (ch == 'k') || (ch == 'n') || (ch == 'm');
    }

    private boolean isColorChar(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f');
    }
}