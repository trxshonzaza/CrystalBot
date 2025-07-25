package trxsh.ontop.crystalbot.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import trxsh.ontop.crystalbot.Main;

public class SetSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {

        if(sender instanceof Player) {

            Main.spawnLocation = ((Player) sender).getLocation();
            sender.sendMessage("location set.");

            return true;

        }

        return false;

    }

}
