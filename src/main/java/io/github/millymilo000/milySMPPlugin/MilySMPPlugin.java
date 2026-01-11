package io.github.millymilo000.milySMPPlugin;

import io.github.millymilo000.milySMPPlugin.commands.PayBorderCommand;
import io.github.millymilo000.milySMPPlugin.listeners.BorderListener;
import io.github.millymilo000.milySMPPlugin.listeners.PayGuiListener;
import io.github.millymilo000.milySMPPlugin.listeners.WheatControlListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MilySMPPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        BorderManager borderManager = new BorderManager(this);
        getServer().getPluginManager().registerEvents(new BorderListener(borderManager), this);
        getServer().getPluginManager().registerEvents(new PayGuiListener(this, borderManager), this);
        getServer().getPluginManager().registerEvents(new WheatControlListener(), this);
        getCommand("pay").setExecutor(new PayBorderCommand(this, borderManager));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        // Todo: Add saving the bordermanager paying people here and prices
    }
}
