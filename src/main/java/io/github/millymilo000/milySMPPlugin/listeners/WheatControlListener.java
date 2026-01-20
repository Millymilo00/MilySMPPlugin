package io.github.millymilo000.milySMPPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WheatControlListener implements Listener {

    @EventHandler
    public void onInteract (PlayerInteractEvent event) {
        // Disable bonemeal on wheat seeds
        if (event.hasItem() && event.getItem().getType().equals(Material.BONE_MEAL)) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType().equals(Material.WHEAT)) {
                event.setCancelled(true);
            }
        }
    }
}
