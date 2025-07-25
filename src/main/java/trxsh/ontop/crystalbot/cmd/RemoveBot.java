package trxsh.ontop.crystalbot.cmd;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import trxsh.ontop.crystalbot.bot.Bot;
import trxsh.ontop.crystalbot.manager.BotManager;

public class RemoveBot implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player) {
            if(BotManager.hasBot((Player)sender)) {

                if(args != null) {
                    if(args.length > 1) {
                        Bot bot = BotManager.getBot((Player)sender);
                        sender.sendMessage(bot.isEating + ", " + bot.isAnchoring + ", " + bot.isCrystaling + ", " + bot.isDigging + ", " + bot.pearling);
                        return true;
                    }
                }

                BotManager.stopBot((Player)sender);

                sender.sendMessage("Bot despawned.");
            } else {
                sender.sendMessage(ChatColor.RED + "Bot could not be found.");
            }

            return true;
        }

        return false;
    }
}
