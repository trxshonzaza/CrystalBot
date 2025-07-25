package trxsh.ontop.crystalbot.manager;

import org.bukkit.entity.Player;
import trxsh.ontop.crystalbot.gui.PresetGUI;
import trxsh.ontop.crystalbot.gui.SpawnGUI;

import java.util.ArrayList;
import java.util.List;

public class GuiManager {
    public static final List<SpawnGUI> guis = new ArrayList<>();
    public static final List<PresetGUI> presetGuis = new ArrayList<>();
    public static final List<SpawnGUI> toRemove = new ArrayList<>();

    public static SpawnGUI createGUI(Player user) {
        SpawnGUI gui = new SpawnGUI(user);
        guis.add(gui);

        return gui;
    }
    public static PresetGUI createPresetGUI(SpawnGUI spawnGui) {
        PresetGUI gui = new PresetGUI(spawnGui);
        presetGuis.add(gui);

        return gui;
    }

    public static void removeGUI(SpawnGUI gui) {
        synchronized (guis) {
            guis.remove(gui);
        }
    }

    public static void removePresetGUI(PresetGUI gui) {
        synchronized (presetGuis) {
            presetGuis.remove(gui);
        }
    }

    public static void removeAfterLoop(SpawnGUI gui) {
        toRemove.add(gui);
    }
}
