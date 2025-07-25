package trxsh.ontop.crystalbot.util;

import net.citizensnpcs.trait.LookClose;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import trxsh.ontop.crystalbot.Main;
import trxsh.ontop.crystalbot.bot.Bot;
import trxsh.ontop.crystalbot.bot.BotSettings;

public class CrystalUtility {

    public static void handleCrystalBreak(Bot bot, EnderCrystal crystal) {
        bot.getNPCAsEntity().getWorld().createExplosion(crystal.getLocation(), 6.0F, false);
        crystal.remove();
    }

    public static void handleAnchorBreak(Bot bot, Location location) {
        location.getBlock().setType(Material.AIR);
        bot.getNPCAsEntity().getWorld().createExplosion(location, 5.0F, true, true);
    }

    public static void placeObsidianAndCrystal(Bot bot, Location obsidian) {
        try {
            Vector look = LookUtility.getDirectionBetween(bot.getNPCAsEntity().getEyeLocation(), obsidian);
            Location l = bot.getNPCAsEntity().getLocation();

            PlayerInventory botInventory = bot.getNPCAsEntity().getInventory();

            ItemStack previous = botInventory.getItemInMainHand();

            Location blockLocation = null;

            boolean found = false;

            for(ItemStack stack : botInventory.getContents()) {

                if(stack != null) {

                    if(stack.getType() == Material.OBSIDIAN) {

                        botInventory.setItemInMainHand(stack);

                        if(obsidian.getBlock().getType() != Material.BEDROCK || obsidian.getBlock().getType() != Material.OBSIDIAN) {
                            obsidian.getBlock().setType(Material.OBSIDIAN);
                            bot.getNPCAsEntity().getWorld().playSound(obsidian, Sound.BLOCK_STONE_PLACE, 1f, 1f);
                        }

                        blockLocation = obsidian;

                        bot.lookAt(look);

                        found = true;

                        stack.setAmount(stack.getAmount() - 1);

                        break;

                    }

                }

            }

            if(!found) {
                bot.isCrystaling = false;
                return;
            }

            final EnderCrystal[] crystal = new EnderCrystal[1];

            Location finalBlockLocation = new Location(blockLocation.getWorld(),
                    blockLocation.getX() + .5,
                    blockLocation.getY(),
                    blockLocation.getZ() + .5);

            Bukkit.getScheduler().runTaskLater(Main.Instance, new Runnable() {

                @Override
                public void run() {

                    for(ItemStack stack : botInventory.getContents()) {
                        if(stack != null) {
                            if(stack.getType() == Material.END_CRYSTAL) {
                                botInventory.setItemInMainHand(stack);

                                stack.setAmount(stack.getAmount() - 1);

                                bot.lookAt(look);

                                crystal[0] = (EnderCrystal) bot.getNPCAsEntity().getWorld().spawnEntity(finalBlockLocation.clone().add(0, 1, 0), EntityType.ENDER_CRYSTAL);
                                crystal[0].setShowingBottom(false);

                                break;
                            }
                        }
                    }

                    Bukkit.getScheduler().runTaskLater(Main.Instance, new Runnable() {
                        @Override
                        public void run() {
                            bot.lookAt(look);

                            bot.getNPCAsEntity().swingMainHand();

                            handleCrystalBreak(bot, crystal[0]);

                            bot.isCrystaling = false;
                        }
                    }, bot.CRYSTAL_BREAK_TICK_DELAY);
                }
            }, bot.CRYSTAL_PLACE_TICK_DELAY);

            botInventory.setItemInMainHand(previous);

            bot.getNPCAsEntity().swingMainHand();
        }catch(Exception e) {
            Bukkit.broadcastMessage("CrystalBot with Citizens NPC ID " + bot.getNPC().getId() + " could not place a crystal. Check console for stacktrace. " + e.getMessage());
            e.printStackTrace();
            bot.isCrystaling = false;
            bot.isEating = false;
        }
    }

    public static void placeAnchorAndExplode(Bot bot, Location anchor) {
        try {
            Vector look = LookUtility.getDirectionBetween(bot.getNPCAsEntity().getEyeLocation(), anchor);
            Location l = bot.getNPCAsEntity().getLocation();

            PlayerInventory botInventory = bot.getNPCAsEntity().getInventory();

            ItemStack previous = botInventory.getItemInMainHand();

            boolean found = false;

            for(ItemStack stack : botInventory.getContents()) {
                if(stack != null) {
                    if(stack.getType() == Material.RESPAWN_ANCHOR) {

                        botInventory.setItemInMainHand(stack);

                        if(!anchor.getBlock().getType().isSolid() || anchor.getBlock().getType().isAir())
                            anchor.getBlock().setType(Material.RESPAWN_ANCHOR);

                        found = true;

                        bot.lookAt(look);

                        stack.setAmount(stack.getAmount() - 1);
                        bot.getNPCAsEntity().getWorld().playSound(anchor, Sound.BLOCK_STONE_PLACE, 1f, 1f);

                        break;
                    }
                }
            }

            if(!found) {
                bot.isAnchoring = false;
                return;
            }

            for(ItemStack stack : botInventory.getContents()) {
                if(stack != null) {
                    if(stack.getType() == Material.GLOWSTONE) {
                        anchor.getBlock().setType(Material.RESPAWN_ANCHOR);

                        final RespawnAnchor anchorBlock = (RespawnAnchor) anchor.getBlock().getBlockData();

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                botInventory.setItemInMainHand(stack);
                                stack.setAmount(stack.getAmount() - 1);

                                bot.lookAt(look);

                                bot.getNPCAsEntity().swingMainHand();

                                anchorBlock.setCharges(1);

                                anchor.getBlock().setBlockData(anchorBlock);
                                anchor.getWorld().playSound(anchor, Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1f, 1f);

                                Bukkit.getScheduler().runTaskLater(Main.Instance, new Runnable() {
                                    @Override
                                    public void run() {
                                        bot.lookAt(look);

                                        bot.getNPCAsEntity().swingMainHand();

                                        handleAnchorBreak(bot, anchor);

                                        bot.isAnchoring = false;
                                    }
                                }, bot.ANCHOR_BREAK_TICK_DELAY);

                                botInventory.setItemInMainHand(previous);
                            }
                        }.runTaskLater(Main.Instance, bot.ANCHOR_PLACE_TICK_DELAY);

                        break;
                    }
                }
            }
        }catch(Exception e) {
            Bukkit.broadcastMessage("CrystalBot with Citizens NPC ID " + bot.getNPC().getId() + " could not place a anchor. Check console for stacktrace. " + e.getMessage());
            e.printStackTrace();
            bot.isEating = false;
            bot.isAnchoring = false;
        }
    }
}
