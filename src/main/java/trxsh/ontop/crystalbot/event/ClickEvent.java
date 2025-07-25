package trxsh.ontop.crystalbot.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChatEvent;
import trxsh.ontop.crystalbot.gui.PresetGUI;
import trxsh.ontop.crystalbot.gui.SpawnGUI;
import trxsh.ontop.crystalbot.manager.GuiManager;

import java.util.HashMap;
import java.util.UUID;

public class ClickEvent implements Listener {
    public static HashMap<UUID, SpawnGUI> awaitingMessages = new HashMap<>();
    public static HashMap<UUID, SpawnGUI> awaitingLocations = new HashMap<>();
    public static HashMap<UUID, SpawnGUI> awaitingString = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if(event.getView().getTitle().equalsIgnoreCase("Spawn GUI")) {
            synchronized (GuiManager.guis) {
                for(SpawnGUI gui : GuiManager.guis) {
                    if(event.getInventory().equals(gui.getInventory())) {
                        event.setCancelled(true);
                        gui.onClick(event);
                    }
                }
            }
        }

        if(event.getView().getTitle().equalsIgnoreCase("Presets")) {
            synchronized (GuiManager.presetGuis) {
                for(PresetGUI gui : GuiManager.presetGuis) {
                    if(event.getInventory().equals(gui.getInventory())) {
                        event.setCancelled(true);
                        gui.onClick(event);
                    }
                }
            }
        }

        GuiManager.guis.removeAll(GuiManager.toRemove);
    }

    //TODO: this event is deprecated, find a workaround.
    @EventHandler
    public void onChat(PlayerChatEvent event) {
        if(awaitingMessages.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);

            SpawnGUI gui = awaitingMessages.get(event.getPlayer().getUniqueId());
            int number = Integer.parseInt(event.getMessage());

            gui.returnInt(number);

            awaitingMessages.remove(event.getPlayer().getUniqueId());
        }

        if(awaitingString.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);

            SpawnGUI gui = awaitingString.get(event.getPlayer().getUniqueId());
            gui.returnString(event.getMessage());

            awaitingString.remove(event.getPlayer().getUniqueId());
        }

        if(awaitingLocations.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);

            SpawnGUI gui = awaitingLocations.get(event.getPlayer().getUniqueId());

            int x = event.getPlayer().getLocation().getBlockX();
            int y = event.getPlayer().getLocation().getBlockY();
            int z = event.getPlayer().getLocation().getBlockZ();

            gui.setLocation(x, y, z);

            awaitingLocations.remove(event.getPlayer().getUniqueId());
        }
    }
}
