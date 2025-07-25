package trxsh.ontop.crystalbot.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageEvent implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if(!player.getMetadata("CrystalBot").isEmpty()) { // will be true if the metadata does exist
                int finalDamage = (int) Math.round(event.getDamage());

                if(finalDamage >= 4) {
                    for(int i = 0; i < Math.round((float)finalDamage / 4); i++) {
                        reduceDurability(player);
                    }
                }
            }
        }
    }

    public void reduceDurability(Player player) {
        if(player.getInventory().getHelmet() != null) {
            Damageable meta = (Damageable) player.getInventory().getHelmet().getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            player.getInventory().getHelmet().setItemMeta(meta);
        }

        if(player.getInventory().getChestplate() != null) {
            Damageable meta = (Damageable) player.getInventory().getChestplate().getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            player.getInventory().getChestplate().setItemMeta(meta);
        }

        if(player.getInventory().getLeggings() != null) {
            Damageable meta = (Damageable) player.getInventory().getLeggings().getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            player.getInventory().getLeggings().setItemMeta(meta);
        }

        if(player.getInventory().getBoots() != null) {
            Damageable meta = (Damageable) player.getInventory().getBoots().getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            player.getInventory().getBoots().setItemMeta(meta);
        }
    }
}
