package io.github.millymilo000.milySMPPlugin;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;

public class BorderManager {

    final private HashMap<UUID, LocalTime> payingPlayers = new HashMap<UUID, LocalTime>();
    final private HashMap<UUID, Integer> prices = new HashMap<UUID, Integer>();
    final private MilySMPPlugin plugin;

    public BorderManager(MilySMPPlugin plugin) {
        this.plugin = plugin;

        // Start a task that checks every 10 minutes if the people in payingPlayers time is up.
        // TODO: test this
        plugin.getServer().getScheduler().runTaskTimer(plugin, new CheckPayingPlayersRunnable(plugin, payingPlayers), 20, 72000);
    }

    public void addUser(UUID uuid, int hours) {
        if (payingPlayers.containsKey(uuid)) {
            //increase time
            payingPlayers.replace(uuid, payingPlayers.get(uuid).plusHours(hours));
        } else {
            payingPlayers.put(uuid, LocalTime.now().plusHours(hours));
        }
        int increaseAmt = plugin.getConfig().getInt("border.cross-price-increase");

        // Increase price
        if (prices.containsKey(uuid)) {
            prices.replace(uuid, prices.get(uuid) + increaseAmt * hours);
            return;
        }
        prices.put(uuid, increaseAmt * hours + plugin.getConfig().getInt("border.initial-cross-price"));
    }

    public boolean checkUser(UUID uuid) {
        return payingPlayers.containsKey(uuid);
    }

    public int getPrice(UUID uuid) {
        if (prices.containsKey(uuid)) {
            return prices.get(uuid);
        }
        return plugin.getConfig().getInt("border.initial-cross-price");
    }
}
