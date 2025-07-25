package trxsh.ontop.crystalbot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import trxsh.ontop.crystalbot.bot.Bot;
import trxsh.ontop.crystalbot.cmd.Gui;
import trxsh.ontop.crystalbot.cmd.RemoveBot;
import trxsh.ontop.crystalbot.cmd.SetSpawn;
import trxsh.ontop.crystalbot.cmd.SpawnNPC;
import trxsh.ontop.crystalbot.event.ClickEvent;
import trxsh.ontop.crystalbot.event.DamageEvent;
import trxsh.ontop.crystalbot.manager.BotManager;
import trxsh.ontop.crystalbot.manager.GuiManager;

import java.util.ArrayList;
import java.util.Collections;

public final class Main extends JavaPlugin {

    public static Location spawnLocation = null;
    public static Main Instance = null;

    @Override
    public void onEnable() {
        // Plugin startup logic

        getCommand("setspawn").setExecutor(new SetSpawn());
        getCommand("spawnbot").setExecutor(new SpawnNPC());
        getCommand("spawngui").setExecutor(new Gui());
        getCommand("removebot").setExecutor(new RemoveBot());

        Bukkit.getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DamageEvent(), this);

        Instance = this;

    }

    @Override
    public void onDisable() {
        for(Bot bot : new ArrayList<>(BotManager.getBots())) { // prevent ConcurrentModificationException
            BotManager.stopBot(bot);
        }
    }
}
