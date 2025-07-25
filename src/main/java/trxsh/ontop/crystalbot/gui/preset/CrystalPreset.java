package trxsh.ontop.crystalbot.gui.preset;

import org.bukkit.ChatColor;

public enum CrystalPreset {
    NORMAL(ChatColor.AQUA + "Normal"),
    FLAT(ChatColor.GREEN + "Flat"),
    DRAIN(ChatColor.GOLD + "Drain");

    String s;

    CrystalPreset(String s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return s;
    }
}
