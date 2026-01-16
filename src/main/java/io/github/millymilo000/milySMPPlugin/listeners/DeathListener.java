package io.github.millymilo000.milySMPPlugin.listeners;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.espi.protectionstones.PSPlayer;
import dev.espi.protectionstones.PSRegion;
import io.github.millymilo000.milySMPPlugin.MilySMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.List;

public class DeathListener implements Listener {

    private final MilySMPPlugin plugin;
    private final Flag<String> greetActFlag;
    private final Flag<String> farewellActFlag;

    public DeathListener(MilySMPPlugin plugin) {
        this.plugin = plugin;
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        // ProtectionStones should load before this plugin, so this should always exist
        this.greetActFlag = (Flag<String>) registry.get("greeting-action");
        this.farewellActFlag = (Flag<String>) registry.get("farewell-action");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        PSPlayer psPlayer = PSPlayer.fromPlayer(event.getPlayer());
        List<PSRegion> regions = psPlayer.getPSRegions(event.getPlayer().getWorld(), true);

        for (PSRegion region : regions) {
            ProtectedRegion protectedRegion = region.getWGRegion();

            protectedRegion.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
            protectedRegion.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);

            String name = plugin.getServer().getOfflinePlayer(region.getOwners().getFirst()).getName();

            protectedRegion.setFlag(greetActFlag, "&lEntering &b&l" + name + "'s &4&lUN&fprotected area");
            protectedRegion.setFlag(farewellActFlag, "&lLeaving &b&l" + name + "'s &4&lUN&fprotected area");
        }

        // Wait 10 minutes then set it back to normal
        plugin.getServer().getScheduler().runTaskLater(plugin, task -> {
            for (PSRegion region : regions) {
                ProtectedRegion protectedRegion = region.getWGRegion();

                protectedRegion.setFlag(Flags.BUILD, StateFlag.State.DENY);
                protectedRegion.setFlag(Flags.INTERACT, StateFlag.State.DENY);

                String name = plugin.getServer().getOfflinePlayer(region.getOwners().getFirst()).getName();

                protectedRegion.setFlag(greetActFlag, "&lEntering &b&l" + name + "'s &f&lprotected area");
                protectedRegion.setFlag(farewellActFlag, "&lLeaving &b&l" + name + "'s &f&lprotected area");
            }
        }, 12000);
    }
}
