package io.github.millymilo000.milySMPPlugin.listeners;

import io.github.millymilo000.milySMPPlugin.BorderManager;
import io.github.millymilo000.milySMPPlugin.utils.BorderTeleportUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class BorderListener implements Listener {

    final private BorderManager borderManager;
    final private FileConfiguration config;

    public BorderListener(BorderManager borderManager, FileConfiguration config) {
        this.borderManager = borderManager;
        this.config = config;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location newLoc = event.getTo();
        Player player = event.getPlayer();

        List<Integer> borderSize = config.getIntegerList("border.size");
        if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL) && (newLoc.x() > borderSize.getFirst() || newLoc.x() < borderSize.getFirst() * -1 || newLoc.z() > borderSize.getLast() || newLoc.z() < borderSize.getLast() * -1) && !borderManager.checkUser(player.getUniqueId())) {
            event.setCancelled(true);
            TextComponent payReq = Component.text(String.format("Run \"/pay\" to pay %d wheat and pass the border", borderManager.getPrice(player.getUniqueId())))
                .color(NamedTextColor.DARK_AQUA)
                .decorate(TextDecoration.UNDERLINED)
                .clickEvent(ClickEvent.suggestCommand("/pay 0-99"));
            player.sendMessage(payReq);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Check if the player is outside the border when they shouldn't be
        Player player = event.getPlayer();
        Location plrLoc = player.getLocation();
        List<Integer> borderSize = config.getIntegerList("border.size");
        if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL) && (plrLoc.x() > borderSize.getFirst() || plrLoc.x() < borderSize.getFirst() * -1 || plrLoc.z() > borderSize.getLast() || plrLoc.z() < borderSize.getLast() * -1)  && !borderManager.checkUser(player.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_AQUA + "You were last outside of the border, and your time has since ran out so you were teleported back inside.");
            player.teleport(BorderTeleportUtil.getNearestInsideLoc(player, config.getIntegerList("border.size")));
        }
    }

    @EventHandler
    public void onDimensionChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        Location plrLoc = player.getLocation();
        List<Integer> borderSize = config.getIntegerList("border.size");

        if (player.getWorld().getEnvironment().equals(World.Environment.NORMAL) && (plrLoc.x() > borderSize.getFirst() || plrLoc.x() < borderSize.getFirst() * -1 || plrLoc.z() > borderSize.getLast() || plrLoc.z() < borderSize.getLast() * -1)  && !borderManager.checkUser(player.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_AQUA + "You were teleported outside of the border, but you haven't paid to get out, so you've been teleported back inside.");
            player.teleport(BorderTeleportUtil.getNearestInsideLoc(player, config.getIntegerList("border.size")));
        }
    }
}
