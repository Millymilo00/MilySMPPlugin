package io.github.millymilo000.milySMPPlugin.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class BorderTeleportUtil {
    public static Location getNearestInsideLoc(Player player, List<Integer> borderSize) {
        Location plrLoc = player.getLocation();
        int newX = (int) plrLoc.x();
        int newZ = (int) plrLoc.z();
        if (newX > borderSize.getFirst()) {
            newX = borderSize.getFirst() - 50;
        } else if (newX < borderSize.getFirst() * -1) {
            newX = borderSize.getFirst() * -1 + 50;
        }
        if (newZ > borderSize.getLast()) {
            newZ = borderSize.getLast() - 50;
        } else if (newZ < borderSize.getLast() * -1) {
            newZ = borderSize.getLast() * -1 + 50;
        }
        return player.getWorld().getHighestBlockAt(newX, newZ).getLocation();
    }
}
