package io.github.millymilo000.milySMPPlugin.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
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

    @EventHandler
    public void onDispenser(BlockDispenseEvent event) {
        if (Material.BONE_MEAL.equals(event.getItem().getType()) && event.getBlock().getBlockData() instanceof Directional direction) {
            // Find the block its interacting with
            Material material = event.getBlock().getRelative(direction.getFacing()).getType();
            if (Material.WHEAT.equals(material)) {
                event.setCancelled(true);
            }
        }
    }
}
