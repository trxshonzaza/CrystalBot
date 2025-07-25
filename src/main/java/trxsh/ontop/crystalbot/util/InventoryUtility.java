package trxsh.ontop.crystalbot.util;

import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import trxsh.ontop.crystalbot.bot.Bot;
import trxsh.ontop.crystalbot.option.CrystalOptions;

public class InventoryUtility {

    /*
    REFER TO THIS IMAGE FOR INVENTORY SLOT IDS:
    https://wiki.vg/images/1/13/Inventory-slots.png
     */

    public static void loadNetherite(Bot bot) {

        ItemStack helmet = new ItemStack(Material.NETHERITE_HELMET);
        ItemStack chest = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.NETHERITE_LEGGINGS);
        ItemStack boot = new ItemStack(Material.NETHERITE_BOOTS);

        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        if(bot.getOptions().contains(CrystalOptions.USE_BLAST_RESISTANCE)) {

            leg.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
            boot.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);

        } else {

            leg.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            boot.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        }

        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        leg.addEnchantment(Enchantment.DURABILITY, 3);
        boot.addEnchantment(Enchantment.DURABILITY, 3);

        helmet.addEnchantment(Enchantment.MENDING, 1);
        chest.addEnchantment(Enchantment.MENDING, 1);
        leg.addEnchantment(Enchantment.MENDING, 1);
        boot.addEnchantment(Enchantment.MENDING, 1);

        Equipment inventory = bot.getNPC().getOrAddTrait(Equipment.class);

        inventory.set(Equipment.EquipmentSlot.HELMET, helmet);
        inventory.set(Equipment.EquipmentSlot.CHESTPLATE, chest);
        inventory.set(Equipment.EquipmentSlot.LEGGINGS, leg);
        inventory.set(Equipment.EquipmentSlot.BOOTS, boot);

    }

    public static void loadDiamond(Bot bot) {

        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemStack chest = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack leg = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack boot = new ItemStack(Material.DIAMOND_BOOTS);

        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
        chest.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        if(bot.getOptions().contains(CrystalOptions.USE_BLAST_RESISTANCE)) {

            leg.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
            boot.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);

        } else {

            leg.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            boot.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

        }

        helmet.addEnchantment(Enchantment.DURABILITY, 3);
        chest.addEnchantment(Enchantment.DURABILITY, 3);
        leg.addEnchantment(Enchantment.DURABILITY, 3);
        boot.addEnchantment(Enchantment.DURABILITY, 3);

        helmet.addEnchantment(Enchantment.MENDING, 1);
        chest.addEnchantment(Enchantment.MENDING, 1);
        leg.addEnchantment(Enchantment.MENDING, 1);
        boot.addEnchantment(Enchantment.MENDING, 1);

        Equipment inventory = bot.getNPC().getOrAddTrait(Equipment.class);

        inventory.set(Equipment.EquipmentSlot.HELMET, helmet);
        inventory.set(Equipment.EquipmentSlot.CHESTPLATE, chest);
        inventory.set(Equipment.EquipmentSlot.LEGGINGS, leg);
        inventory.set(Equipment.EquipmentSlot.BOOTS, boot);

    }

    public static void loadPickaxe(Bot bot) {
        ItemStack stack = new ItemStack(Material.NETHERITE_PICKAXE);

        stack.addEnchantment(Enchantment.DIG_SPEED, 5);
        stack.addEnchantment(Enchantment.DURABILITY, 3);
        stack.addEnchantment(Enchantment.MENDING, 1);

        bot.getNPCAsEntity().getInventory().addItem(stack);

    }

    public static void loadSword(Bot bot) {

        ItemStack stack = new ItemStack(Material.NETHERITE_SWORD);

        stack.addEnchantment(Enchantment.DAMAGE_ALL, 5);
        stack.addEnchantment(Enchantment.DURABILITY, 3);
        stack.addEnchantment(Enchantment.MENDING, 1);
        stack.addEnchantment(Enchantment.SWEEPING_EDGE, 3);

        if(bot.getOptions().contains(CrystalOptions.CAN_USE_KB))
            stack.addEnchantment(Enchantment.KNOCKBACK, 1);

        Equipment inventory = bot.getNPC().getOrAddTrait(Equipment.class);

        inventory.set(Equipment.EquipmentSlot.HAND, stack);

    }

    public static void loadGapples(Bot bot, int stackAmounts) {

        ItemStack stack = new ItemStack(Material.GOLDEN_APPLE);
        stack.setAmount(64);

        for(int i = 0; i < stackAmounts; i++)
            bot.getNPCAsEntity().getInventory().addItem(stack);

    }


    public static void loadTotems(Bot bot, int amount) {

        ItemStack stack = new ItemStack(Material.TOTEM_OF_UNDYING);

        for(int i = 0; i < amount; i++)
            bot.getNPCAsEntity().getInventory().addItem(stack);

    }

    public static void loadPearls(Bot bot, int stackAmounts) {

        ItemStack stack = new ItemStack(Material.ENDER_PEARL);
        stack.setAmount(16);

        for(int i = 0; i < stackAmounts; i++)
           bot.getNPCAsEntity().getInventory().addItem(stack);

    }

    public static void loadCrystalsAndObsidian(Bot bot, int crystalStackAmounts, int obsidianStackAmounts) {

        ItemStack crystals = new ItemStack(Material.END_CRYSTAL);
        ItemStack obsidian = new ItemStack(Material.OBSIDIAN);

        crystals.setAmount(64);
        obsidian.setAmount(64);

        for(int i = 0; i < crystalStackAmounts; i++)
            bot.getNPCAsEntity().getInventory().addItem(crystals);

        for(int i = 0; i < obsidianStackAmounts; i++)
            bot.getNPCAsEntity().getInventory().addItem(obsidian);

    }

    public static void loadAnchorsAndGlowstone(Bot bot, int anchorStackAmounts, int glowstoneStackAmounts) {

        ItemStack anchor = new ItemStack(Material.RESPAWN_ANCHOR);
        ItemStack glowstone = new ItemStack(Material.GLOWSTONE);

        anchor.setAmount(64);
        glowstone.setAmount(64);

        for(int i = 0; i < anchorStackAmounts; i++)
            bot.getNPCAsEntity().getInventory().addItem(anchor);

        for(int i = 0; i < glowstoneStackAmounts; i++)
            bot.getNPCAsEntity().getInventory().addItem(glowstone);

    }

}
