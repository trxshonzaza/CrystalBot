package trxsh.ontop.crystalbot.manager;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import trxsh.ontop.crystalbot.Main;
import trxsh.ontop.crystalbot.bot.Bot;

import java.util.ArrayList;
import java.util.List;

public class BotManager {
    private static final List<Bot> bots = new ArrayList<>();

    public static List<Bot> getBots() {
        return bots;
    }

    public static Bot getBot(int index) {
        return bots.get(index);
    }

    public static void stopBot(Bot bot) {
        synchronized (bots) {
            bot.getNPCAsEntity().damage(1000);
            bot.getNPC().destroy();
            bots.remove(bot);
        }
    }

    public static void stopBot(Player player) {
        Bot targetBot = null;

        synchronized (bots) {
            for(Bot bot : bots) {
                if(bot.owner.getUniqueId().equals(player.getUniqueId())) {
                    targetBot = bot;
                    break;
                }
            }
        }

        if(targetBot != null)
            stopBot(targetBot);
        else
            throw new RuntimeException("Bot not found");
    }

    public static Bot getBot(Player player) {
        Bot targetBot = null;

        synchronized (bots) {
            for(Bot bot : bots) {
                if(bot.owner.getUniqueId().equals(player.getUniqueId())) {
                    targetBot = bot;
                    break;
                }
            }
        }

        if(targetBot != null)
            return targetBot;

        return null;
    }

    public static Bot getBotFromNPC(NPC npc) {
        for(Bot bot : bots) {
            if(bot.getNPC().getId() == npc.getId()) {
                return bot;
            }
        }

        return null;
    }

    public static boolean hasBot(Player player) {
        for(Bot bot : bots) {
            if(bot.owner.getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }

        return false;
    }

    public static void addAndLoopBot(Bot bot, Player player) {
        if(hasBot(player)) {
            player.sendMessage(ChatColor.RED + "You can only have one bot at a time. to remove the previous bot use /removebot");
            bot.removeRequest = true;
            return;
        }

        bots.add(bot);
        bot.owner = player;

        player.sendMessage("Bot Spawned. Have Fun! :)");

        new BukkitRunnable() {
            @Override
            public void run() {
                if(bot.getNPC() == null || bot.getNPCAsEntity() == null) {
                    bot.removeRequest = true;
                }

                if(bot.removeRequest) {
                    this.cancel();
                    bot.getNPC().destroy();
                    CitizensAPI.getNPCRegistry().deregister(bot.getNPC());
                    bots.remove(bot);
                    return;
                }

                bot.runLogic();
            }
        }.runTaskTimer(Main.Instance, bot.MAIN_LOGIC_LOOP_DELAY, bot.MAIN_LOGIC_LOOP_DELAY);
    }

}
