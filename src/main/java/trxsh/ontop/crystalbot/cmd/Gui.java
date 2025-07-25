package trxsh.ontop.crystalbot.cmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import trxsh.ontop.crystalbot.Main;
import trxsh.ontop.crystalbot.manager.GuiManager;

public class Gui implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if(sender instanceof Player) {
            ((Player) sender).openInventory(GuiManager.createGUI((Player)sender).getInventory());

            return true;

        }

        return false;
    }
}
