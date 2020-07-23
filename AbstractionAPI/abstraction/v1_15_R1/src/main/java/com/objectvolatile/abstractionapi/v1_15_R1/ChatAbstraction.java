package com.objectvolatile.abstractionapi.v1_15_R1;

import com.objectvolatile.abstractionapi.nmsinterface.ChatBaseComponentUtil;
import com.objectvolatile.abstractionapi.nmsinterface.IChatAbstraction;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import net.minecraft.server.v1_15_R1.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ChatAbstraction implements IChatAbstraction {

    @Override
    public boolean sendTitle(boolean json, String titleMsg, String subtitleMsg, UUID playerId, int fadeIn, int dur, int fadeOut) {
        final Player player = Bukkit.getPlayer(playerId);
        final CraftPlayer cp = (CraftPlayer) player;
        final EntityPlayer ep = cp.getHandle();

        IChatBaseComponent titleMessage = IChatBaseComponent.ChatSerializer.a(json ? titleMsg : ChatBaseComponentUtil.constructJson(titleMsg));
        IChatBaseComponent subtitleMessage = IChatBaseComponent.ChatSerializer.a(json ? subtitleMsg : ChatBaseComponentUtil.constructJson(subtitleMsg));

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMessage);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleMessage);
        PacketPlayOutTitle timesPacket = new PacketPlayOutTitle(fadeIn, dur, fadeOut);

        ep.playerConnection.sendPacket(titlePacket);
        ep.playerConnection.sendPacket(subtitlePacket);
        ep.playerConnection.sendPacket(timesPacket);

        return true;
    }

    @Override
    public boolean sendActionbar(boolean json, String message, UUID playerId) {
        final Player player = Bukkit.getPlayer(playerId);
        final CraftPlayer cp = (CraftPlayer) player;
        final EntityPlayer ep = cp.getHandle();

        IChatBaseComponent actionbarMessage = IChatBaseComponent.ChatSerializer.a(json ? message : ChatBaseComponentUtil.constructJson(message));

        PacketPlayOutTitle actionbarPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, actionbarMessage);

        ep.playerConnection.sendPacket(actionbarPacket);

        return true;
    }

    @Override
    public boolean resetTitle(UUID playerId) {
        final Player player = Bukkit.getPlayer(playerId);
        final CraftPlayer cp = (CraftPlayer) player;
        final EntityPlayer ep = cp.getHandle();

        IChatBaseComponent blank = IChatBaseComponent.ChatSerializer.a("");

        PacketPlayOutTitle resetPacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, blank);

        ep.playerConnection.sendPacket(resetPacket);

        return true;
    }

    @Override
    public boolean sendRaw(UUID playerId, String json) {
        final Player player = Bukkit.getPlayer(playerId);
        final CraftPlayer cp = (CraftPlayer) player;
        final EntityPlayer ep = cp.getHandle();

        ep.playerConnection.sendPacket(new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(json)));

        return true;
    }

}