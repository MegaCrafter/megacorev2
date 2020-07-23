package com.objectvolatile.megacorev2;

import com.objectvolatile.abstractionapi.AbstractionAPI;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class AbstractionUtil {

    private AbstractionAPI abstraction;

    public AbstractionUtil() {
        this.abstraction = new AbstractionAPI();
    }

    public void sendTitle(boolean alreadyJson, String titleText, String subtitleText, Player p, int fadeIn, int dur, int fadeOut) {
        boolean sent = abstraction.titleManager().sendTitle(alreadyJson, titleText, subtitleText, p.getUniqueId(), fadeIn, dur, fadeOut);

        if (!sent) {
            MegaCore.logger.log(Level.WARNING, "Titles are not supported in " + abstraction.serverVersion());
        }
    }

    public void sendActionbar(boolean alreadyJson, String text, Player p) {
        boolean sent = abstraction.titleManager().sendActionbar(alreadyJson, text, p.getUniqueId());

        if (!sent) {
            MegaCore.logger.log(Level.WARNING, "Actionbars is not supported in " + abstraction.serverVersion());
        }
    }

    public void resetTitle(Player p) {
        boolean sent = abstraction.titleManager().resetTitle(p.getUniqueId());

        if (!sent) {
            MegaCore.logger.log(Level.WARNING, "Title resetting is not supported in " + abstraction.serverVersion());
        }
    }

    public void sendRaw(Player p, String json) {
        boolean sent = abstraction.titleManager().sendRaw(p.getUniqueId(), json);

        if (!sent) {
            MegaCore.logger.log(Level.WARNING, "Raw json sending is not supported in " + abstraction.serverVersion());
        }
    }
}