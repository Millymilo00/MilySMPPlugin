package io.github.millymilo000.milySMPPlugin.listeners;

import io.github.millymilo000.milySMPPlugin.BorderManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/*
* TODO: Make world border editable in config
*/

public class BorderListener implements Listener {

    final private BorderManager borderManager;

    public BorderListener(BorderManager borderManager) {
        this.borderManager = borderManager;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location newLoc = event.getTo();
        Player player = event.getPlayer();

        if (newLoc.x() > 800 || newLoc.x() < -800 || newLoc.z() > 800 || newLoc.z() < -800) {
            if (borderManager.checkUser(player.getUniqueId())) {
                return;
            }
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
        if ((plrLoc.x() > 800 || plrLoc.x() < -800 || plrLoc.z() > 800 || plrLoc.z() < -800) && borderManager.checkUser(player.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_AQUA + "You were last outside of the border, and your time has since ran out so you were teleported back inside.");
            // If they have a respawnLocation send em there! Otherwise, send them to the world spawn.
            // I was thinking maybe they should be sent to the closest spot inside the border to them, but ah, that'd be annoying with y coords and such
            if (player.getRespawnLocation() instanceof Location spawnLoc) {
                player.teleport(spawnLoc);
            } else {
                player.teleport(player.getWorld().getSpawnLocation());
            }
        }
    }
}
