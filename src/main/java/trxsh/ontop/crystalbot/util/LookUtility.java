package trxsh.ontop.crystalbot.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LookUtility {

    public static Vector getDirectionBetween(Location a, Location b) {

       return b.clone().add(0, 1, 0).toVector().subtract(a.clone().add(0, 1, 0).toVector()).normalize();

    }

}
