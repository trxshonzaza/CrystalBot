package trxsh.ontop.crystalbot.event;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

public class DamageEvent implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if(!player.getMetadata("CrystalBot").isEmpty()) { // will be true if the metadata does exist
                int finalDamage = (int) Math.round(event.getDamage());

                // loose 1 durability for every 2 hearts of damage (see https://minecraft.fandom.com/wiki/Durability#Armor_durability)
                if(finalDamage >= 4) {
                    for(int i = 1; i < Math.round((float)finalDamage / 4); i++) {
                        if(new Random().nextInt(0, 4) <= 1) { // loose 1 durability for every 1 in (1 + enchant level)
                            reduceDurability(player);
                        }
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

            if(meta.getDamage() >= player.getInventory().getHelmet().getType().getMaxDurability()) {
                player.getInventory().setHelmet(null);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            }
        }

        if(player.getInventory().getChestplate() != null) {
            Damageable meta = (Damageable) player.getInventory().getChestplate().getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            player.getInventory().getChestplate().setItemMeta(meta);

            if(meta.getDamage() >= player.getInventory().getChestplate().getType().getMaxDurability()) {
                player.getInventory().setChestplate(null);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            }
        }

        if(player.getInventory().getLeggings() != null) {
            Damageable meta = (Damageable) player.getInventory().getLeggings().getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            player.getInventory().getLeggings().setItemMeta(meta);

            if(meta.getDamage() >= player.getInventory().getLeggings().getType().getMaxDurability()) {
                player.getInventory().setLeggings(null);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            }
        }

        if(player.getInventory().getBoots() != null) {
            Damageable meta = (Damageable) player.getInventory().getBoots().getItemMeta();
            meta.setDamage(meta.getDamage() + 1);
            player.getInventory().getBoots().setItemMeta(meta);

            if(meta.getDamage() >= player.getInventory().getBoots().getType().getMaxDurability()) {
                player.getInventory().setBoots(null);
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1f, 1f);
            }
        }
    }
}
