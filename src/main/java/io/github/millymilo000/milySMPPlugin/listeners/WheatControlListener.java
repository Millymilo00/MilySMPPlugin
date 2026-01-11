package io.github.millymilo000.milySMPPlugin.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WheatControlListener implements Listener {

    private final FileConfiguration config;

    public WheatControlListener(FileConfiguration config) {
        this.config = config;
    }

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
    public void onGrow (BlockGrowEvent event) {
        // Slow grow speed
        if (event.getBlock().getType().equals(Material.WHEAT_SEEDS) || event.getBlock().getType().equals(Material.WHEAT)) {
            double chance = config.getInt("wheat-growth-rate");
            // If chance is 0.9, then it's effectively a 90% chance for wheat to grow
            if (Math.random() > chance) {
                event.setCancelled(true);
            }
        }
    }
}
