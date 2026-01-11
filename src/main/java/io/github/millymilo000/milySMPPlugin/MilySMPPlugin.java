package io.github.millymilo000.milySMPPlugin;

import io.github.millymilo000.milySMPPlugin.commands.PayBorderCommand;
import io.github.millymilo000.milySMPPlugin.listeners.BorderListener;
import io.github.millymilo000.milySMPPlugin.listeners.PayGuiListener;
import io.github.millymilo000.milySMPPlugin.listeners.WheatControlListener;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class MilySMPPlugin extends JavaPlugin {

    public FileConfiguration config = getConfig(); // TODO: Maybe use a custom config api so I can have comments
    private BorderManager borderManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupConfig(config);
        config.options().copyDefaults(true);
        saveConfig();

        this.borderManager = new BorderManager(this);
        getServer().getPluginManager().registerEvents(new BorderListener(borderManager, config), this);
        getServer().getPluginManager().registerEvents(new PayGuiListener(this, borderManager), this);
        getServer().getPluginManager().registerEvents(new WheatControlListener(config), this);
        getCommand("pay").setExecutor(new PayBorderCommand(this, borderManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Todo: Add saving the bordermanager paying people here and prices
    }

    private static void setupConfig(FileConfiguration config) {
        HashMap<String, Object> borderConfigs = new HashMap<>();
        borderConfigs.put("size", new int[] {800, 800});
        borderConfigs.put("initial-cross-price", 10);
        borderConfigs.put("cross-price-increase", 10);
        borderConfigs.put("max-price", 15552); // A full single chest worth of haybales

        config.addDefault("border", borderConfigs);
        config.addDefault("wheat-growth-rate", 0.9);
    }
}
