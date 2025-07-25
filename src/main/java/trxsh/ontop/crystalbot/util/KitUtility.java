package trxsh.ontop.crystalbot.util;

import trxsh.ontop.crystalbot.bot.Bot;
import trxsh.ontop.crystalbot.kit.CrystalKit;
import trxsh.ontop.crystalbot.option.CrystalOptions;

public class KitUtility {

    public static void loadKit(CrystalKit kit, Bot bot) {

        if(bot.getNPC() == null)
            throw new NullPointerException("CrystalBot NPC cannot be null!");

        switch(kit) {

            case NORMAL:
                InventoryUtility.loadNetherite(bot);
                break;
            case DIAMOND_PVP:
                InventoryUtility.loadDiamond(bot);
                break;

        }

        InventoryUtility.loadSword(bot);
        InventoryUtility.loadGapples(bot, bot.gappleStacks);
        InventoryUtility.loadPearls(bot, 4);
        InventoryUtility.loadTotems(bot, bot.totemAmount);
        InventoryUtility.loadCrystalsAndObsidian(bot, 2, 2);
        InventoryUtility.loadAnchorsAndGlowstone(bot, 2, 2);

        if(bot.getOptions().contains(CrystalOptions.CAN_DIG))
            InventoryUtility.loadPickaxe(bot);

    }

}
