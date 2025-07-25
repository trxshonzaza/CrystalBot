package trxsh.ontop.crystalbot.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import trxsh.ontop.crystalbot.gui.preset.CrystalPreset;
import trxsh.ontop.crystalbot.kit.CrystalKit;

import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.UUID;

public class PresetGUI {
    public Inventory inventory;
    public SpawnGUI gui;

    public PresetGUI(SpawnGUI gui) {
        this.inventory = Bukkit.createInventory(null, 9, "Presets");
        this.gui = gui;
        update();
    }

    public void update() {
        inventory.setItem(2, create(Material.GRASS_BLOCK, CrystalPreset.FLAT.toString()));

        inventory.setItem(4, create(Material.TOTEM_OF_UNDYING, CrystalPreset.DRAIN.toString()));

        inventory.setItem(6, create(Material.STONE, CrystalPreset.NORMAL.toString()));
    }

    public void onClick(InventoryClickEvent event) {
        int index = event.getSlot();

        if(event.getWhoClicked() instanceof Player)
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 2f);

        switch(index) {
            case 2:
                gui.selectPreset(CrystalPreset.FLAT);
                gui.update();
                gui.player.openInventory(gui.getInventory());
                break;
            case 4:
                gui.selectPreset(CrystalPreset.DRAIN);
                gui.update();
                gui.player.openInventory(gui.getInventory());
                break;
            case 6:
                gui.selectPreset(CrystalPreset.NORMAL);
                gui.update();
                gui.player.openInventory(gui.getInventory());
                break;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public ItemStack create(Material mat, String name) {
        ItemStack stack = new ItemStack(mat);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);

        return stack;
    }
}
