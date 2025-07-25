package trxsh.ontop.crystalbot.gui;

import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.profile.PlayerProfile;
import trxsh.ontop.crystalbot.Main;
import trxsh.ontop.crystalbot.bot.Bot;
import trxsh.ontop.crystalbot.event.ClickEvent;
import trxsh.ontop.crystalbot.gui.preset.CrystalPreset;
import trxsh.ontop.crystalbot.kit.CrystalKit;
import trxsh.ontop.crystalbot.manager.BotManager;
import trxsh.ontop.crystalbot.manager.GuiManager;
import trxsh.ontop.crystalbot.option.BotDifficulty;
import trxsh.ontop.crystalbot.option.CrystalOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SpawnGUI {
    public Inventory inventory;
    /*
    digging, blast res and knockback are the only options that can be added here.
     */
    public List<CrystalOptions> options;
    public Location spawnLocation;
    public Location previousLocation;
    public Player player;
    public CrystalKit kit;
    public int selectedIndex = 0;
    public int selectedArray = 0;
    public int gappleStacks = 2;
    public int totemAmount = 18;
    public boolean gappleSelected = false, totemSelected = false;
    public boolean profileChanged = false;
    public UUID guiId = null;
    public CrystalPreset selectedPreset = CrystalPreset.NORMAL;
    public BotDifficulty selectedDifficulty = BotDifficulty.NORMAL;
    public String skin = null;

    /*
    index 0 is crystal place tick delay
    index 1 is crystal break tick delay
    index 2 is anchor place tick delay
    index 3 is anchor break tick delay
    index 4 is main loop delay
    index 5 is check totem delay
    */
    public int[] delays = new int[] {
            2, 2, 2, 2, 1, 1
    };

    /*
    index 0 is dig verbose max
    index 1 is anchor verbose max
     */
    public int[] verboses = new int[] {
            7, 10
    };

    public SpawnGUI(Player opener) {
        this.guiId = UUID.randomUUID();
        this.options = new ArrayList<>();
        this.spawnLocation = opener.getLocation();
        this.player = opener;
        this.inventory = Bukkit.createInventory(null, 45, "Spawn GUI");
        this.kit = CrystalKit.NORMAL;
        update();
    }

    public void update() {
        if(!options.contains(CrystalOptions.CAN_DIG)) {
            inventory.setItem(3, create(Material.NETHERITE_PICKAXE, ChatColor.WHITE + "Can Dig: " + ChatColor.RED + "False"));
        } else {
            inventory.setItem(3, create(Material.NETHERITE_PICKAXE, ChatColor.WHITE + "Can Dig: " + ChatColor.GREEN + "True"));
        }

        if(!options.contains(CrystalOptions.USE_BLAST_RESISTANCE)) {
            inventory.setItem(4, create(Material.MAGMA_CREAM, ChatColor.WHITE + "Blast Resistance: " + ChatColor.RED + "False"));
        } else {
            inventory.setItem(4, create(Material.MAGMA_CREAM, ChatColor.WHITE + "Blast Resistance: " + ChatColor.GREEN + "True"));
        }

        if(!options.contains(CrystalOptions.CAN_USE_KB)) {
            inventory.setItem(5, create(Material.NETHERITE_SWORD, ChatColor.WHITE + "Knock back: " + ChatColor.RED + "False"));
        } else {
            inventory.setItem(5, create(Material.NETHERITE_SWORD, ChatColor.WHITE + "Knock back: " + ChatColor.GREEN + "True"));
        }

        inventory.setItem(9 + 2, create(Material.OBSIDIAN, ChatColor.WHITE + "Crystal Place Tick Rate: " + ChatColor.AQUA + delays[0] + " ticks"));
        inventory.setItem(9 + 3, create(Material.END_CRYSTAL, ChatColor.WHITE + "Crystal Break Tick Rate: " + ChatColor.AQUA + delays[1] + " ticks"));
        inventory.setItem(9 + 4, create(Material.RESPAWN_ANCHOR, ChatColor.WHITE + "Anchor Place Tick Rate: " + ChatColor.AQUA + delays[2] + " ticks"));
        inventory.setItem(9 + 5, create(Material.GLOWSTONE, ChatColor.WHITE + "Anchor Break Tick Rate: " + ChatColor.AQUA + delays[3] + " ticks"));
        inventory.setItem(9 + 6, create(Material.REDSTONE, ChatColor.WHITE + "Main Loop Rate: " + ChatColor.AQUA + delays[4] + " ticks"));

        // verboses
        inventory.setItem(18 + 3, create(Material.DIAMOND_SHOVEL, ChatColor.WHITE + "Dig Verbose: " + ChatColor.AQUA + verboses[0] + " ticks"));
        inventory.setItem(18 + 5, create(Material.GLOWSTONE_DUST, ChatColor.WHITE + "Anchor Verbose: " + ChatColor.AQUA + verboses[1] + " ticks"));

        // difficulties
        inventory.setItem(0, create(Material.LIME_DYE, ChatColor.GREEN + "Easy", List.of("" + ChatColor.WHITE + "Slow and no advanced tactics.")));
        inventory.setItem(9, create(Material.ORANGE_DYE, ChatColor.GOLD + "Medium", List.of("" + ChatColor.WHITE + "Moderate speed, balanced. Your average joe.")));
        inventory.setItem(18, create(Material.RED_DYE, ChatColor.RED + "Hard", List.of("" + ChatColor.WHITE + "Faster and harder to defeat. Not your average joe.")));
        inventory.setItem(27, create(Material.BLACK_DYE, ChatColor.DARK_RED + "Impossible", List.of("" + ChatColor.WHITE + "fastest and perfect timing. Nearly unbeatable." + ChatColor.RED + " Good luck >:)")));

        // set location
        inventory.setItem(8, create(Material.COMPASS, ChatColor.WHITE + "Set Spawn Location (X: " + spawnLocation.getBlockX() + ", Y: " + spawnLocation.getBlockY() + ", Z: " + spawnLocation.getBlockZ() + ")"));

        // set kit
        if(kit == CrystalKit.DIAMOND_PVP) {
            inventory.setItem(17, create(Material.DIAMOND_CHESTPLATE, ChatColor.WHITE + "Diamond Kit"));
        } else if(kit == CrystalKit.NORMAL) {
            inventory.setItem(17, create(Material.NETHERITE_CHESTPLATE, ChatColor.WHITE + "Netherite Kit"));
        }

        // spawn bot
        inventory.setItem(40, create(Material.EMERALD_BLOCK, ChatColor.GREEN + "Spawn Bot"));

        // set gapple stacks
        inventory.setItem(26, create(Material.GOLDEN_APPLE, ChatColor.WHITE + "Golden Apple Amount: " + ChatColor.AQUA + gappleStacks + " stacks"));

        // set totem stacks
        inventory.setItem(35, create(Material.TOTEM_OF_UNDYING, ChatColor.WHITE + "Totem Amount: " + ChatColor.AQUA + totemAmount));

        // set totem check
        inventory.setItem(18 + 4, create(Material.CLOCK, ChatColor.WHITE + "Totem Check Tick Rate: " + ChatColor.AQUA + delays[5] + " ticks"));

        // presets
        inventory.setItem(42, create(Material.DIAMOND_BLOCK, ChatColor.WHITE + "Presets " + ChatColor.AQUA + "(Selected: " + selectedPreset.toString() + ChatColor.AQUA + ")"));

        // change bot skin
        inventory.setItem(38, create(Material.PLAYER_HEAD, ChatColor.WHITE + "Change Bot Skin " + ChatColor.AQUA + "(Current: " + (skin == null ? "Default Skin" : skin) + ")"));

        player.updateInventory();
    }

    public void onClick(InventoryClickEvent event) {
        int index = event.getSlot();

        if(event.getWhoClicked() instanceof Player)
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.BLOCK_LEVER_CLICK, 1f, 2f);

        switch(index) {
            case 0:
                setDifficulty(BotDifficulty.EASY);
                break;
            case 9:
                setDifficulty(BotDifficulty.NORMAL);
                break;
            case 18:
                setDifficulty(BotDifficulty.HARD);
                break;
            case 27:
                setDifficulty(BotDifficulty.GOD);
                break;
            case 40:
                spawnBot();
                break;
            case 26:
                player.sendMessage("Please input an integer (a limit of 3 is highly recommended)");
                player.closeInventory();

                gappleSelected = true;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 35:
                player.sendMessage("Please input an integer (a limit of 20 is highly recommended)");
                player.closeInventory();

                totemSelected = true;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 8:
                player.sendMessage("Please stand where you want the bot to spawn, then send a message.");
                previousLocation = player.getLocation();
                player.closeInventory();

                ClickEvent.awaitingLocations.put(player.getUniqueId(), this);
                break;
            case 17:
                if(kit == CrystalKit.DIAMOND_PVP) {
                    kit = CrystalKit.NORMAL;
                } else if(kit == CrystalKit.NORMAL) {
                    kit = CrystalKit.DIAMOND_PVP;
                }
                break;
            case 3:
                if(options.contains(CrystalOptions.CAN_DIG)) {
                    options.remove(CrystalOptions.CAN_DIG);
                } else {
                    options.add(CrystalOptions.CAN_DIG);
                }
                break;
            case 4:
                if(options.contains(CrystalOptions.USE_BLAST_RESISTANCE)) {
                    options.remove(CrystalOptions.USE_BLAST_RESISTANCE);
                } else {
                    options.add(CrystalOptions.USE_BLAST_RESISTANCE);
                }
                break;
            case 5:
                if(options.contains(CrystalOptions.CAN_USE_KB)) {
                    options.remove(CrystalOptions.CAN_USE_KB);
                } else {
                    options.add(CrystalOptions.CAN_USE_KB);
                }
                break;
            case 9 + 2:
                player.sendMessage("Please input an integer (in ticks, a limit of 20 is highly recommended)");
                player.closeInventory();

                selectedArray = 0;
                selectedIndex = 0;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 9 + 3:
                player.sendMessage("Please input an integer (in ticks, a limit of 20 is highly recommended)");
                player.closeInventory();

                selectedArray = 0;
                selectedIndex = 1;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 9 + 4:
                player.sendMessage("Please input an integer (in ticks, a limit of 20 is highly recommended)");
                player.closeInventory();

                selectedArray = 0;
                selectedIndex = 2;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 9 + 5:
                player.sendMessage("Please input an integer (in ticks, a limit of 20 is highly recommended)");
                player.closeInventory();

                selectedArray = 0;
                selectedIndex = 3;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 9 + 6:
                player.sendMessage("Please input an integer (in ticks, a limit of 20 is highly recommended)");
                player.closeInventory();

                selectedArray = 0;
                selectedIndex = 4;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 18 + 3:
                player.sendMessage("Please input an integer (in ticks, a limit of 20 is highly recommended)");
                player.closeInventory();

                selectedArray = 1;
                selectedIndex = 0;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 18 + 4:
                player.sendMessage("Please input an integer (in ticks, a limit of 5 is highly recommended)");
                player.closeInventory();

                selectedArray = 0;
                selectedIndex = 5;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 18 + 5:
                player.sendMessage("Please input an integer (in ticks, a limit of 20 is highly recommended)");
                player.closeInventory();

                selectedArray = 1;
                selectedIndex = 1;

                ClickEvent.awaitingMessages.put(player.getUniqueId(), this);
                break;
            case 42:
                PresetGUI presetGUI = GuiManager.createPresetGUI(this);
                player.closeInventory();
                player.openInventory(presetGUI.getInventory());
                break;
            case 38:
                player.sendMessage("Please input a player name to change the bot's skin to");
                player.closeInventory();

                ClickEvent.awaitingString.put(player.getUniqueId(), this);
                break;
        }

        update();
    }

    public void returnInt(int number) {
        player.openInventory(inventory);

        if(!gappleSelected && !totemSelected) {
            if(selectedArray == 0) {
                delays[selectedIndex] = number;
            } else if(selectedArray == 1) {
                verboses[selectedIndex] = number;
            }
        } else if(gappleSelected) {
            gappleStacks = number;
        } else {
            totemAmount = number;
        }

        selectedIndex = 0;
        selectedArray = 0;

        gappleSelected = false;
        totemSelected = false;

        update();
    }

    public void returnString(String str) {
        player.openInventory(inventory);

        skin = str;

        update();
    }

    public void setLocation(int x, int y, int z) {
        player.teleport(previousLocation);
        player.openInventory(inventory);
        spawnLocation = new Location(player.getWorld(), x, y, z);
        update();
    }

    public void setDifficulty(BotDifficulty difficulty) {
        options.clear();
        selectedDifficulty = difficulty;

        switch(difficulty) {
            case EASY:
                delays[0] = 6;
                delays[1] = 5;
                delays[2] = 6;
                delays[3] = 5;
                delays[4] = 3;
                delays[5] = 12;
                verboses[0] = 30;
                verboses[1] = 30;
                break;
            case NORMAL:
                delays[0] = 3;
                delays[1] = 3;
                delays[2] = 4;
                delays[3] = 3;
                delays[4] = 2;
                delays[5] = 8;
                verboses[0] = 15;
                verboses[1] = 17;
                options.add(CrystalOptions.CAN_DIG);
                options.add(CrystalOptions.CAN_USE_KB);
                break;
            case HARD:
                delays[0] = 1;
                delays[1] = 2;
                delays[2] = 3;
                delays[3] = 2;
                delays[4] = 1;
                delays[5] = 2;
                verboses[0] = 10;
                verboses[1] = 7;
                options.add(CrystalOptions.CAN_DIG);
                options.add(CrystalOptions.CAN_USE_KB);
                options.add(CrystalOptions.USE_BLAST_RESISTANCE);
                break;
            case GOD:
                delays[0] = 1;
                delays[1] = 1;
                delays[2] = 1;
                delays[3] = 1;
                delays[4] = 1;
                delays[5] = 1;
                verboses[0] = 3;
                verboses[1] = 1;
                options.add(CrystalOptions.CAN_DIG);
                options.add(CrystalOptions.CAN_USE_KB);
                options.add(CrystalOptions.USE_BLAST_RESISTANCE);
                break;
        }

        update();
    }

    public void spawnBot() {
        Bot bot = new Bot(player.getName() + "'s CrystalBot", kit, options, guiId);

        bot.CRYSTAL_PLACE_TICK_DELAY = delays[0];
        bot.CRYSTAL_BREAK_TICK_DELAY = delays[1];
        bot.ANCHOR_PLACE_TICK_DELAY = delays[2];
        bot.ANCHOR_BREAK_TICK_DELAY = delays[3];
        bot.MAIN_LOGIC_LOOP_DELAY = delays[4];
        bot.checkTotemMax = delays[5];

        bot.digVerboseMax = verboses[0];
        bot.anchorVerboseMax = verboses[1];

        bot.totemAmount = totemAmount;
        bot.gappleStacks = gappleStacks;

        bot.createNPC();

        bot.spawn(spawnLocation);

        if(skin != null)
            bot.getNPC().getOrAddTrait(SkinTrait.class).setSkinName(skin);


        for (Player online : Bukkit.getOnlinePlayers()) {
            online.hidePlayer(Main.Instance, (Player)bot.getNPCAsEntity());
            Bukkit.getScheduler().runTaskLater(Main.Instance, () -> online.showPlayer(Main.Instance, (Player)bot.getNPCAsEntity()), 1);
        }

        bot.loadKit();

        BotManager.addAndLoopBot(bot, player);

        player.closeInventory();

        GuiManager.removeAfterLoop(this);
    }

    public void selectPreset(CrystalPreset preset) {
        selectedPreset = preset;

        switch(preset) {
            case FLAT:
                delays[2] = Integer.MAX_VALUE;
                delays[3] = Integer.MAX_VALUE;
                verboses[1] = Integer.MAX_VALUE;
                gappleStacks = 2;
                break;
            case NORMAL:
                setDifficulty(selectedDifficulty);
                gappleStacks = 2;
                break;
            case DRAIN:
                gappleStacks = 0;
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

    public ItemStack create(Material mat, String name, List<String> lore) {
        ItemStack stack = new ItemStack(mat);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        stack.setItemMeta(meta);

        return stack;
    }
}
