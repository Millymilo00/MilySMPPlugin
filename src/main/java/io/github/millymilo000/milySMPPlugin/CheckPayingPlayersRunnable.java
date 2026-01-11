package io.github.millymilo000.milySMPPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CheckPayingPlayersRunnable implements Runnable {

    private final MilySMPPlugin plugin;
    final private HashMap<UUID, LocalTime> payingPlayers;


    public CheckPayingPlayersRunnable(MilySMPPlugin plugin, HashMap<UUID, LocalTime> payingPlayers) {
        this.plugin = plugin;
        this.payingPlayers = payingPlayers;
    }

    @Override
    public void run() {
        for (Map.Entry<UUID, LocalTime> entry : payingPlayers.entrySet()) {
            UUID uuid = entry.getKey();
            LocalTime endTime = entry.getValue();

            // Get rid of the guys who are overdue
            Player player = plugin.getServer().getPlayer(uuid);
            if (LocalTime.now().isBefore(endTime)) {
                if (player.isOnline()) { // frickk you intellij its not going to be null unless some kind of other error that should instead be fixed or if mojang deletes their account
                    player.sendMessage(ChatColor.DARK_AQUA +"Your time is up, hope you had a good exploring!");
                    // If they have a respawnLocation send em there! Otherwise, send them to the world spawn.
                    // I was thinking maybe they should be sent to the closest spot inside the border to them, but ah, that'd be annoying with y coords and such
                    if (player.getRespawnLocation() instanceof Location spawnLoc) {
                        player.teleport(spawnLoc);
                    } else {
                        player.teleport(player.getWorld().getSpawnLocation());
                    }
                }
                payingPlayers.remove(uuid);
                return;
            }
            // Remind the guys who are have 10 minutes til they gotta go
            if (LocalTime.now().minusMinutes(10).isBefore(endTime) && player.isOnline()) {
                TextComponent warning = Component.text("You have 10 minutes before you won't be able to go outside the border anymore.")
                        .color(NamedTextColor.YELLOW)
                        .appendNewline()
                        .append(
                                Component.text("You can pay for more hours to be out for longer though")
                                        .color(NamedTextColor.GOLD)
                                        .decorate(TextDecoration.UNDERLINED)
                                        .clickEvent(ClickEvent.suggestCommand("/pay 0-99"))
                        );
                player.sendMessage(warning);
            }
        }
    }
}
