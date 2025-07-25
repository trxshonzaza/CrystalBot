package trxsh.ontop.crystalbot.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import trxsh.ontop.crystalbot.Main;
import trxsh.ontop.crystalbot.bot.Bot;
import trxsh.ontop.crystalbot.manager.BotManager;
import trxsh.ontop.crystalbot.kit.CrystalKit;
import trxsh.ontop.crystalbot.option.CrystalOptions;

import java.util.ArrayList;
import java.util.UUID;

public class SpawnNPC implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        if(sender instanceof Player) {

            Bot bot = new Bot("CrystalBot (dev by trxsh)", CrystalKit.DIAMOND_PVP, new ArrayList<>(), UUID.randomUUID());

            //bot.addOption(CrystalOptions.CAN_USE_KB);
            bot.addOption(CrystalOptions.USE_BLAST_RESISTANCE);
            bot.addOption(CrystalOptions.CAN_DIG);

            bot.createNPC();
            bot.spawn(Main.spawnLocation);
            bot.loadKit();

            sender.sendMessage("summoned. running logic.");

            BotManager.addAndLoopBot(bot, (Player)sender);

            return true;

        }

        return false;

    }

}
