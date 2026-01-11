package io.github.millymilo000.milySMPPlugin;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.UUID;

public class BorderManager {
    final private HashMap<UUID, LocalTime> payingPlayers = new HashMap<UUID, LocalTime>();
    final private HashMap<UUID, Integer> prices = new HashMap<UUID, Integer>();

    public BorderManager(MilySMPPlugin plugin) {
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
        // TODO: replace this with a config thingy
        int increaseAmt = 10;

        // Increase price
        if (prices.containsKey(uuid)) {
            prices.replace(uuid, prices.get(uuid) + increaseAmt * hours);
            return;
        }
        prices.put(uuid, increaseAmt * hours + 10);
    }

    public boolean checkUser(UUID uuid) {
        return payingPlayers.containsKey(uuid);
    }

    public int getPrice(UUID uuid) {
        if (prices.containsKey(uuid)) {
            return prices.get(uuid);
        }
        return 10;
    }
}
